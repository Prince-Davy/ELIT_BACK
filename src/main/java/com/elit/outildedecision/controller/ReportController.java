
package com.elit.outildedecision.controller;

import com.elit.outildedecision.exception.ExecutionPlanNotFoundException;
import com.elit.outildedecision.exception.MulticriteriaNotFoundException;
import com.elit.outildedecision.exception.PredictibilityAnaliseNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.ReportNotFoundException;
import com.elit.outildedecision.model.ReportResponseModel;
import com.elit.outildedecision.service.ReportService;
import org.springframework.http.HttpEntity;
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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Report controller.
 */
@RestController
@RequestMapping("/api/report/")
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
   * Upload report to database.
   * @param projectId Project id
   * @param analyseId Analyse id
   * @param type Type of report
   * @param file File
   * @return ResponseEntity
   * @throws ProjectNotFoundException ProjectNotFoundException
   * @throws MulticriteriaNotFoundException MulticriteriaNotFoundException
   * @throws ExecutionPlanNotFoundException ExecutionPlanNotFoundException
   * @throws PredictibilityAnaliseNotFoundException PredictibilityAnaliseNotFoundException
   */
  @PostMapping(
      value = "upload/{projectId}/{analyseId}/{type}",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<Object> uploadReport(
      @PathVariable final Long projectId,
      @PathVariable final Long analyseId,
      @PathVariable final String type,
      @RequestParam("file") final MultipartFile file)
      throws ProjectNotFoundException, MulticriteriaNotFoundException,
          ExecutionPlanNotFoundException, PredictibilityAnaliseNotFoundException {
    return reportService.uploadReport(projectId, analyseId, type, file);
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
   * @throws PredictibilityAnaliseNotFoundException PredictibilityAnaliseNotFoundException
   */
  @PostMapping(
      value = "upload/{projectId}/{analyseId}/{type}/{iteration}",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<Object> uploadReport2(
      @PathVariable final Long projectId,
      @PathVariable final Long analyseId,
      @PathVariable final String type,
      @RequestParam("file") final MultipartFile file,
      @PathVariable final Integer iteration)
      throws ProjectNotFoundException, MulticriteriaNotFoundException,
          ExecutionPlanNotFoundException, PredictibilityAnaliseNotFoundException {
    return reportService.uploadReport2(projectId, analyseId, type, file, iteration);
  }
 /**
   * Preview report by id.
   * @param reportId Report id
  * @param response HttpServletResponse
   * @throws ReportNotFoundException ReportNotFoundException
   */
  @RequestMapping("/preview/{reportId}")
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
  @RequestMapping("download/{reportId}")
  public void downloadReport(@PathVariable final Long reportId, final HttpServletResponse response)
      throws IOException, ReportNotFoundException {
    reportService.downloadReport(reportId, response);
  }
  /**
   * Get all reports of a project.
   * @param projectId Project id
   * @return List of ReportResponseModel
   */
  @GetMapping("{projectId}")
  public List<ReportResponseModel> getReportsByProjectId(@PathVariable final Long projectId)
      throws ProjectNotFoundException {
    return reportService.getReportsByProjectId(projectId);
  }
  /**
   * Get all reports of a project.
   * @param reportId report id
   * @return List of ReportResponseModel
   */
  @DeleteMapping("{reportId}")
  public ResponseEntity<String> deleteReportById(@PathVariable final Long reportId)
      throws ReportNotFoundException {
    return reportService.deleteReportById(reportId);
  }
  /**
   * Rename report by id.
   * @param reportId Report id
   * @param name New name
   * @return ResponseEntity
   * @throws ReportNotFoundException ReportNotFoundException
   * @throws IOException IOException
   */
  @PutMapping("{reportId}")
  public ResponseEntity<String> renameReportById(
      @PathVariable final Long reportId, final HttpEntity<String> name)
      throws IOException, ReportNotFoundException {
    return reportService.renameReportById(reportId, name);
  }
}
