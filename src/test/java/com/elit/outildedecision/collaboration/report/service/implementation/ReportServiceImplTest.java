package com.elit.outildedecision.collaboration.report.service.implementation;

import com.elit.outildedecision.collaboration.executionplan.repository.ExecutionPlanRepository;
import com.elit.outildedecision.collaboration.multicriteria.exception.MulticriteriaNotFoundException;
import com.elit.outildedecision.collaboration.multicriteria.repository.MultiCriteriaAnalysisRepository;
import com.elit.outildedecision.collaboration.predictibility.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.collaboration.predictibility.repository.PredictibilityAnalysisRepository;
import com.elit.outildedecision.collaboration.report.dto.ReportResponse;
import com.elit.outildedecision.collaboration.report.entity.Report;
import com.elit.outildedecision.collaboration.report.exception.ReportNotFoundException;
import com.elit.outildedecision.collaboration.report.mapper.ReportMapper;
import com.elit.outildedecision.collaboration.report.repository.ReportContentRepository;
import com.elit.outildedecision.collaboration.report.repository.ReportRepository;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.exception.ConflictException;
import com.elit.outildedecision.shared.exception.ExecutionPlanNotFoundException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.project.repository.ProjectRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {
  @InjectMocks
  private ReportServiceImpl reportService;
  @Mock
  ReportRepository reportRepository;
  @Mock
  ReportContentRepository reportContentRepository;
  @Mock
  ProjectRepository projectRepository;
  @Mock
  ExecutionPlanRepository executionPlanRepository;
  @Mock
  PredictibilityAnalysisRepository predictibilityAnalysisRepository;
  @Mock
  MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository;
  @Mock
  ReportMapper reportMapper;
  private MultipartFile fileMultipart;
  private Long projectId, analysisId;
  Integer iteration;
  private String type;
  @TempDir
  File tempDir;

  @BeforeEach
  public void setup() {
    fileMultipart = new MockMultipartFile(
      "file",
      "report.pdf",
      MediaType.APPLICATION_PDF_VALUE,
      "<<pdf data>>".getBytes(StandardCharsets.UTF_8));
    projectId = 1L;
    analysisId = 1L;
    iteration = 1;
    type = "type";
  }

  @Test
  void uploadReport2_whenRepportAlreadyExists() {
    type = "execution_report";
    Integer localIteration = 1;
    Report report = new Report();

    when(reportRepository.findReportByAnalyseIdAndType2(analysisId, type, localIteration)).thenReturn(report);
    ConflictException e = Assertions.assertThrows(ConflictException.class, () -> reportService.uploadReport2(projectId, analysisId, type, fileMultipart, localIteration));
    Assertions.assertEquals("Le rapport a déjà été généré pour cette analyse.", e.getMessage());
  }

  @Test
  void uploadReport2_whenProjectNotFound() {
    when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
    ProjectNotFoundException e = Assertions.assertThrows(ProjectNotFoundException.class, () -> reportService.uploadReport2(projectId, analysisId, type, fileMultipart, iteration));
    Assertions.assertEquals("Le projet avec l'ID " + projectId + " n'existe pas.", e.getMessage());
  }

  @Test
  void uploadReport2_whenMulticriteriaNotFound() {
    type = "multicriteria_report";
    Project project = new Project();
    project.setProjectId(projectId);
    project.setFolderPath("projects");

    when(reportRepository.findReportByAnalyseIdAndType2(analysisId, type, iteration)).thenReturn(null);
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    when(multiCriteriaAnalysisRepository.existsById(analysisId)).thenReturn(false);
    MulticriteriaNotFoundException e = Assertions.assertThrows(MulticriteriaNotFoundException.class, () -> reportService.uploadReport2(projectId, analysisId, type, fileMultipart, iteration));
    Assertions.assertEquals("Cette analyse multicritère n'existe pas.", e.getMessage());
  }

  @Test
  void uploadReport2_whenPredictibilityAnalysisNotFound() {
    type = "predictibility_report";
    iteration = 1;
    Project project = new Project();
    project.setProjectId(projectId);
    project.setFolderPath("projects");

    when(reportRepository.findReportByAnalyseIdAndType2(analysisId, type, iteration)).thenReturn(null);
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    when(predictibilityAnalysisRepository.existsById(analysisId)).thenReturn(false);
    PredictibilityAnalysisNotFoundException e = Assertions.assertThrows(PredictibilityAnalysisNotFoundException.class, () -> reportService.uploadReport2(projectId, analysisId, type, fileMultipart, iteration));
    Assertions.assertEquals("Cette analyse de prévisibilité n'existe pas.", e.getMessage());
  }

  @Test
  void uploadReport2_whenExecutionPlanNotFound() {
    type = "execution_report";
    iteration = 1;
    Project project = new Project();
    project.setProjectId(projectId);
    project.setFolderPath("projects");

    when(reportRepository.findReportByAnalyseIdAndType2(analysisId, type, iteration)).thenReturn(null);
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    when(executionPlanRepository.existsById(analysisId)).thenReturn(false);
    ExecutionPlanNotFoundException e = Assertions.assertThrows(ExecutionPlanNotFoundException.class, () -> reportService.uploadReport2(projectId, analysisId, type, fileMultipart, iteration));
    Assertions.assertEquals("Ce plan d'exécution n'existe pas.", e.getMessage());
  }

  @Test
  void previewReport_whenReportNotFound() {
    Long localReportId = 1L;
    HttpServletResponse mockResponse = mock(HttpServletResponse.class);
    when(reportRepository.findById(localReportId)).thenReturn(Optional.empty());
    ReportNotFoundException e = Assertions.assertThrows(ReportNotFoundException.class, () -> reportService.previewReport(localReportId, mockResponse));
    Assertions.assertEquals("Le rapport avec l'ID " + localReportId + " n'existe pas.", e.getMessage());
  }

  @Test
  void downloadReport_whenReportNotFound() {
    Long localReportId = 1L;
    HttpServletResponse mockResponse = mock(HttpServletResponse.class);
    when(reportRepository.findById(localReportId)).thenReturn(Optional.empty());
    ReportNotFoundException p = Assertions.assertThrows(ReportNotFoundException.class, () -> reportService.downloadReport(localReportId, mockResponse));
    Assertions.assertEquals("Le rapport avec l'ID " + localReportId + " n'existe pas.", p.getMessage());
  }

  @Test
  void getReportsByProjectId() throws ProjectNotFoundException {
    projectId = 1L;
    Project project = new Project();
    project.setProjectId(projectId);
    List<Report> reportsList = new ArrayList<>();

    List<ReportResponse> reportstModelList = new ArrayList<>();

    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    when(reportRepository.findReportsByProjectId(project.getProjectId())).thenReturn(reportsList);
    assertEquals(reportstModelList, reportService.getReportsByProjectId(projectId));
  }

  @Test
  void getReportsByProjectId_whenProjectNotFound() {
    projectId = 1L;
    Project project = new Project();
    project.setProjectId(projectId);
    when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
    ProjectNotFoundException p = Assertions.assertThrows(ProjectNotFoundException.class, () -> reportService.getReportsByProjectId(projectId));
    Assertions.assertEquals("Le projet avec l'ID 1 n'existe pas.", p.getMessage());
  }

  @Test
  void deleteReportById() throws ReportNotFoundException {
    Report report = new Report();
    report.setReportId(1L);
    report.setReportPath(String.valueOf(tempDir));
    when(reportRepository.findById(report.getReportId())).thenReturn(Optional.of(report));
    reportRepository.deleteById(report.getReportId());
    verify(reportRepository, times(1)).deleteById(report.getReportId());

    assertEquals("Fichier supprimé avec succès", reportService.deleteReportById(report.getReportId()));
  }
}
