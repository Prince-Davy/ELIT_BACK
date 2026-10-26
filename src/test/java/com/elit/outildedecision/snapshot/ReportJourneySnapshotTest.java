package com.elit.outildedecision.snapshot;

import static org.assertj.core.api.Assertions.assertThat;

import com.azure.communication.email.EmailClient;
import com.elit.outildedecision.collaboration.multicriteria.entity.MultiCriteriaAnalysis;
import com.elit.outildedecision.shared.report.entity.Report;
import com.elit.outildedecision.shared.report.entity.ReportContent;
import com.elit.outildedecision.shared.report.repository.ReportContentRepository;
import com.elit.outildedecision.shared.report.repository.ReportRepository;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.project.repository.ProjectRepository;
import com.elit.outildedecision.shared.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import com.elit.outildedecision.shared.util.ReportTypes;


/**
 * Snapshot journey for the report endpoints (ENDPOINTS_BACKEND §5):
 * {@code /report/upload/{projectId}/{analyseId}/{type}/{iteration}} (POST + {@code /update} PUT),
 * {@code /report/{projectId}} (list), {@code /report/preview|download/{reportId}} (binary),
 * {@code DELETE /report/{reportId}}.
 *
 * <p>A {@link MultiCriteriaAnalysis} and a {@link Report} backed by database bytes are
 * seeded so the upload, list, binary and delete flows are deterministic. Binary endpoints are
 * asserted by status + Content-Type + size > 0 (§10.6); the list DTO is snapshotted (path-derived
 * title and generated ids/dates tolerated); plain-text bodies are frozen by string equality; one
 * not-found error payload is captured for the global handler.
 */
@Transactional
class ReportJourneySnapshotTest extends SnapshotTestSupport {

  private static final String OWNER_EMAIL = "report-journey-owner@elit.test";
  private static final String OWNER_PASSWORD = "Azerty1234!?";
  private static final String TYPE = ReportTypes.MULTI_CRITERIA_ANALYSIS.toString();
  private static final Path UPLOAD_DIR = Path.of("target", "test-uploads", "files");

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ProjectRepository projectRepository;
  @Autowired
  private ReportRepository reportRepository;
  @Autowired
  private ReportContentRepository reportContentRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @PersistenceContext
  private EntityManager entityManager;

  @MockitoBean
  private EmailClient emailClient;

  private Long projectId;
  private Long analyseId;
  private Long reportId;

  @BeforeEach
  void seed() throws Exception {
    MailMocks.stubSuccessfulSend(emailClient);

    User owner = userRepository.save(User.builder()
        .username("user")
        .userFirstName("John")
        .userLastName("Doe")
        .userEmail(OWNER_EMAIL)
        .userPassword(passwordEncoder.encode(OWNER_PASSWORD))
        .isEmailVerified(true)
        .isUserActive(true)
        .isAdmin(false)
        .build());

    Project project = projectRepository.save(Project.builder()
        .name("Report Pilot")
        .description("Deterministic seed for the report snapshot journey")
        .chefId(owner.getUserId())
        .folderPath("")
        .build());
    projectId = project.getProjectId();

    MultiCriteriaAnalysis analysis = new MultiCriteriaAnalysis();
    analysis.setProject(project);
    analysis.setMultiCriteriaAnalysisIteration(1);
    analysis.setIsFinished(false);
    entityManager.persist(analysis);
    analyseId = analysis.getMultiCriteriaAnalysisId();

    // The logical path drives the report title and download filename; the bytes live in report_content.
    Path stored = UPLOAD_DIR.resolve("report.pdf");

    Report report = reportRepository.save(Report.builder()
        .type(ReportTypes.MULTI_CRITERIA_ANALYSIS)
        .reportPath(stored.toString())
        .analyseId(analyseId)
        .project(project)
        .iteration(1)
        .build());
    reportId = report.getReportId();
    reportContentRepository.save(
        new ReportContent(reportId, "fixed-report-content".getBytes(StandardCharsets.UTF_8)));

    entityManager.flush();
    entityManager.clear();
  }

  @Test
  void getReportsByProjectShouldReturnSeededReport() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);

    MvcResult result = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .get("/report/{projectId}", projectId), token))
        .andReturn();

    // Title comes from a logical path; id/creationDate are generated and tolerated.
    assertSnapshot(result, "report/GET_report_byProject.json",
        SnapshotComparators.tolerating("[0].id", "[0].title", "[0].creationDate"));
  }

  @Test
  void previewReportShouldStreamInlineBinaryContent() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);

    MvcResult result = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .get("/report/preview/{reportId}", reportId), token))
        .andReturn();

    assertThat(result.getResponse().getStatus()).isEqualTo(200);
    assertThat(result.getResponse().getContentAsByteArray()).isNotEmpty();
    assertThat(result.getResponse().getHeader("Content-Disposition")).contains("inline");
  }

  @Test
  void downloadReportShouldStreamBinaryContent() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);

    MvcResult result = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .get("/report/download/{reportId}", reportId), token))
        .andReturn();

    assertThat(result.getResponse().getStatus()).isEqualTo(200);
    assertThat(result.getResponse().getContentAsByteArray()).isNotEmpty();
    assertThat(result.getResponse().getHeader("Content-Disposition")).contains("attachment");
  }

  @Test
  void uploadReportForNewIterationShouldReturnSuccessMessage() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);
    MockMultipartFile file = new MockMultipartFile(
        "file", "new-report.pdf", MediaType.APPLICATION_PDF_VALUE,
        "fixed-new-report".getBytes(StandardCharsets.UTF_8));

    MvcResult result = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .multipart("/report/upload/{projectId}/{analyseId}/{type}/{iteration}",
                projectId, analyseId, TYPE, 2)
            .file(file), token))
        .andReturn();

    assertThat(result.getResponse().getStatus()).isEqualTo(200);
    assertThat(result.getResponse().getContentAsString(StandardCharsets.UTF_8))
        .isEqualTo("Fichier enregistré avec succès.");
  }

  @Test
  void uploadReportForExistingIterationShouldReturnConflict() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);
    MockMultipartFile file = new MockMultipartFile(
        "file", "dup-report.pdf", MediaType.APPLICATION_PDF_VALUE,
        "fixed-dup-report".getBytes(StandardCharsets.UTF_8));

    MvcResult result = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .multipart("/report/upload/{projectId}/{analyseId}/{type}/{iteration}",
                projectId, analyseId, TYPE, 1)
            .file(file), token))
        .andReturn();

    // A report already exists for (analyseId, type, iteration=1) → 409 + uniform error payload
    // (the service throws ConflictException, mapped by the global handler).
    assertThat(result.getResponse().getStatus()).isEqualTo(409);
    assertSnapshot(result, "report/POST_report_upload_conflict.json");
  }

  @Test
  void deleteReportShouldReturnSuccessMessage() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);

    MvcResult result = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .delete("/report/{reportId}", reportId), token))
        .andReturn();

    assertThat(result.getResponse().getStatus()).isEqualTo(200);
    assertThat(result.getResponse().getContentAsString(StandardCharsets.UTF_8))
        .isEqualTo("Fichier supprimé avec succès");
  }

  @Test
  void previewUnknownReportShouldReturnNotFoundError() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);

    MvcResult result = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .get("/report/preview/{reportId}", 999999L), token))
        .andReturn();

    // Representative global-handler error payload for this journey: ReportNotFound → 404.
    assertThat(result.getResponse().getStatus()).isEqualTo(404);
    assertSnapshot(result, "report/GET_report_preview_notFound.json");
  }
}
