package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.ExecutionPlan;
import com.elit.outildedecision.entity.MultiCriteriaAnalysis;
import com.elit.outildedecision.entity.PredictibilityAnalysis;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.Report;
import com.elit.outildedecision.exception.ExecutionPlanNotFoundException;
import com.elit.outildedecision.exception.FileCouldNotBeSaved;
import com.elit.outildedecision.exception.MulticriteriaNotFoundException;
import com.elit.outildedecision.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.ReportNotFoundException;
import com.elit.outildedecision.model.ReportResponseModel;
import com.elit.outildedecision.repository.ExecutionPlanRepository;
import com.elit.outildedecision.repository.MultiCriteriaAnalysisRepository;
import com.elit.outildedecision.repository.PredictibilityAnalysisRepository;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.repository.ReportRepository;
import com.elit.outildedecision.service.ReportService;
import com.elit.outildedecision.util.ReportTypes;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * this class is the implementation of the ReportService interface.
 * Service implementation for generating and storing reports.
 */
@Service
public class ReportServiceImpl implements ReportService {
  /**
   * Repository instance for accessing report data.
   */
  private final ReportRepository reportRepository;

  /**
   * Repository instance for accessing project data.
   */
  private final ProjectRepository projectRepository;

  /**
   * Repository instance for accessing multi-criteria analysis data.
   */
  private final MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository;

  /**
   * Repository instance for accessing predictibility analysis data.
   */
  private final PredictibilityAnalysisRepository predictibilityAnalysisRepository;

  /**
   * Repository instance for accessing execution plan data.
   */
  private final ExecutionPlanRepository executionPlanRepository;
  /**
   * Directory path to store generated reports.
   */
  @Value("${file.upload-dir}")
  private String storingFolder;
  /**
   * The message to display when the project with the specified ID cannot be found.
   */
  private static final String PROJECT_NOT_FOUND = "Le projet avec l'ID %d n'existe pas.";
  /**
   * The message to display when the report with the specified ID cannot be found.
   */
  private static final String REPORT_NOT_FOUND = "Le rapport avec l'ID %d n'existe pas.";
  /**
   * Constructs a new ReportServiceImpl with the specified repository instances.
   * it is used to inject the repository instances
   *
   * @param reportRepository                 The repository instance for accessing report data.
   * @param projectRepository                The repository instance for accessing project data.
   * @param multiCriteriaAnalysisRepository  The repository instance for accessing multi-criteria analysis data.
   * @param predictibilityAnalysisRepository The repository instance for accessing predictibility analysis data.
   * @param executionPlanRepository          The repository instance for accessing execution plan data.
   */
  public ReportServiceImpl(
    final ReportRepository reportRepository,
    final ProjectRepository projectRepository,
    final MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository,
    final PredictibilityAnalysisRepository predictibilityAnalysisRepository,
    final ExecutionPlanRepository executionPlanRepository) {
    this.reportRepository = reportRepository;
    this.projectRepository = projectRepository;
    this.multiCriteriaAnalysisRepository = multiCriteriaAnalysisRepository;
    this.predictibilityAnalysisRepository = predictibilityAnalysisRepository;
    this.executionPlanRepository = executionPlanRepository;
  }
  /**
   * Uploads a report file for the specified project and analysis, and saves the report to the database.
   *
   * @param projectId The ID of the project associated with the report.
   * @param analyseId The ID of the analysis associated with the report.
   * @param type The type of the report (e.g. MultiCriteriaAnalysis, PredictibilityAnalysis, ExecutionPlan).
   * @param file The report file to upload.
   * @return A ResponseEntity containing a success message if the file was uploaded successfully.
   * @throws ProjectNotFoundException If the project with the specified ID cannot be found.
   * @throws PredictibilityAnalysisNotFoundException If the predictibility analysis with the specified ID cannot be found.
   * @throws MulticriteriaNotFoundException If the multi-criteria analysis with the specified ID cannot be found.
   * @throws ExecutionPlanNotFoundException If the execution plan with the specified ID cannot be found.
   * @throws FileCouldNotBeSaved If the report could not be saved.
   */
  @Override
  public ResponseEntity<Object> uploadReport(final Long projectId,
                                             final Long analyseId,
                                             final String type,
                                             final MultipartFile file) throws ProjectNotFoundException,
    PredictibilityAnalysisNotFoundException,
    MulticriteriaNotFoundException,
    ExecutionPlanNotFoundException, FileCouldNotBeSaved {
    Report reportFromDB = reportRepository.findReportByAnalyseIdAndType(analyseId, type);
    if (!Objects.isNull(reportFromDB)) {
      return new ResponseEntity<>(
        "Le rapport a déjà été généré pour cette analyse.", HttpStatus.CONFLICT);
    }

    Project project =
      projectRepository
        .findById(projectId)
        .orElseThrow(() -> new ProjectNotFoundException(String.format(PROJECT_NOT_FOUND, projectId)));
    String folderPath = StringUtils.cleanPath(storingFolder + project.getFolderPath());

    int iteration = 0;

    if (type.equals(ReportTypes.MULTI_CRITERIA_ANALYSIS.toString())) {
      MultiCriteriaAnalysis multiCriteriaAnalysis =
        multiCriteriaAnalysisRepository
          .findById(analyseId)
          .orElseThrow(
            () ->
              new MulticriteriaNotFoundException(
                "Cette analyse multicritère n'existe pas."));
      iteration = multiCriteriaAnalysis.getMultiCriteriaAnalysisIteration();
    } else if (type.equals(ReportTypes.PREDICTIBILITY_ANALYSIS.toString())) {
      PredictibilityAnalysis predictibilityAnalysis =
        predictibilityAnalysisRepository
          .findById(analyseId)
          .orElseThrow(
            () ->
              new PredictibilityAnalysisNotFoundException(
                "Cette analyse de prévisibilité n'existe pas."));
      iteration = predictibilityAnalysis.getPredictibilityAnalysisIteration();
    } else if (type.equals(ReportTypes.EXECUTION_PLAN.toString())) {
      ExecutionPlan executionPlan =
        executionPlanRepository
          .findById(analyseId)
          .orElseThrow(
            () -> new ExecutionPlanNotFoundException("Ce plan d'exécution n'existe pas."));
      iteration = executionPlan.getExecutionIteration();
    }

    try {
      String filePath = StringUtils.cleanPath(folderPath.concat("\\").concat(file.getOriginalFilename()));
      Files.copy(file.getInputStream(), Path.of(filePath), StandardCopyOption.REPLACE_EXISTING);
      Report report =
        Report.builder()
          .type(type)
          .reportPath(filePath)
          .analyseId(analyseId)
          .project(project)
          .iteration(iteration)
          .build();
      reportRepository.save(report);
    } catch (IOException e) {
      throw new FileCouldNotBeSaved("Erreur lors de l'enregistrement du fichier : " + file.getOriginalFilename());
    }
    return ResponseEntity.ok().body("Fichier enregistré avec succès.");
  }
  /**
   *
   *  the method to upload a report for a given project, analysis, type, file, and iteration.
   * @param projectId The ID of the project for which the report is being uploaded.
   * @param analyseId The ID of the analysis for which the report is being uploaded.
   * @param type The type of report being uploaded (MULTI_CRITERIA_ANALYSIS, PREDICTIBILITY_ANALYSIS, or EXECUTION_PLAN).
   * @param file The report file being uploaded.
   * @param iteration The iteration of the report being uploaded.
   * @return A ResponseEntity with a success message if the file was uploaded successfully, or a conflict status if a report already exists for the given analysis and type.
   * @throws ProjectNotFoundException If the project does not exist.
   * @throws MulticriteriaNotFoundException If the analysis type is MULTI_CRITERIA_ANALYSIS and the analysis does not exist.
   * @throws PredictibilityAnalysisNotFoundException If the analysis type is PREDICTIBILITY_ANALYSIS and the analysis does not exist.
   * @throws ExecutionPlanNotFoundException If the analysis type is EXECUTION_PLAN and the analysis does not exist.
   * @throws RuntimeException If there is an error while saving the report file.
   */
  @Override
  public ResponseEntity<Object> uploadReport2(final Long projectId,
                                              final Long analyseId,
                                              final String type,
                                              final MultipartFile file,
                                              final Integer iteration) throws ProjectNotFoundException,
    PredictibilityAnalysisNotFoundException,
    MulticriteriaNotFoundException,
    ExecutionPlanNotFoundException, IOException {
    Report reportFromDB =
      reportRepository.findReportByAnalyseIdAndType2(analyseId, type, iteration);
    if (!Objects.isNull(reportFromDB)) {
      return new ResponseEntity<>(
        "Le rapport a déjà été généré pour cette analyse.", HttpStatus.CONFLICT);
    }

    Project project =
      projectRepository
        .findById(projectId)
        .orElseThrow(() -> new ProjectNotFoundException(String.format(PROJECT_NOT_FOUND, projectId)));
    String folderPath = StringUtils.cleanPath(storingFolder + project.getFolderPath());

    if (type.equals(ReportTypes.MULTI_CRITERIA_ANALYSIS.toString()) && !multiCriteriaAnalysisRepository.existsById(analyseId)) {
      throw new MulticriteriaNotFoundException("Cette analyse multicritère n'existe pas.");
    } else if (type.equals(ReportTypes.PREDICTIBILITY_ANALYSIS.toString()) && !predictibilityAnalysisRepository.existsById(analyseId)) {
      throw new PredictibilityAnalysisNotFoundException("Cette analyse de prévisibilité n'existe pas.");
    } else if (type.equals(ReportTypes.EXECUTION_PLAN.toString()) && !executionPlanRepository.existsById(analyseId)) {
      throw new ExecutionPlanNotFoundException("Ce plan d'exécution n'existe pas.");
    }
      String filePath = StringUtils.cleanPath(folderPath.concat("\\").concat(file.getOriginalFilename()));
      Files.copy(file.getInputStream(), Path.of(filePath), StandardCopyOption.REPLACE_EXISTING);
      Report report =
        Report.builder()
          .type(type)
          .reportPath(filePath)
          .analyseId(analyseId)
          .project(project)
          .iteration(iteration)
          .build();
      reportRepository.save(report);
    return ResponseEntity.ok().body("Fichier enregistré avec succès.");
  }
  /**
   * the method to preview a report by sending it as a response to the given HttpServletResponse object.
   * @param reportId The ID of the report to preview.
   * @param response The HttpServletResponse object to send the report as a response.
   * @throws ReportNotFoundException If the report with the given ID does not exist.
   * @throws IOException If there is an error while reading or writing the report file.
   */
  @Override
  public void previewReport(final Long reportId,
                            final HttpServletResponse response) throws ReportNotFoundException,
    IOException {
    Report report =
      reportRepository
        .findById(reportId)
        .orElseThrow(() -> new ReportNotFoundException(String.format(REPORT_NOT_FOUND, reportId)));

    File fileReport = new File(report.getReportPath());

    if (fileReport.exists()) {
      String mimeType = URLConnection.guessContentTypeFromName(fileReport.getName());
      if (mimeType == null) {
        mimeType = "application/octet-stream";
      }

      response.setContentType(mimeType);
      response.setHeader(
        "Content-Disposition", String.format("inline; filename=\"%s\"", fileReport.getName()));
      response.setContentLength((int) fileReport.length());

      try (InputStream inputStream = new BufferedInputStream(new FileInputStream(fileReport))) {
        FileCopyUtils.copy(inputStream, response.getOutputStream());
      }
    } else {
      throw new ReportNotFoundException("Le rapport n'existe pas.");
    }
  }
  /**
   * Overrides the method downloadReport to download a report file by its ID using the HttpServletResponse object.
   * @param reportId The ID of the report to be downloaded.
   * @param response The HttpServletResponse object to use to send the file to the client.
   * @throws IOException If there is an issue with the input/output operations.
   * @throws ReportNotFoundException If the requested report does not exist.
   */
  @Override
  public void downloadReport(final Long reportId,
                             final HttpServletResponse response) throws IOException,
    ReportNotFoundException {
    Report report =
      reportRepository
        .findById(reportId)
        .orElseThrow(() -> new ReportNotFoundException(String.format(REPORT_NOT_FOUND, reportId)));

    File file = new File(report.getReportPath());

    if (file.exists()) {
      String mimeType = URLConnection.guessContentTypeFromName(file.getName());
      if (mimeType == null) {
        mimeType = "application/octet-stream";
      }

      response.setContentType(mimeType);
      response.setHeader(
        "Content-Disposition", String.format("report; filename=\"%s\"", file.getName()));
      response.setContentLength((int) file.length());

      try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
        FileCopyUtils.copy(inputStream, response.getOutputStream());
      }
    } else {
      throw new ReportNotFoundException("Le rapport n'existe pas.");
    }
  }
  /**
   * Overrides the method getReportsByProjectId to retrieve a list of ReportResponseModel objects based on a project ID.
   * @param projectId The ID of the project to retrieve reports for.
   * @throws ProjectNotFoundException If the requested project does not exist.
   * @return A list of ReportResponseModel objects.
   */
  @Override
  public List<ReportResponseModel> getReportsByProjectId(final Long projectId) throws ProjectNotFoundException {
    Project project =
      projectRepository
        .findById(projectId)
        .orElseThrow(() -> new ProjectNotFoundException(String.format(PROJECT_NOT_FOUND, projectId)));

    String folderPath = StringUtils.cleanPath(storingFolder + project.getFolderPath());

    List<Report> reportsList = reportRepository.findReportsByProjectId(project.getProjectId());
    List<ReportResponseModel> reportstModelList = new ArrayList<>();
    for (Report report : reportsList) {
      ReportResponseModel reportModel = new ReportResponseModel();
      reportModel.setId(report.getReportId());
      reportModel.setTitle(report.getReportPath().replace(folderPath + "/", ""));
      reportModel.setType(report.getType());
      reportModel.setIteration(report.getIteration());
      reportModel.setCreationDate(report.getCreatedAt());
      reportstModelList.add(reportModel);
    }

    return reportstModelList;
  }
  /**
   * Overrides the method to delete a report by its ID, along with the corresponding file in the file system.
   *
   * @param reportId The ID of the report to be deleted.
   * @return A ResponseEntity object indicating whether the file was successfully deleted or not.
   * @throws ReportNotFoundException If the report with the given ID does not exist.
   */
  @Override
  public ResponseEntity<String> deleteReportById(final Long reportId) throws ReportNotFoundException {
    Report report =
      reportRepository
        .findById(reportId)
        .orElseThrow(() -> new ReportNotFoundException(String.format(REPORT_NOT_FOUND, reportId)));

    File fileToDelete = new File(report.getReportPath());
    if (FileSystemUtils.deleteRecursively(fileToDelete)) {
      reportRepository.deleteById(reportId);
      return ResponseEntity.ok().body("Fichier supprimé avec succès");
    } else {
      return ResponseEntity.badRequest().body("Erreur lors de la suppression du fichier.");
    }
  }
  /**
   * Overrides the method to rename a report by its ID, along with the corresponding file in the file system.
   * @param reportId The ID of the report to be renamed.
   * @param name An HttpEntity object containing the new name of the report in its body.
   * @return A ResponseEntity object indicating whether the file was successfully renamed or not.
   * @throws IOException If there is an error while reading or writing the report file.
   * @throws ReportNotFoundException If the report with the given ID does not exist.
   */
  @Override
  public ResponseEntity<String> renameReportById(final Long reportId,
                                                 final HttpEntity<String> name) throws IOException,
    ReportNotFoundException {
    Report reportFromDB =
      reportRepository
        .findById(reportId)
        .orElseThrow(() -> new ReportNotFoundException(String.format(REPORT_NOT_FOUND, reportId)));
    Map<String, String> newName = new ObjectMapper().readValue(name.getBody(), HashMap.class);
    reportFromDB.setReportPath(newName.get("newReportName"));
    Path oldFile = Paths.get(reportFromDB.getReportPath());
    Path newPath = Files.move(oldFile, oldFile.resolveSibling(newName.get("newReportName")));
    reportFromDB.setReportPath(StringUtils.cleanPath(newPath.toString()));
    reportRepository.save(reportFromDB);
    return ResponseEntity.ok().body("Le fichier a été mis à jour.");
  }
}
