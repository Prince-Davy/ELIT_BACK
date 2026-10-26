
package com.elit.outildedecision.shared.report.service;

import com.elit.outildedecision.shared.report.dto.ReportResponse;
import com.elit.outildedecision.shared.report.exception.ReportNotFoundException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.util.ReportTypes;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
/**
 * This interface defines the contract for the Report Service, which provides methods to upload, preview,
 * download, retrieve, update, and delete reports associated with projects and analyses.
 */
public interface ReportService {

  /**
   * Uploads a report file with an iteration number for the specified project, analysis, and type.
   *
   * @param projectId the ID of the project to associate the report with
   * @param analyseId the ID of the analysis to associate the report with
   * @param type the typed report category
   * @param file the MultipartFile object representing the report file to upload
   * @param iteration an Integer representing the iteration number to associate with the report
   * @return a success message; a {@link com.elit.outildedecision.shared.exception.ConflictException}
   *         (HTTP 409) is thrown when a report already exists for this analysis
   * @throws ProjectNotFoundException if the specified project does not exist
   * @throws IOException if an I/O error occurs while uploading the report file
   */
  String uploadReport2(
    Long projectId, Long analyseId, ReportTypes type, MultipartFile file, Integer iteration)
    throws ProjectNotFoundException, IOException;

  /**
   * Uploads and update a report file with an iteration number for the specified project, analysis,and type.
   *
   * @param projectId the ID of the project to associate the report with
   * @param analyseId the ID of the analysis to associate the report with
   * @param type the typed report category
   * @param file the MultipartFile object representing the report file to upload
   * @param iteration an Integer representing the iteration number to associate with the report
   * @return a success message; a {@link com.elit.outildedecision.shared.exception.ConflictException}
   *         (HTTP 409) is thrown when no report exists yet for this analysis
   * @throws ProjectNotFoundException if the specified project does not exist
   * @throws IOException if an I/O error occurs while uploading the report file
   */
  String uploadAndUpdateReport2(
    Long projectId, Long analyseId, ReportTypes type, MultipartFile file, Integer iteration)
    throws ProjectNotFoundException, IOException;

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
   * Retrieves a list of ReportResponse objects for the specified project.
   *
   * @param projectId the ID of the project to retrieve reports for
   * @return a List of ReportResponse objects representing the
   * reports associated with the specified project
   * @throws ProjectNotFoundException if the specified project does not exist
   */
  List<ReportResponse> getReportsByProjectId(Long projectId) throws ProjectNotFoundException;

  /**
   * Deletes the report with the specified ID.
   *
   * @param reportId the ID of the report to delete
   * @return a success message
   * @throws ReportNotFoundException if the specified report does not exist
   */
  String deleteReportById(Long reportId) throws ReportNotFoundException;
}
