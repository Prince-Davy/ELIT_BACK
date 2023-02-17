package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.*;
import com.elit.outildedecision.exception.*;
import com.elit.outildedecision.model.ReportResponseModel;
import com.elit.outildedecision.repository.*;
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
import java.io.*;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class ReportServiceImpl implements ReportService {

  private final ReportRepository reportRepository;
  private final ProjectRepository projectRepository;
  private final MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository;
  private final PredictibilityAnalysisRepository predictibilityAnalysisRepository;
  private final ExecutionPlanRepository executionPlanRepository;
  @Value("${file.upload-dir}")
  String STORING_FOLDER;

  public ReportServiceImpl(
      ReportRepository reportRepository,
      ProjectRepository projectRepository,
      MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository,
      PredictibilityAnalysisRepository predictibilityAnalysisRepository,
      ExecutionPlanRepository executionPlanRepository) {
    this.reportRepository = reportRepository;
    this.projectRepository = projectRepository;
    this.multiCriteriaAnalysisRepository = multiCriteriaAnalysisRepository;
    this.predictibilityAnalysisRepository = predictibilityAnalysisRepository;
    this.executionPlanRepository = executionPlanRepository;
  }

  /**
   * @param projectId
   * @param analyseId
   * @param type
   * @param file
   * @return ResponseEntity<Object>
   * @throws ProjectNotFoundException
   * @throws PredictibilityAnaliseNotFoundException
   * @throws MulticriteriaNotFoundException
   * @throws ExecutionPlanNotFoundException
   */
  @Override
  public ResponseEntity<Object> uploadReport(
      Long projectId, Long analyseId, String type, MultipartFile file)
      throws ProjectNotFoundException, PredictibilityAnaliseNotFoundException,
          MulticriteriaNotFoundException, ExecutionPlanNotFoundException {
    Report reportFromDB = reportRepository.findReportByAnalyseIdAndType(analyseId, type);
    if (!Objects.isNull(reportFromDB)) {
      return new ResponseEntity<>(
          "Le rapport a déjà été généré pour cette analyse.", HttpStatus.CONFLICT);
    }

    Project project =
        projectRepository
            .findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("Ce projet n'existe pas."));
    String folderPath = StringUtils.cleanPath(STORING_FOLDER + project.getFolderPath());

    int iteration = 0;

    if (type == ReportTypes.MULTI_CRITERIA_ANALYSIS.toString()) {
      MultiCriteriaAnalysis multiCriteriaAnalysis =
          multiCriteriaAnalysisRepository
              .findById(analyseId)
              .orElseThrow(
                  () ->
                      new MulticriteriaNotFoundException(
                          "Cette analyse multicritère n'existe pas."));
      iteration = multiCriteriaAnalysis.getMultiCriteriaAnalysisIteration();
    } else if (type == ReportTypes.PREDICTIBILITY_ANALYSIS.toString()) {
      PredictibilityAnalysis predictibilityAnalysis =
          predictibilityAnalysisRepository
              .findById(analyseId)
              .orElseThrow(
                  () ->
                      new PredictibilityAnaliseNotFoundException(
                          "Cette analyse de prévisibilité n'existe pas."));
      iteration = predictibilityAnalysis.getPredictibilityAnalysisIteration();
    } else if (type == ReportTypes.EXECUTION_PLAN.toString()) {
      ExecutionPlan executionPlan =
          executionPlanRepository
              .findById(analyseId)
              .orElseThrow(
                  () -> new ExecutionPlanNotFoundException("Ce plan d'exécution n'existe pas."));
      iteration = executionPlan.getExecutionIteration();
    }

    try {
      String filePath = StringUtils.cleanPath(folderPath + "\\" + file.getOriginalFilename());
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
      throw new RuntimeException(
          "Erreur lors de l'enregistrement du fichier : " + file.getOriginalFilename());
    }
    return ResponseEntity.ok().body("Fichier enregistré avec succès.");
  }

  @Override
  public ResponseEntity<Object> uploadReport2(
      Long projectId, Long analyseId, String type, MultipartFile file, Integer iteration)
      throws ProjectNotFoundException, PredictibilityAnaliseNotFoundException,
          MulticriteriaNotFoundException, ExecutionPlanNotFoundException {
    Report reportFromDB =
        reportRepository.findReportByAnalyseIdAndType2(analyseId, type, iteration);
    if (!Objects.isNull(reportFromDB)) {
      return new ResponseEntity<>(
          "Le rapport a déjà été généré pour cette analyse.", HttpStatus.CONFLICT);
    }

    Project project =
        projectRepository
            .findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("Ce projet n'existe pas."));
    String folderPath = StringUtils.cleanPath(STORING_FOLDER + project.getFolderPath());

    if (type == ReportTypes.MULTI_CRITERIA_ANALYSIS.toString()) {
      MultiCriteriaAnalysis multiCriteriaAnalysis =
          multiCriteriaAnalysisRepository
              .findById(analyseId)
              .orElseThrow(
                  () ->
                      new MulticriteriaNotFoundException(
                          "Cette analyse multicritère n'existe pas."));

    } else if (type == ReportTypes.PREDICTIBILITY_ANALYSIS.toString()) {
      PredictibilityAnalysis predictibilityAnalysis =
          predictibilityAnalysisRepository
              .findById(analyseId)
              .orElseThrow(
                  () ->
                      new PredictibilityAnaliseNotFoundException(
                          "Cette analyse de prévisibilité n'existe pas."));
    } else if (type == ReportTypes.EXECUTION_PLAN.toString()) {
      ExecutionPlan executionPlan =
          executionPlanRepository
              .findById(analyseId)
              .orElseThrow(
                  () -> new ExecutionPlanNotFoundException("Ce plan d'exécution n'existe pas."));
    }

    try {
      String filePath = StringUtils.cleanPath(folderPath + "\\" + file.getOriginalFilename());
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
      throw new RuntimeException(
          "Erreur lors de l'enregistrement du fichier : " + file.getOriginalFilename());
    }
    return ResponseEntity.ok().body("Fichier enregistré avec succès.");
  }

  @Override
  public void previewReport(Long reportId, HttpServletResponse response)
      throws ReportNotFoundException, IOException {
    Report report =
        reportRepository
            .findById(reportId)
            .orElseThrow(() -> new ReportNotFoundException("Ce rapport n'existe pas."));

    File file = new File(report.getReportPath());

    if (file.exists()) {
      String mimeType = URLConnection.guessContentTypeFromName(file.getName());
      if (mimeType == null) {
        mimeType = "application/octet-stream";
      }

      response.setContentType(mimeType);
      response.setHeader(
          "Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));
      response.setContentLength((int) file.length());

      InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
      FileCopyUtils.copy(inputStream, response.getOutputStream());
    } else {
      System.out.println("Le rapport n'existe pas.");
    }
  }

  @Override
  public void downloadReport(Long reportId, HttpServletResponse response)
      throws IOException, ReportNotFoundException {
    Report report =
        reportRepository
            .findById(reportId)
            .orElseThrow(() -> new ReportNotFoundException("Ce rapport n'existe pas."));

    File file = new File(report.getReportPath());

    if (file.exists()) {
      String mimeType = URLConnection.guessContentTypeFromName(file.getName());
      if (mimeType == null) {
        mimeType = "application/octet-stream";
      }

      response.setContentType(mimeType);
      response.setHeader(
          "Content-Disposition", String.format("report; filename=\"" + file.getName() + "\""));
      response.setContentLength((int) file.length());

      InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
      FileCopyUtils.copy(inputStream, response.getOutputStream());
    } else {
      System.out.println("Le rapport n'existe pas.");
    }
  }

  @Override
  public List<ReportResponseModel> getReportsByProjectId(Long projectId)
      throws ProjectNotFoundException {
    Project project =
        projectRepository
            .findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("Ce projet n'existe pas."));

    String folderPath = StringUtils.cleanPath(STORING_FOLDER + project.getFolderPath());

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

  @Override
  public ResponseEntity<String> deleteReportById(Long reportId) throws ReportNotFoundException {
    Report report =
        reportRepository
            .findById(reportId)
            .orElseThrow(() -> new ReportNotFoundException("Ce rapport n'existe pas."));

    File fileToDelete = new File(report.getReportPath());
    if (FileSystemUtils.deleteRecursively(fileToDelete)) {
      reportRepository.deleteById(reportId);
      return ResponseEntity.ok().body("Fichier supprimé avec succès");
    } else {
      return ResponseEntity.badRequest().body("Erreur lors de la suppression du fichier.");
    }
  }

  @Override
  public ResponseEntity<String> renameReportById(Long reportId, HttpEntity<String> name)
      throws IOException, ReportNotFoundException {
    Report reportFromDB =
        reportRepository
            .findById(reportId)
            .orElseThrow(() -> new ReportNotFoundException("Ce rapport n'existe pas."));

    Map<String, String> newName = new ObjectMapper().readValue(name.getBody(), HashMap.class);

    reportFromDB.setReportPath(newName.get("newReportName"));
    Path oldFile = Paths.get(reportFromDB.getReportPath());
    Path newPath = Files.move(oldFile, oldFile.resolveSibling(newName.get("newReportName")));
    reportFromDB.setReportPath(StringUtils.cleanPath(newPath.toString()));
    reportRepository.save(reportFromDB);

    return ResponseEntity.ok().body("Le fichier a été mis à jour.");
  }
}
