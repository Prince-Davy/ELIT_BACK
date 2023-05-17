package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.ExecutionPlan;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.Report;
import com.elit.outildedecision.exception.*;
import com.elit.outildedecision.model.ReportResponseModel;
import com.elit.outildedecision.repository.*;
import com.elit.outildedecision.service.implementation.ReportServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {
  @InjectMocks
  private ReportServiceImpl reportService;
  @Mock
  ReportRepository reportRepository;
  @Mock
  ProjectRepository projectRepository;
  @Mock
  ExecutionPlanRepository executionPlanRepository;
  @Mock
  PredictibilityAnalysisRepository predictibilityAnalysisRepository;
  @Mock
  MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository;
  private MultipartFile fileMultipart;
  private Long projectId, analysisId, reportId;
  Integer iteration;
  private String type;
  final private String storingFolder = ".\\outildedecision\\src\\main\\resources\\projects\\";
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
    reportId = 1L;
    iteration = 1;
    type = "type";
  }


// probleme de variable d'environnement FILE_UPLOAD_DIR ou  @Value("${file.upload-dir}")
 /* @Test
  void uploadReport() throws ProjectNotFoundException, MulticriteriaNotFoundException, ExecutionPlanNotFoundException, FileCouldNotBeSaved, PredictibilityAnalysisNotFoundException, IOException {
    type = "execution_report";
    Project project = new Project();
    project.setProjectId(projectId);
    project.setFolderPath("reports");

    Report report =new Report();
    report.setReportId(1L);
    report.setType(type);
    report.setAnalyseId(analysisId);
    report.setProject(project);
    report.setReportPath(String.valueOf(tempDir));

    ExecutionPlan executionPlan = new ExecutionPlan();
    executionPlan.setExecutionIteration(1);

    when(reportRepository.findReportByAnalyseIdAndType(analysisId, type)).thenReturn(null);
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    when(executionPlanRepository.findById(analysisId)).thenReturn(Optional.of(executionPlan));
    assertEquals(ResponseEntity.ok().body("Fichier enregistré avec succès."),reportService.uploadReport(projectId,analysisId,type,fileMultipart));

  }*/

  @Test
  void uploadReport_whenFileCouldNotBeSaved() {
    type = "execution_report";
    Project project = new Project();
    project.setProjectId(projectId);

    ExecutionPlan executionPlan = new ExecutionPlan();
    executionPlan.setExecutionIteration(1);

    when(reportRepository.findReportByAnalyseIdAndType(analysisId, type)).thenReturn(null);
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    when(executionPlanRepository.findById(analysisId)).thenReturn(Optional.of(executionPlan));
    FileCouldNotBeSaved e = Assertions.assertThrows(FileCouldNotBeSaved.class, () -> reportService.uploadReport(projectId, analysisId, type, fileMultipart));
    assertEquals("Erreur lors de l'enregistrement du fichier : " + fileMultipart.getOriginalFilename(), e.getMessage());

  }

  @Test
  void uploadReport_whenRepportAlreadyExists() throws ProjectNotFoundException, MulticriteriaNotFoundException, ExecutionPlanNotFoundException, FileCouldNotBeSaved, PredictibilityAnalysisNotFoundException {
    type = "execution_report";
    Project project = new Project();
    project.setProjectId(projectId);
    project.setFolderPath("projects");

    Report report = new Report();
    report.setReportId(1L);
    report.setType(type);
    report.setAnalyseId(analysisId);
    report.setProject(project);
    report.setReportPath(String.valueOf(tempDir));

    when(reportRepository.findReportByAnalyseIdAndType(analysisId, type)).thenReturn(report);

    assertEquals(ResponseEntity.status(HttpStatus.CONFLICT).body("Le rapport a déjà été généré pour cette analyse."), reportService.uploadReport(projectId, analysisId, type, fileMultipart));

  }

  @Test
  void uploadReport_whenProjectNotFound() {
    when(reportRepository.findReportByAnalyseIdAndType(analysisId, type)).thenReturn(null);
    when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
    ProjectNotFoundException e = Assertions.assertThrows(ProjectNotFoundException.class, () -> reportService.uploadReport(projectId, analysisId, type, fileMultipart));
    Assertions.assertEquals("Le projet avec l'ID " + projectId + " n'existe pas.", e.getMessage());
  }

  @Test
  void uploadReport_whenMulticriteriaNotFound() {
    type = "multicriteria_report";
    Project project = new Project();
    project.setProjectId(projectId);
    project.setFolderPath("projects");

    when(reportRepository.findReportByAnalyseIdAndType(analysisId, type)).thenReturn(null);
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    when(multiCriteriaAnalysisRepository.findById(analysisId)).thenReturn(Optional.empty());
    MulticriteriaNotFoundException e = Assertions.assertThrows(MulticriteriaNotFoundException.class, () -> reportService.uploadReport(projectId, analysisId, type, fileMultipart));
    Assertions.assertEquals("Cette analyse multicritère n'existe pas.", e.getMessage());
  }

  @Test
  void uploadReport_whenPredictibilityAnalysisNotFound() {
    type = "predictibility_report";
    Project project = new Project();
    project.setProjectId(projectId);
    project.setFolderPath("projects");

    when(reportRepository.findReportByAnalyseIdAndType(analysisId, type)).thenReturn(null);
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    when(predictibilityAnalysisRepository.findById(analysisId)).thenReturn(Optional.empty());
    PredictibilityAnalysisNotFoundException e = Assertions.assertThrows(PredictibilityAnalysisNotFoundException.class, () -> reportService.uploadReport(projectId, analysisId, type, fileMultipart));
    Assertions.assertEquals("Cette analyse de prévisibilité n'existe pas.", e.getMessage());
  }

  @Test
  void uploadReport_whenExecutionPlanNotFound() {
    type = "execution_report";
    Project project = new Project();
    project.setProjectId(projectId);
    project.setFolderPath("projects");

    when(reportRepository.findReportByAnalyseIdAndType(analysisId, type)).thenReturn(null);
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    when(executionPlanRepository.findById(analysisId)).thenReturn(Optional.empty());
    ExecutionPlanNotFoundException e = Assertions.assertThrows(ExecutionPlanNotFoundException.class, () -> reportService.uploadReport(projectId, analysisId, type, fileMultipart));
    Assertions.assertEquals("Ce plan d'exécution n'existe pas.", e.getMessage());
  }

  // probleme de variable d'environnement FILE_UPLOAD_DIR ou  @Value("${file.upload-dir}")
  /*@Test
    void uploadReport2() throws ProjectNotFoundException, MulticriteriaNotFoundException, ExecutionPlanNotFoundException, IOException, PredictibilityAnalysisNotFoundException {
    type = "execution_report";
    Integer iteration = 1;
    Project project = new Project();
    project.setProjectId(projectId);
    project.setFolderPath("/");

    Report report =new Report();
    report.setReportId(1L);
    report.setType(type);
    report.setAnalyseId(analysisId);
    report.setProject(project);
    report.setReportPath(String.valueOf(tempDir));

    ExecutionPlan executionPlan = new ExecutionPlan();
    executionPlan.setExecutionIteration(1);

    when(reportRepository.findReportByAnalyseIdAndType2(analysisId, type, iteration)).thenReturn(null);
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    when(executionPlanRepository.existsById(analysisId)).thenReturn(true);
    assertEquals(ResponseEntity.ok().body("Fichier enregistré avec succès."),reportService.uploadReport2(projectId,analysisId,type,fileMultipart,iteration));
  }*/

  @Test
  void uploadReport2_whenRepportAlreadyExists() throws ProjectNotFoundException, MulticriteriaNotFoundException, ExecutionPlanNotFoundException, PredictibilityAnalysisNotFoundException, IOException {
    type = "execution_report";
    Integer iteration = 1;
    Project project = new Project();
    project.setProjectId(projectId);
    project.setFolderPath("projects");
    Report report = new Report();

    when(reportRepository.findReportByAnalyseIdAndType2(analysisId, type, iteration)).thenReturn(report);
    assertEquals(ResponseEntity.status(HttpStatus.CONFLICT).body("Le rapport a déjà été généré pour cette analyse."), reportService.uploadReport2(projectId, analysisId, type, fileMultipart, iteration));

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
    Long reportId = 1L;
    Report report = new Report();
    report.setReportPath(String.valueOf(tempDir));
    HttpServletResponse mockResponse;
    mockResponse = mock(HttpServletResponse.class);
    when(reportRepository.findById(reportId)).thenReturn(Optional.empty());
    ReportNotFoundException e = Assertions.assertThrows(ReportNotFoundException.class, () -> reportService.previewReport(reportId, mockResponse));
    Assertions.assertEquals("Le rapport avec l'ID " + reportId + " n'existe pas.", e.getMessage());
  }

  @Test
  void previewReport_whenReportDoseNotExists() {
    Long reportId = 1L;
    Report report = new Report();
    report.setReportPath(storingFolder);
    HttpServletResponse mockResponse;
    mockResponse = mock(HttpServletResponse.class);
    File fileReport = new File("");
    when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
    assertFalse(fileReport.exists());
    ReportNotFoundException e = Assertions.assertThrows(ReportNotFoundException.class, () -> reportService.previewReport(reportId, mockResponse));
    Assertions.assertEquals("Le rapport n'existe pas.", e.getMessage());
  }

  @Test
  void downloadReport_whenReportNotFoundException() {
    Long reportId = 1L;
    Report report = new Report();
    report.setReportPath(storingFolder);
    HttpServletResponse mockResponse;
    mockResponse = mock(HttpServletResponse.class);
    when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
    ReportNotFoundException e = Assertions.assertThrows(ReportNotFoundException.class, () -> reportService.downloadReport(reportId, mockResponse));
    Assertions.assertEquals("Le rapport n'existe pas.", e.getMessage());


  }

  @Test
  void downloadReport_whenReportNotFound() {
    Long reportId = 1L;
    HttpServletResponse mockResponse;
    mockResponse = mock(HttpServletResponse.class);
    when(reportRepository.findById(reportId)).thenReturn(Optional.empty());
    ReportNotFoundException p = Assertions.assertThrows(ReportNotFoundException.class, () -> reportService.downloadReport(reportId, mockResponse));
    Assertions.assertEquals("Le rapport avec l'ID " + reportId + " n'existe pas.", p.getMessage());
  }

  @Test
  void getReportsByProjectId() throws ProjectNotFoundException {
    projectId = 1L;
    analysisId = 1L;
    type = "type";
    Project project = new Project();
    project.setProjectId(projectId);
    List<Report> reportsList = new ArrayList<>();

    List<ReportResponseModel> reportstModelList = new ArrayList<>();

    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    when(reportRepository.findReportsByProjectId(project.getProjectId())).thenReturn(reportsList);
    assertEquals(reportstModelList, reportService.getReportsByProjectId(projectId));
  }

  @Test
  void getReportsByProjectId_whenProjectNotFound() {
    projectId = 1L;
    analysisId = 1L;
    type = "type";
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

    assertEquals(ResponseEntity.ok().body("Fichier supprimé avec succès"), reportService.deleteReportById(report.getReportId()));

  }

  @Test
  void deleteReportById_whenErrorTodelete() throws ReportNotFoundException {
    Report report = new Report();
    report.setReportPath("./reports");
    when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
    assertEquals(ResponseEntity.badRequest().body("Erreur lors de la suppression du fichier."), reportService.deleteReportById(reportId));

  }


  @Test
  void renameReportById_whenReportNotFound() {
    Report report = new Report();
    report.setReportId(1L);
    HttpEntity<String> name = new HttpEntity<>("newName");

    when(reportRepository.findById(any())).thenReturn(Optional.empty());
    ReportNotFoundException p = Assertions.assertThrows(ReportNotFoundException.class, () -> reportService.renameReportById(report.getReportId(), name));

    Assertions.assertEquals("Le rapport avec l'ID " + report.getReportId() + " n'existe pas.", p.getMessage());
  }
}
