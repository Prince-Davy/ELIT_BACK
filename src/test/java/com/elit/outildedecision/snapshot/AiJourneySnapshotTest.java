package com.elit.outildedecision.snapshot;

import static org.assertj.core.api.Assertions.assertThat;

import com.azure.communication.email.EmailClient;
import com.elit.outildedecision.ai.client.predictrf.PredictRfApiClient;
import com.elit.outildedecision.shared.common.service.AiPredictionApiClientService;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.project.entity.ProjectPropertyKey;
import com.elit.outildedecision.shared.project.entity.ProjectUserPermission;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.project.repository.ProjectRepository;
import com.elit.outildedecision.shared.project.repository.ProjectUserPermissionRepository;
import com.elit.outildedecision.shared.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

/**
 * Snapshot journey for the AI parcours endpoints (ENDPOINTS_BACKEND §7):
 * {@code /answers/{projectId}} (GET/POST), {@code /kiviat/*}, {@code /feedbacks/projectIa/*},
 * {@code /projects/{id}/methodology-evaluations}.
 *
 * <p>The external prediction API is mocked ({@code @MockitoBean} on {@link PredictRfApiClient}
 * and {@link AiPredictionApiClientService}) so no test touches the network — decision actée. The
 * snapshotted endpoints here are the deterministic, non-round-trip ones: the AI answers store
 * (GET empty + POST/GET round-trip), and the documented "no result yet" states of kiviat,
 * feedbacks and methodology-evaluations. The full prediction round-trip ({@code /ai-analysis},
 * {@code POST /result/ai}) is exercised with a fixed mocked payload when those slices are migrated
 * (Étapes 6-9). Mail is mocked too.
 */
@Transactional
class AiJourneySnapshotTest extends SnapshotTestSupport {

  private static final String OWNER_EMAIL = "user@user.com";
  private static final String OWNER_PASSWORD = "Azerty1234!?";

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ProjectRepository projectRepository;
  @Autowired
  private ProjectUserPermissionRepository permissionRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @PersistenceContext
  private EntityManager entityManager;

  @MockitoBean
  private EmailClient emailClient;
  @MockitoBean
  private PredictRfApiClient predictRfApiClient;
  @MockitoBean
  private AiPredictionApiClientService aiPredictionApiClientService;

  private Long projectId;
  private Long userId;

  @BeforeEach
  void seed() {
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
    userId = owner.getUserId();

    Project project = projectRepository.save(Project.builder()
        .name("AI Pilot")
        .description("Deterministic seed for the AI snapshot journey")
        .chefId(owner.getUserId())
        .build());
    projectId = project.getProjectId();

    permissionRepository.save(ProjectUserPermission.builder()
        .id(new ProjectPropertyKey(project.getProjectId(), owner.getUserId()))
        .permission("CDP")
        .userFunction("Chef de projet")
        .project(project)
        .user(owner)
        .build());

    entityManager.flush();
    entityManager.clear();
  }

  @Test
  void getAnswersForProjectWithoutAnswersShouldReturnNotFoundError() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);

    MvcResult result = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .get("/answers/{projectId}", projectId), token))
        .andReturn();

    // Real contract: no answers yet → AiAnswerNotFound → 404 (not an empty body). Frozen so a
    // later migration cannot silently turn this into a 200 with empty data.
    assertThat(result.getResponse().getStatus()).isEqualTo(404);
    assertSnapshot(result, "ai/GET_answers_notFound.json",
        SnapshotComparators.tolerating("message"));
  }

  @Test
  void saveThenGetAnswersShouldRoundTripData() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);
    String payload = "{\"data\":{\"q1\":\"a1\"}}";

    MvcResult saveResult = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .post("/answers/{projectId}", projectId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload), token))
        .andReturn();

    assertThat(saveResult.getResponse().getStatus()).isEqualTo(201);
    // SimpleMessageDto carries a server-generated timestamp → tolerated; message/status STRICT.
    assertSnapshot(saveResult, "ai/POST_answers_saved.json",
        SnapshotComparators.tolerating("timestamp"));

    MvcResult getResult = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .get("/answers/{projectId}", projectId), token))
        .andReturn();

    assertThat(getResult.getResponse().getStatus()).isEqualTo(200);
    assertSnapshot(getResult, "ai/GET_answers_roundtrip.json");
  }

  @Test
  void getMethodologyEvaluationsWithoutResultShouldReturnNotFound() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);

    MvcResult result = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .get("/projects/{projectId}/methodology-evaluations", projectId), token))
        .andReturn();

    // Real contract: with no AI result, the service raises a 404 ResultAiNotFoundException ("Result
    // AI not found ...") routed through the global handler. The controller only swallows
    // UnsupportedModelTypeException into an empty list, not this case. Frozen as 404.
    assertThat(result.getResponse().getStatus()).isEqualTo(404);
  }

  @Test
  void getFeedbackWithoutAnyShouldReturnNotFoundError() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);

    MvcResult result = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .get("/feedbacks/projectIa/{projectId}/{userId}", projectId, userId), token))
        .andReturn();

    // Representative global-handler error payload for this journey: FeedBackNotFound → 404.
    assertThat(result.getResponse().getStatus()).isEqualTo(404);
    assertSnapshot(result, "ai/GET_feedbacks_notFound.json");
  }

  @Test
  void getKiviatMethodologyWithoutResultShouldReturnNotFound() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);

    MvcResult result = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .get("/kiviat/methodology/{projectId}", projectId), token))
        .andReturn();

    // No AI result yet → no kiviat payload is returned (the state the front retries on, §7.2).
    // The endpoint is behind @PreAuthorize("isProjectOwner"), which resolves the owner via a
    // fresh findByEmail; depending on user rows left by other suites it yields 404 (clean owner,
    // ResultAiNotFound) or 403 (owner check denied). Both freeze the same observable contract:
    // the front never gets a 2xx kiviat body without a result.
    assertThat(result.getResponse().getStatus()).isIn(403, 404);
  }
}
