package com.elit.outildedecision.collaboration.report.service.implementation;

import com.elit.outildedecision.collaboration.multicriteria.exception.MulticriteriaNotFoundException;
import com.elit.outildedecision.collaboration.predictibility.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.collaboration.report.dto.ReportResponse;
import com.elit.outildedecision.collaboration.report.entity.Report;
import com.elit.outildedecision.collaboration.report.entity.ReportContent;
import com.elit.outildedecision.collaboration.report.exception.ReportNotFoundException;
import com.elit.outildedecision.collaboration.report.mapper.ReportMapper;
import com.elit.outildedecision.collaboration.report.repository.ReportContentRepository;
import com.elit.outildedecision.collaboration.report.repository.ReportRepository;
import com.elit.outildedecision.shared.exception.ConflictException;
import com.elit.outildedecision.shared.exception.ExecutionPlanNotFoundException;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.project.repository.ProjectRepository;
import com.elit.outildedecision.shared.port.AiReportOwnershipPort;
import com.elit.outildedecision.shared.port.AuditReportOwnershipPort;
import com.elit.outildedecision.shared.port.CollaborationReportOwnershipPort;
import com.elit.outildedecision.shared.util.ReportTypes;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Regression tests for legacy HTTP values, typed persistence and report ownership. */
@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

  @InjectMocks private ReportServiceImpl reportService;
  @Mock private ReportRepository reportRepository;
  @Mock private ReportContentRepository reportContentRepository;
  @Mock private ProjectRepository projectRepository;
  @Mock private CollaborationReportOwnershipPort collaborationReportOwnershipPort;
  @Mock private ReportMapper reportMapper;
  @Mock private AiReportOwnershipPort aiReportOwnershipPort;
  @Mock private AuditReportOwnershipPort auditReportOwnershipPort;

  private final Long projectId = 1L;
  private final Long analysisId = 1L;
  private final Integer iteration = 1;
  private MockMultipartFile file;
  private Project project;

  @BeforeEach
  void setup() {
    file = new MockMultipartFile("file", "report.pdf", MediaType.APPLICATION_PDF_VALUE,
      "fixed-pdf-bytes".getBytes(StandardCharsets.UTF_8));
    project = new Project();
    project.setProjectId(projectId);
    project.setFolderPath("");
    // @Value is applied by Spring at runtime, but not by @InjectMocks in a unit test.
    ReflectionTestUtils.setField(reportService, "storingFolder", "target/test-uploads/files");
  }

  private void existingProject() {
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
  }

  private void existingMulticriteria() {
    when(collaborationReportOwnershipPort.belongsToProject(
      ReportTypes.MULTI_CRITERIA_ANALYSIS, analysisId, projectId)).thenReturn(true);
  }

  @Test
  void uploadReport2_whenReportAlreadyExists() {
    existingProject();
    existingMulticriteria();
    when(reportRepository.findReportByProjectAndAnalyseAndTypeAndIteration(
      projectId, analysisId, ReportTypes.MULTI_CRITERIA_ANALYSIS, iteration))
      .thenReturn(new Report());

    ConflictException error = assertThrows(ConflictException.class,
      () -> reportService.uploadReport2(projectId, analysisId, "multicriteria_report", file, iteration));
    assertEquals("Le rapport a déjà été généré pour cette analyse.", error.getMessage());
    verify(reportRepository, never()).save(any(Report.class));
  }

  @Test
  void uploadReport2_whenProjectNotFound() {
    when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
    ProjectNotFoundException error = assertThrows(ProjectNotFoundException.class,
      () -> reportService.uploadReport2(projectId, analysisId, "execution_report", file, iteration));
    assertEquals("Le projet avec l'ID 1 n'existe pas.", error.getMessage());
  }

  @Test
  void uploadReport2_whenMulticriteriaNotFound() {
    existingProject();
    when(collaborationReportOwnershipPort.belongsToProject(
      ReportTypes.MULTI_CRITERIA_ANALYSIS, analysisId, projectId)).thenReturn(false);
    MulticriteriaNotFoundException error = assertThrows(MulticriteriaNotFoundException.class,
      () -> reportService.uploadReport2(projectId, analysisId, "multicriteria_report", file, iteration));
    assertEquals("Cette analyse multicritère n'existe pas.", error.getMessage());
    verify(reportRepository, never()).save(any(Report.class));
  }

  @Test
  void uploadReport2_whenPredictibilityAnalysisNotFound() {
    existingProject();
    when(collaborationReportOwnershipPort.belongsToProject(
      ReportTypes.PREDICTIBILITY_ANALYSIS, analysisId, projectId)).thenReturn(false);
    PredictibilityAnalysisNotFoundException error = assertThrows(PredictibilityAnalysisNotFoundException.class,
      () -> reportService.uploadReport2(projectId, analysisId, "predictibility_report", file, iteration));
    assertEquals("Cette analyse de prévisibilité n'existe pas.", error.getMessage());
    verify(reportRepository, never()).save(any(Report.class));
  }

  @Test
  void uploadReport2_whenExecutionPlanNotFound() {
    existingProject();
    when(collaborationReportOwnershipPort.belongsToProject(
      ReportTypes.EXECUTION_PLAN, analysisId, projectId)).thenReturn(false);
    ExecutionPlanNotFoundException error = assertThrows(ExecutionPlanNotFoundException.class,
      () -> reportService.uploadReport2(projectId, analysisId, "execution_report", file, iteration));
    assertEquals("Ce plan d'exécution n'existe pas.", error.getMessage());
    verify(reportRepository, never()).save(any(Report.class));
  }

  @Test
  void uploadReport2_whenAnalysisBelongsToAnotherProject() {
    existingProject();
    when(collaborationReportOwnershipPort.belongsToProject(
      ReportTypes.MULTI_CRITERIA_ANALYSIS, analysisId, projectId)).thenReturn(false);

    assertThrows(MulticriteriaNotFoundException.class,
      () -> reportService.uploadReport2(projectId, analysisId, "multicriteria_report", file, iteration));
    verify(reportRepository, never()).save(any(Report.class));
  }

  @Test
  void uploadReport2_whenTypeIsUnknownReturnsBadRequest() {
    existingProject();
    ResponseStatusException error = assertThrows(ResponseStatusException.class,
      () -> reportService.uploadReport2(projectId, analysisId, "UNKNOWN_TYPE", file, iteration));
    assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
    verify(reportRepository, never()).save(any(Report.class));
  }

  @Test
  void uploadReport2_whenAiResultNotFound() {
    existingProject();
    when(aiReportOwnershipPort.belongsToProject(analysisId, projectId)).thenReturn(false);
    ResponseStatusException error = assertThrows(ResponseStatusException.class,
      () -> reportService.uploadReport2(projectId, analysisId, "ai_report", file, iteration));
    assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
    verify(reportRepository, never()).save(any(Report.class));
  }

  @Test
  void uploadReport2_whenAuditBelongsToAnotherProject() {
    existingProject();
    when(auditReportOwnershipPort.belongsToProject(analysisId, projectId)).thenReturn(false);
    ResponseStatusException error = assertThrows(ResponseStatusException.class,
      () -> reportService.uploadReport2(projectId, analysisId, "audit_report", file, iteration));
    assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
  }

  @Test
  void uploadReport2_acceptsAiReportForMatchingProject() throws Exception {
    existingProject();
    when(aiReportOwnershipPort.belongsToProject(analysisId, projectId)).thenReturn(true);
    when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> {
      Report report = invocation.getArgument(0);
      report.setReportId(20L);
      return report;
    });

    assertEquals("Fichier enregistré avec succès.",
      reportService.uploadReport2(projectId, analysisId, "ai_report", file, iteration));
    verify(reportRepository).save(argThat(report -> report.getType() == ReportTypes.AI_ANALYSIS));
  }

  @Test
  void uploadReport2_acceptsAuditForMatchingProject() throws Exception {
    existingProject();
    when(auditReportOwnershipPort.belongsToProject(analysisId, projectId)).thenReturn(true);
    when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> {
      Report report = invocation.getArgument(0);
      report.setReportId(21L);
      return report;
    });

    assertEquals("Fichier enregistré avec succès.",
      reportService.uploadReport2(projectId, analysisId, "audit_report", file, iteration));
    verify(reportRepository).save(argThat(report -> report.getType() == ReportTypes.AUDIT));
  }

  @Test
  void uploadReport2_acceptsExecutionPlanLinkedToProject() throws Exception {
    existingProject();
    when(collaborationReportOwnershipPort.belongsToProject(
      ReportTypes.EXECUTION_PLAN, analysisId, projectId)).thenReturn(true);
    when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> {
      Report report = invocation.getArgument(0);
      report.setReportId(22L);
      return report;
    });

    assertEquals("Fichier enregistré avec succès.",
      reportService.uploadReport2(projectId, analysisId, "execution_report", file, iteration));
    verify(reportRepository).save(argThat(report -> report.getType() == ReportTypes.EXECUTION_PLAN));
  }

  @Test
  void uploadReport2_rejectsBlankFilename() {
    existingProject();
    existingMulticriteria();
    MockMultipartFile noName = new MockMultipartFile("file", "", MediaType.APPLICATION_PDF_VALUE,
      "bytes".getBytes(StandardCharsets.UTF_8));

    ResponseStatusException error = assertThrows(ResponseStatusException.class,
      () -> reportService.uploadReport2(projectId, analysisId, "multicriteria_report", noName, iteration));
    assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
    verify(reportRepository, never()).save(any(Report.class));
  }

  @Test
  void uploadReport2_savesReportAndBinaryInDatabase() throws Exception {
    existingProject();
    existingMulticriteria();
    when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> {
      Report report = invocation.getArgument(0);
      report.setReportId(7L);
      return report;
    });

    assertEquals("Fichier enregistré avec succès.",
      reportService.uploadReport2(projectId, analysisId, "multicriteria_report", file, iteration));
    verify(reportRepository).save(argThat(report -> report.getType() == ReportTypes.MULTI_CRITERIA_ANALYSIS
      && report.getProject() == project && report.getAnalyseId().equals(analysisId)));
    byte[] expectedBytes = file.getBytes();
    verify(reportContentRepository).save(argThat(content -> Long.valueOf(7L).equals(content.getReportId())
      && java.util.Arrays.equals(expectedBytes, content.getContent())));
  }

  @Test
  void updateReport2_doesNotDependOnProjectReportsBeingLoaded() throws Exception {
    existingProject();
    existingMulticriteria();
    Report previous = new Report();
    previous.setReportId(7L);
    previous.setProject(project);
    previous.setType(ReportTypes.MULTI_CRITERIA_ANALYSIS);
    when(reportRepository.findReportByProjectAndAnalyseAndTypeAndIteration(
      projectId, analysisId, ReportTypes.MULTI_CRITERIA_ANALYSIS, iteration))
      .thenReturn(previous);

    assertEquals("Le fichier a été mis à jours avec succès.",
      reportService.uploadAndUpdateReport2(projectId, analysisId, "multicriteria_report", file, iteration));
    verify(reportRepository).save(previous);
    verify(reportContentRepository).save(argThat(content -> Long.valueOf(7L).equals(content.getReportId())));
  }

  @Test
  void previewReport_whenReportNotFound() {
    when(reportRepository.findById(1L)).thenReturn(Optional.empty());
    ReportNotFoundException error = assertThrows(ReportNotFoundException.class,
      () -> reportService.previewReport(1L, new MockHttpServletResponse()));
    assertEquals("Le rapport avec l'ID 1 n'existe pas.", error.getMessage());
  }

  @Test
  void downloadReport_whenReportNotFound() {
    when(reportRepository.findById(1L)).thenReturn(Optional.empty());
    ReportNotFoundException error = assertThrows(ReportNotFoundException.class,
      () -> reportService.downloadReport(1L, new MockHttpServletResponse()));
    assertEquals("Le rapport avec l'ID 1 n'existe pas.", error.getMessage());
  }

  @Test
  void downloadReport_streamsAttachment() throws Exception {
    Report report = new Report();
    report.setReportId(7L);
    report.setReportPath("target/test-uploads/files/report.pdf");
    byte[] bytes = "binary".getBytes(StandardCharsets.UTF_8);
    when(reportRepository.findById(7L)).thenReturn(Optional.of(report));
    when(reportContentRepository.findById(7L)).thenReturn(Optional.of(new ReportContent(7L, bytes)));
    MockHttpServletResponse response = new MockHttpServletResponse();

    reportService.downloadReport(7L, response);
    assertEquals("attachment; filename=\"report.pdf\"", response.getHeader("Content-Disposition"));
    assertArrayEquals(bytes, response.getContentAsByteArray());
    assertEquals(MediaType.APPLICATION_PDF_VALUE, response.getContentType());
  }

  @Test
  void getReportsByProjectId() throws ProjectNotFoundException {
    existingProject();
    when(reportRepository.findReportsByProjectId(projectId)).thenReturn(List.of());
    assertEquals(List.<ReportResponse>of(), reportService.getReportsByProjectId(projectId));
  }

  @Test
  void getReportsByProjectId_whenProjectNotFound() {
    when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
    ProjectNotFoundException error = assertThrows(ProjectNotFoundException.class,
      () -> reportService.getReportsByProjectId(projectId));
    assertEquals("Le projet avec l'ID 1 n'existe pas.", error.getMessage());
  }

  @Test
  void deleteReportById() throws ReportNotFoundException {
    Report report = new Report();
    report.setReportId(7L);
    when(reportRepository.findById(7L)).thenReturn(Optional.of(report));
    when(reportContentRepository.existsById(7L)).thenReturn(true);

    assertEquals("Fichier supprimé avec succès", reportService.deleteReportById(7L));
    verify(reportContentRepository, times(1)).deleteById(7L);
    verify(reportRepository, times(1)).deleteById(7L);
  }
}
