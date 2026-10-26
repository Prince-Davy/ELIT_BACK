
package com.elit.outildedecision.collaboration.report.controller;

import com.elit.outildedecision.collaboration.multicriteria.exception.MulticriteriaNotFoundException;
import com.elit.outildedecision.collaboration.predictibility.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.collaboration.report.dto.ReportResponse;
import com.elit.outildedecision.collaboration.report.exception.ReportNotFoundException;
import com.elit.outildedecision.collaboration.report.service.ReportService;
import com.elit.outildedecision.shared.exception.ExecutionPlanNotFoundException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.util.ReportTypes;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Report controller.
 */
@RestController
@RequestMapping("/report/")
@Tag(name = "Collaboration - Report", description = "Génération et gestion des rapports")
public class ReportController {
  /**
   * Report service.
   */
  private final ReportService reportService;
  /**
   * Constructor of ReportController.
   * dependency injection of ReportService
   * @param reportService ReportService
   */
  public ReportController(final ReportService reportService) {
    this.reportService = reportService;
  }

  /**
   * the second method to Upload report to database.
   * @param projectId Project id
   * @param analyseId Analyse id
   * @param type Type of report
   * @param file File
   * @param iteration Iteration
   * @return ResponseEntity
   * @throws ProjectNotFoundException ProjectNotFoundException
   * @throws MulticriteriaNotFoundException MulticriteriaNotFoundException
   * @throws ExecutionPlanNotFoundException ExecutionPlanNotFoundException
   * @throws PredictibilityAnalysisNotFoundException PredictibilityAnaliseNotFoundException
   * @throws IOException when there is a problem with the report file
   */
  @PostMapping(
      value = "upload/{projectId}/{analyseId}/{type}/{iteration}",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<String> uploadReport2(
      @PathVariable final Long projectId,
      @PathVariable final Long analyseId,
      @PathVariable final ReportTypes type,
      @RequestParam("file") final MultipartFile file,
      @PathVariable final Integer iteration)
    throws ProjectNotFoundException, MulticriteriaNotFoundException,
    ExecutionPlanNotFoundException, PredictibilityAnalysisNotFoundException, IOException {
    return ResponseEntity.ok(reportService.uploadReport2(projectId, analyseId, type, file, iteration));
  }

  /**
   * the method to Update report to database.
   * @param projectId project Id
   * @param analyseId analyse Id
   * @param type type
   * @param file File
   * @param iteration number of iteration
   * @return ResponseEntity
   * @throws ProjectNotFoundException ProjectNotFoundException
   * @throws MulticriteriaNotFoundException MulticriteriaNotFoundException
   * @throws ExecutionPlanNotFoundException ExecutionPlanNotFoundException
   * @throws PredictibilityAnalysisNotFoundException PredictibilityAnaliseNotFoundException
   * @throws IOException when there is a problem with the report file
   */
  @PutMapping(
    value = "upload/{projectId}/{analyseId}/{type}/{iteration}/update",
    consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<String> updateReport2(
    @PathVariable final Long projectId,
    @PathVariable final Long analyseId,
    @PathVariable final ReportTypes type,
    @RequestParam("file") final MultipartFile file,
    @PathVariable final Integer iteration)
    throws ProjectNotFoundException, MulticriteriaNotFoundException,
    ExecutionPlanNotFoundException, PredictibilityAnalysisNotFoundException, IOException {
    return ResponseEntity.ok(reportService.uploadAndUpdateReport2(projectId, analyseId, type, file, iteration));
  }

 /**
   * Preview report by id.
   * @param reportId Report id
  *  @param response HttpServletResponse
   * @throws ReportNotFoundException ReportNotFoundException
  *  @throws IOException when there is a problem with the report file
   */
  @GetMapping("/preview/{reportId}")
  public void previewReport(final HttpServletResponse response, @PathVariable final Long reportId)
      throws IOException, ReportNotFoundException {
    reportService.previewReport(reportId, response);
  }
  /**
   * Download report by id.
   * @param reportId Report id
   * @param response HttpServletResponse
   * @throws ReportNotFoundException ReportNotFoundException
   * @throws IOException IOException
   */
  @GetMapping("download/{reportId}")
  public void downloadReport(@PathVariable final Long reportId, final HttpServletResponse response)
      throws IOException, ReportNotFoundException {
    reportService.downloadReport(reportId, response);
  }
  /**
   * Get all reports of a project.
   * @param projectId Project id
   * @throws ProjectNotFoundException when the project is not found in the database
   * @return List of ReportResponse
   */
  @GetMapping("{projectId}")
  public List<ReportResponse> getReportsByProjectId(@PathVariable final Long projectId)
      throws ProjectNotFoundException {
    return reportService.getReportsByProjectId(projectId);
  }
  /**
   * Delete a report by id.
   *
   * @param reportId report id
   * @throws ReportNotFoundException when the report is not found in the database
   * @return ResponseEntity with the deletion outcome
   */
  @DeleteMapping("{reportId}")
  public ResponseEntity<String> deleteReportById(@PathVariable final Long reportId)
      throws ReportNotFoundException {
    return ResponseEntity.ok(reportService.deleteReportById(reportId));
  }
}
