
package com.elit.outildedecision.service;

import com.elit.outildedecision.exception.ExecutionPlanNotFoundException;
import com.elit.outildedecision.exception.FileCouldNotBeSaved;
import com.elit.outildedecision.exception.MulticriteriaNotFoundException;
import com.elit.outildedecision.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.ReportNotFoundException;
import com.elit.outildedecision.model.ReportResponseModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
/**
 * This interface defines the contract for the Report Service, which provides methods to upload, preview,
 * download, retrieve, update, and delete reports associated with projects and analyses.
 */
public interface ReportService {

  /**
   * Uploads a report file for the specified project, analysis, and type.
   *
   * @param projectId the ID of the project to associate the report with
   * @param analyseId the ID of the analysis to associate the report with
   * @param type a String representing the type of report being uploaded
   * @param file the MultipartFile object representing the report file to upload
   * @return a ResponseEntity containing the uploaded report data
   * @throws ProjectNotFoundException if the specified project does not exist
   * @throws PredictibilityAnalysisNotFoundException if the specified predictibility analysis does not exist
   * @throws MulticriteriaNotFoundException if the specified multi-criteria analysis does not exist
   * @throws ExecutionPlanNotFoundException if the specified execution plan does not exist
   */
  ResponseEntity<Object> uploadReport(
    Long projectId, Long analyseId, String type, MultipartFile file)
    throws ProjectNotFoundException, PredictibilityAnalysisNotFoundException,
    MulticriteriaNotFoundException, ExecutionPlanNotFoundException, FileCouldNotBeSaved;

  /**
   * Uploads a report file with an iteration number for the specified project, analysis, and type.
   *
   * @param projectId the ID of the project to associate the report with
   * @param analyseId the ID of the analysis to associate the report with
   * @param type a String representing the type of report being uploaded
   * @param file the MultipartFile object representing the report file to upload
   * @param iteration an Integer representing the iteration number to associate with the report
   * @return a ResponseEntity containing the uploaded report data
   * @throws ProjectNotFoundException if the specified project does not exist
   * @throws PredictibilityAnalysisNotFoundException if the specified predictibility analysis does not exist
   * @throws MulticriteriaNotFoundException if the specified multi-criteria analysis does not exist
   * @throws ExecutionPlanNotFoundException if the specified execution plan does not exist
   * @throws IOException if an I/O error occurs while uploading the report file
   */
  ResponseEntity<Object> uploadReport2(
    Long projectId, Long analyseId, String type, MultipartFile file, Integer iteration)
    throws ProjectNotFoundException, PredictibilityAnalysisNotFoundException,
    MulticriteriaNotFoundException, ExecutionPlanNotFoundException, IOException;

  /**
   * Sends a preview of the report with the specified ID to the specified HttpServletResponse.
   *
   * @param reportId the ID of the report to preview
   * @param response the HttpServletResponse to send the preview to
   * @throws ReportNotFoundException if the specified report does not exist
   * @throws IOException if an I/O error occurs while sending the preview
   */
  void previewReport(Long reportId, HttpServletResponse response)
    throws ReportNotFoundException, IOException;

  /**
   * Downloads the report with the specified ID as an attachment to the specified HttpServletResponse.
   *
   * @param reportId the ID of the report to download
   * @param response the HttpServletResponse to send the downloaded report to
   * @throws IOException if an I/O error occurs while sending the report
   * @throws ReportNotFoundException if the specified report does not exist
   */
  void downloadReport(Long reportId, HttpServletResponse response)
    throws IOException, ReportNotFoundException;

/**
 * Retrieves a list of ReportResponseModel objects for the specified project.
 *
 * @param projectId the ID of the project to retrieve reports for
 * @return a List of ReportResponseModel objects representing the
 * reports associated with the specified project
 * @throws ProjectNotFoundException if the specified project does not exist
 */
List<ReportResponseModel> getReportsByProjectId(Long projectId) throws ProjectNotFoundException;
  /**
   *
   * Deletes the report with the specified ID.
   * @param reportId the ID of the report to delete
   * @return a ResponseEntity indicating the success or failure of the delete operation
   * @throws ReportNotFoundException if the specified report does not exist
   */
  ResponseEntity<String> deleteReportById(Long reportId) throws ReportNotFoundException;
  /**
   *
   * Renames the report with the specified ID.
   * @param reportId the ID of the report to rename
   * @param name an HttpEntity containing a String representing the new name of the report
   * @return a ResponseEntity indicating the success or failure of the update operation
   * @throws IOException if an I/O error occurs while updating the report data
   * @throws ReportNotFoundException if the specified report does not exist
   */
  ResponseEntity<String> renameReportById(Long reportId, HttpEntity<String> name)
    throws IOException, ReportNotFoundException;
}
