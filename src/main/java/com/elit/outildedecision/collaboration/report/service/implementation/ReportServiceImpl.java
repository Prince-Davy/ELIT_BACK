package com.elit.outildedecision.collaboration.report.service.implementation;

import com.elit.outildedecision.collaboration.executionplan.entity.ExecutionPlan;
import com.elit.outildedecision.collaboration.executionplan.repository.ExecutionPlanRepository;
import com.elit.outildedecision.collaboration.multicriteria.entity.MultiCriteriaAnalysis;
import com.elit.outildedecision.collaboration.multicriteria.exception.MulticriteriaNotFoundException;
import com.elit.outildedecision.collaboration.multicriteria.repository.MultiCriteriaAnalysisRepository;
import com.elit.outildedecision.collaboration.predictibility.entity.PredictibilityAnalysis;
import com.elit.outildedecision.collaboration.predictibility.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.collaboration.predictibility.repository.PredictibilityAnalysisRepository;
import com.elit.outildedecision.collaboration.report.dto.ReportResponse;
import com.elit.outildedecision.collaboration.report.entity.Report;
import com.elit.outildedecision.collaboration.report.entity.ReportContent;
import com.elit.outildedecision.collaboration.report.exception.ReportNotFoundException;
import com.elit.outildedecision.collaboration.report.mapper.ReportMapper;
import com.elit.outildedecision.collaboration.report.repository.ReportContentRepository;
import com.elit.outildedecision.collaboration.report.repository.ReportRepository;
import com.elit.outildedecision.collaboration.report.service.ReportService;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.exception.ConflictException;
import com.elit.outildedecision.shared.exception.ExecutionPlanNotFoundException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.project.repository.ProjectRepository;
import com.elit.outildedecision.shared.util.ReportTypes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.elit.outildedecision.ai.result.repository.ResultIaRepository;
import com.elit.outildedecision.audit.repository.AuditRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLConnection;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of {@link ReportService}: stores, streams, lists and deletes the synthesis
 * reports attached to a project's analyses. Report bytes are persisted in the database
 * ({@code report_content} table) rather than on the filesystem, so they survive container
 * restarts and are shared across replicas.
 */
@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
  /**
   * The message to display when the project with the specified ID cannot be found.
   */
  private static final String PROJECT_NOT_FOUND = "Le projet avec l'ID %d n'existe pas.";
  /**
   * The message to display when the report with the specified ID cannot be found.
   */
  private static final String REPORT_NOT_FOUND = "Le rapport avec l'ID %d n'existe pas.";
  /**
   * The default MIME type used when the report content type cannot be guessed.
   */
  private static final String DEFAULT_MIME_TYPE = "application/octet-stream";
  /**
   * Repository instance for accessing report data.
   */
  private final ReportRepository reportRepository;
  /**
   * Repository instance for accessing the binary content of reports.
   */
  private final ReportContentRepository reportContentRepository;
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
   * Mapper between report entities and their response DTOs.
   */
  private final ReportMapper reportMapper;
  /**
   * Repository instance for accessing AI result data.
   */
  private final ResultIaRepository resultIaRepository;
  /**
   * Repository instance for accessing audit data.
   */
  private final AuditRepository auditRepository;
  /**
   * Base folder used to store uploaded files.
   */
  @Value("${file.upload-dir}")
  private String storingFolder;

  /**
   * Constructs a new ReportServiceImpl with the specified collaborators.
   *
   * @param reportRepository                 The repository instance for accessing report data.
   * @param reportContentRepository          The repository instance for accessing report binary content.
   * @param projectRepository                The repository instance for accessing project data.
   * @param multiCriteriaAnalysisRepository  The repository instance for accessing multi-criteria analysis data.
   * @param predictibilityAnalysisRepository The repository instance for accessing predictibility analysis data.
   * @param executionPlanRepository          The repository instance for accessing execution plan data.
   * @param reportMapper                     The mapper between report entities and their response DTOs.
   */
  public ReportServiceImpl(
    final ReportRepository reportRepository,
    final ReportContentRepository reportContentRepository,
    final ProjectRepository projectRepository,
    final MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository,
    final PredictibilityAnalysisRepository predictibilityAnalysisRepository,
    final ExecutionPlanRepository executionPlanRepository,
    final ReportMapper reportMapper,
    final ResultIaRepository resultIaRepository,
    final AuditRepository auditRepository) {
    this.reportRepository = reportRepository;
    this.reportContentRepository = reportContentRepository;
    this.projectRepository = projectRepository;
    this.multiCriteriaAnalysisRepository = multiCriteriaAnalysisRepository;
    this.predictibilityAnalysisRepository = predictibilityAnalysisRepository;
    this.executionPlanRepository = executionPlanRepository;
    this.reportMapper = reportMapper;
    this.resultIaRepository = resultIaRepository;
    this.auditRepository = auditRepository;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public String uploadReport2(final Long projectId,
                              final Long analyseId,
                              final String type,
                              final MultipartFile file,
                              final Integer iteration) throws ProjectNotFoundException,
    PredictibilityAnalysisNotFoundException,
    MulticriteriaNotFoundException,
    ExecutionPlanNotFoundException, IOException {
    log.info("Report upload requested for projectId={} analyseId={} type={} iteration={}",
      projectId, analyseId, type, iteration);
    Project project = findProject(projectId);
    ReportTypes reportType = parseReportType(type);
    assertAnalyseExists(reportType, analyseId, projectId);

    // Include projectId in the duplicate key: different projects can reuse analysis IDs.
    Report reportFromDB = reportRepository.findReportByProjectAndAnalyseAndTypeAndIteration(
      projectId, analyseId, reportType, iteration);
    if (reportFromDB != null) {
      log.warn("Report already generated for projectId={} analyseId={} type={} iteration={}",
        projectId, analyseId, reportType, iteration);
      throw new ConflictException("Le rapport a déjà été généré pour cette analyse.");
    }

    byte[] bytes = file.getBytes();
    String filePath = buildLogicalReportPath(project, file);
    Report report =
      Report.builder()
        .type(reportType)
        .reportPath(filePath)
        .analyseId(analyseId)
        .project(project)
        .iteration(iteration)
        .build();
    Report savedReport = reportRepository.save(report);
    reportContentRepository.save(new ReportContent(savedReport.getReportId(), bytes));
    log.info("Report saved for projectId={} analyseId={} type={} iteration={}",
      projectId, analyseId, type, iteration);
    return "Fichier enregistré avec succès.";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public String uploadAndUpdateReport2(final Long projectId,
                                       final Long analyseId,
                                       final String type,
                                       final MultipartFile file,
                                       final Integer iteration) throws ProjectNotFoundException,
    PredictibilityAnalysisNotFoundException,
    MulticriteriaNotFoundException,
    ExecutionPlanNotFoundException, IOException {
    log.info("Report update requested for projectId={} analyseId={} type={} iteration={}",
      projectId, analyseId, type, iteration);
    Project project = findProject(projectId);
    ReportTypes reportType = parseReportType(type);
    assertAnalyseExists(reportType, analyseId, projectId);

    Report reportFromDB = reportRepository.findReportByProjectAndAnalyseAndTypeAndIteration(
      projectId, analyseId, reportType, iteration);
    if (reportFromDB == null) {
      log.warn("No report to update for projectId={} analyseId={} type={} iteration={}",
        projectId, analyseId, type, iteration);
      throw new ConflictException("Le rapport n'existe pas pour cette analyse.");
    }

    // The report is already scoped to this project by the repository query.
    reportFromDB.setReportPath(buildLogicalReportPath(project, file));
    reportRepository.save(reportFromDB);
    reportContentRepository.save(new ReportContent(reportFromDB.getReportId(), file.getBytes()));
    log.info("Report updated for projectId={} analyseId={} type={} iteration={}",
      projectId, analyseId, type, iteration);
    return "Le fichier a été mis à jours avec succès.";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(readOnly = true)
  public void previewReport(final Long reportId,
                            final HttpServletResponse response) throws ReportNotFoundException,
    IOException {
    log.info("Report preview requested for reportId={}", reportId);
    Report report =
      reportRepository
        .findById(reportId)
        .orElseThrow(() -> new ReportNotFoundException(String.format(REPORT_NOT_FOUND, reportId)));

    streamReport(report, response, "inline");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(readOnly = true)
  public void downloadReport(final Long reportId,
                             final HttpServletResponse response) throws IOException,
    ReportNotFoundException {
    log.info("Report download requested for reportId={}", reportId);
    Report report =
      reportRepository
        .findById(reportId)
        .orElseThrow(() -> new ReportNotFoundException(String.format(REPORT_NOT_FOUND, reportId)));

    streamReport(report, response, "attachment");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(readOnly = true)
  public List<ReportResponse> getReportsByProjectId(final Long projectId) throws ProjectNotFoundException {
    log.info("Reports listing requested for projectId={}", projectId);
    Project project = findProject(projectId);

    String folderPath = StringUtils.cleanPath(storingFolder + project.getFolderPath());

    List<Report> reportsList = reportRepository.findReportsByProjectId(project.getProjectId());
    return reportsList.stream()
      .map(report -> {
        ReportResponse response = reportMapper.toResponse(report);
        response.setTitle(report.getReportPath().replace(folderPath + "/", ""));
        return response;
      })
      .toList();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public String deleteReportById(final Long reportId) throws ReportNotFoundException {
    log.info("Report deletion requested for reportId={}", reportId);
    Report report =
      reportRepository
        .findById(reportId)
        .orElseThrow(() -> new ReportNotFoundException(String.format(REPORT_NOT_FOUND, reportId)));

    if (reportContentRepository.existsById(report.getReportId())) {
      reportContentRepository.deleteById(report.getReportId());
    }
    reportRepository.deleteById(reportId);
    log.info("Report deleted for reportId={}", reportId);
    return "Fichier supprimé avec succès";
  }

  /**
   * Loads a project or throws a {@link ProjectNotFoundException} with the canonical message.
   *
   * @param projectId the project id
   * @return the project entity
   * @throws ProjectNotFoundException if the project does not exist
   */
  private Project findProject(final Long projectId) throws ProjectNotFoundException {
    return projectRepository
      .findById(projectId)
      .orElseThrow(() -> new ProjectNotFoundException(String.format(PROJECT_NOT_FOUND, projectId)));
  }

  /**
   * Parses the existing API values (for example {@code multicriteria_report}). The HTTP
   * contract stays a String; the domain and persistence model use {@link ReportTypes}.
   */
  private ReportTypes parseReportType(final String value) {
    try {
      return ReportTypes.fromValue(value);
    } catch (IllegalArgumentException exception) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
    }
  }

  /**
   * Builds a logical path for the report title. The bytes are saved exclusively in MySQL.
   * Never trust client-provided folder components in a filename.
   */
  private String buildLogicalReportPath(final Project project, final MultipartFile file) {
    String suppliedName = file.getOriginalFilename();
    String fileName = suppliedName == null
      ? null : StringUtils.getFilename(StringUtils.cleanPath(suppliedName));
    if (!StringUtils.hasText(fileName) || ".".equals(fileName) || "..".equals(fileName)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nom de fichier de rapport invalide.");
    }
    String projectFolder = Objects.toString(project.getFolderPath(), "");
    String logicalFolder = StringUtils.cleanPath(storingFolder + projectFolder);
    return StringUtils.cleanPath(logicalFolder + "/" + fileName);
  }

  /**
   * Verifies the analysis exists AND belongs to the requested project.
   * A bare existsById check would allow attaching a report to the wrong project.
   */
  private void assertAnalyseExists(final ReportTypes type, final Long analyseId, final Long projectId)
      throws MulticriteriaNotFoundException,
      PredictibilityAnalysisNotFoundException,
      ExecutionPlanNotFoundException {
    switch (type) {
      case MULTI_CRITERIA_ANALYSIS -> {
        if (multiCriteriaAnalysisRepository.findById(analyseId)
            .filter(analysis -> belongsToProject(analysis, projectId)).isEmpty()) {
          throw new MulticriteriaNotFoundException("Cette analyse multicritère n'existe pas.");
        }
      }
      case PREDICTIBILITY_ANALYSIS -> {
        if (predictibilityAnalysisRepository.findById(analyseId)
            .filter(analysis -> belongsToProject(analysis, projectId)).isEmpty()) {
          throw new PredictibilityAnalysisNotFoundException("Cette analyse de prévisibilité n'existe pas.");
        }
      }
      case EXECUTION_PLAN -> {
        if (executionPlanRepository.findById(analyseId)
            .filter(plan -> belongsToProject(plan, projectId)).isEmpty()) {
          throw new ExecutionPlanNotFoundException("Ce plan d'exécution n'existe pas.");
        }
      }
      case AI_ANALYSIS -> {
        if (resultIaRepository.findById(analyseId)
            .filter(result -> result.getProject() != null
                && Objects.equals(result.getProject().getProjectId(), projectId)).isEmpty()) {
          throw new ResponseStatusException(HttpStatus.NOT_FOUND,
            "Le résultat IA est introuvable pour ce projet.");
        }
      }
      case AUDIT -> {
        if (auditRepository.findById(analyseId)
            .filter(audit -> audit.getProject() != null
                && Objects.equals(audit.getProject().getProjectId(), projectId)).isEmpty()) {
          throw new ResponseStatusException(HttpStatus.NOT_FOUND,
            "L'audit est introuvable pour ce projet.");
        }
      }
    }
  }

  private boolean belongsToProject(final MultiCriteriaAnalysis analysis, final Long projectId) {
    return analysis.getProject() != null
      && Objects.equals(analysis.getProject().getProjectId(), projectId);
  }

  private boolean belongsToProject(final PredictibilityAnalysis analysis, final Long projectId) {
    return analysis.getMultiCriteriaAnalysis() != null
      && belongsToProject(analysis.getMultiCriteriaAnalysis(), projectId);
  }

  private boolean belongsToProject(final ExecutionPlan plan, final Long projectId) {
    return plan.getPredictibilityAnalysis() != null
      && belongsToProject(plan.getPredictibilityAnalysis(), projectId);
  }

  /**
   * Streams the report bytes (loaded from the database) to the response using the given
   * Content-Disposition prefix.
   *
   * @param report      the report whose content must be streamed
   * @param response    the servlet response to write the binary content to
   * @param disposition the Content-Disposition prefix ({@code inline} or {@code attachment})
   * @throws ReportNotFoundException if the content backing the report is missing in the database
   * @throws IOException             if an I/O error occurs while writing the response
   */
  private void streamReport(final Report report,
                            final HttpServletResponse response,
                            final String disposition) throws ReportNotFoundException, IOException {
    ReportContent reportContent = reportContentRepository
      .findById(report.getReportId())
      .orElseThrow(() -> {
        log.error("Report content is missing in database for reportId={}", report.getReportId());
        return new ReportNotFoundException("Le rapport n'existe pas.");
      });

    String fileName = StringUtils.getFilename(report.getReportPath());
    String mimeType = URLConnection.guessContentTypeFromName(fileName);
    if (mimeType == null) {
      mimeType = DEFAULT_MIME_TYPE;
    }

    byte[] content = reportContent.getContent();
    response.setContentType(mimeType);
    response.setHeader(
      "Content-Disposition", String.format("%s; filename=\"%s\"", disposition, fileName));
    response.setContentLength(content.length);
    response.getOutputStream().write(content);
  }
}
