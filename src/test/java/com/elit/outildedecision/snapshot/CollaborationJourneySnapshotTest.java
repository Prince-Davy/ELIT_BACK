package com.elit.outildedecision.snapshot;

import static org.assertj.core.api.Assertions.assertThat;

import com.azure.communication.email.EmailClient;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

/**
 * Snapshot journey for the collaborative parcours endpoints (ENDPOINTS_BACKEND §6): the survey
 * tail ({@code /survey/question}, {@code /survey/answer/{userId}}) plus the now-resolved anomaly
 * §4.2 ({@code GET /brainstorming/abstract/{prevId}} is officialised in Étape 11).
 *
 * <p>The richer collaborative reads (multicriteria score, predictibility, execution plan,
 * brainstorming) require deep multi-entity graphs (form steps, predictibility analysis, votes,
 * execution plan questions) and are migrated with full seeds in Étapes 13-16; here the goal is
 * to freeze the deterministic survey contract, the §4.2 single-segment route, and one
 * global-handler error. Mail is mocked so the journey never touches the network.
 */
@Transactional
class CollaborationJourneySnapshotTest extends SnapshotTestSupport {

  private static final String OWNER_EMAIL = "user@user.com";
  private static final String OWNER_PASSWORD = "Azerty1234!?";

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;

  @MockitoBean
  private EmailClient emailClient;

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
  }

  @Test
  void getSurveyQuestionsShouldReturnEmptyListWhenNoneSeeded() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);

    MvcResult result = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .get("/survey/question"), token))
        .andReturn();

    // No survey question seeded in the test DB → the raw-entity list is empty. Freezes the
    // empty-collection contract of this endpoint.
    assertThat(result.getResponse().getStatus()).isEqualTo(200);
    assertSnapshot(result, "collaboration/GET_survey_question_empty.json");
  }

  @Test
  void getSurveyAnswersForUserWithoutAnswersShouldReturnEmptyList() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);

    MvcResult result = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .get("/survey/answer/{userId}", userId), token))
        .andReturn();

    assertThat(result.getResponse().getStatus()).isEqualTo(200);
    assertSnapshot(result, "collaboration/GET_survey_answer_empty.json");
  }

  @Test
  void getSurveyAnswersForUnknownUserShouldReturnNotFoundError() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);

    MvcResult result = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .get("/survey/answer/{userId}", 999999L), token))
        .andReturn();

    // Representative global-handler error payload for this journey: UserNotFound → 404.
    assertThat(result.getResponse().getStatus()).isEqualTo(404);
    assertSnapshot(result, "collaboration/GET_survey_answer_notFound.json");
  }

  @Test
  void brainstormingAbstractWithoutProjectIdResolvesByAnalysisId() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);

    // Anomaly §4.2 resolved (Étape 11): the front (PlanExecutionApi) calls
    // GET /brainstorming/abstract/{prevId} with a single segment. The back now exposes a
    // dedicated route that resolves the brainstorming abstract from the predictibility analysis
    // id alone. With no analysis seeded, prevId=123 is unknown → PredictibilityAnalysisNotFound
    // → 404 via the global handler. This freezes the officialised single-segment contract.
    MvcResult result = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .get("/brainstorming/abstract/{prevId}", 123L), token))
        .andReturn();

    assertThat(result.getResponse().getStatus()).isEqualTo(404);
  }
}
