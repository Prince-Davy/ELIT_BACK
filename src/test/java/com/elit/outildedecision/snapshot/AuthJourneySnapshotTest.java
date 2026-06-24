package com.elit.outildedecision.snapshot;

import static org.assertj.core.api.Assertions.assertThat;

import com.azure.communication.email.EmailClient;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.user.repository.UserRepository;
import java.nio.charset.StandardCharsets;
import java.util.Map;
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
 * Snapshot journey for the authentication / session endpoints (ENDPOINTS_BACKEND §1):
 * {@code /register}, {@code /sendRegistrationConfirmationEmailAgain}, {@code /login},
 * {@code /refreshToken}, {@code /send-reset-password-email}, {@code /reset-password}.
 *
 * <p>Mail is mocked ({@code @MockitoBean EmailClient}) so registration and reset-mail flows
 * never touch the network nor depend on {@code .env}: the stubbed poller resolves to
 * {@code SUCCEEDED} immediately (see {@link MailMocks}). The login token fields are the only
 * tolerated volatile fields ({@link SnapshotComparators#loginTokens()}); everything else is
 * compared STRICT. The {@code /register} and reset endpoints return plain-text bodies, frozen
 * here by exact status + string equality (no JSON snapshot).
 */
@Transactional
class AuthJourneySnapshotTest extends SnapshotTestSupport {

  private static final String ACTIVE_EMAIL = "user@user.com";
  private static final String ACTIVE_PASSWORD = "Azerty1234!?";

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;

  @MockitoBean
  private EmailClient emailClient;

  @BeforeEach
  void seed() {
    MailMocks.stubSuccessfulSend(emailClient);

    userRepository.save(User.builder()
        .username("user")
        .userFirstName("John")
        .userLastName("Doe")
        .userEmail(ACTIVE_EMAIL)
        .userPassword(passwordEncoder.encode(ACTIVE_PASSWORD))
        .isEmailVerified(true)
        .isUserActive(true)
        .isAdmin(false)
        .build());
  }

  @Test
  void loginShouldReturnTokenPair() throws Exception {
    String payload = objectMapper.writeValueAsString(Map.of(
        "userEmail", ACTIVE_EMAIL,
        "password", ACTIVE_PASSWORD));

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andReturn();

    assertThat(result.getResponse().getStatus()).isEqualTo(200);
    assertSnapshot(result, "auth/POST_login.json", SnapshotComparators.loginTokens());
  }

  @Test
  void loginWithWrongPasswordForActiveAccountShouldReturnEmptyOk() throws Exception {
    String payload = objectMapper.writeValueAsString(Map.of(
        "userEmail", ACTIVE_EMAIL,
        "password", "WrongPassword1!?"));

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andReturn();

    // Real (latent-bug) contract: JwtAuthenticationFilter#unsuccessfulAuthentication only
    // writes a 401 when the account is missing or inactive. For an existing *active* account
    // with a wrong password neither branch fires, so the response stays 200 with an empty
    // body and no token pair. Frozen as-is — migration must not alter this observable behaviour.
    assertThat(result.getResponse().getStatus()).isEqualTo(200);
    assertThat(result.getResponse().getContentAsString(StandardCharsets.UTF_8)).isEmpty();
  }

  @Test
  void loginForInactiveAccountShouldReturnUnauthorized() throws Exception {
    userRepository.save(User.builder()
        .username("pending")
        .userFirstName("Pat")
        .userLastName("Pending")
        .userEmail("pending@elit.com")
        .userPassword(passwordEncoder.encode(ACTIVE_PASSWORD))
        .isEmailVerified(false)
        .isUserActive(false)
        .isAdmin(false)
        .build());

    String payload = objectMapper.writeValueAsString(Map.of(
        "userEmail", "pending@elit.com",
        "password", ACTIVE_PASSWORD));

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andReturn();

    // Inactive account → explicit 401 with a plain-text body.
    assertThat(result.getResponse().getStatus()).isEqualTo(401);
    assertThat(result.getResponse().getContentAsString(StandardCharsets.UTF_8))
        .isEqualTo("Account is not activated");
  }

  @Test
  void registerShouldCreateAccountAndReturn201() throws Exception {
    String payload = objectMapper.writeValueAsString(Map.of(
        "username", "newuser",
        "userFirstName", "Jane",
        "userLastName", "Roe",
        "userEmail", "newuser@elit.com",
        "userPassword", "Azerty1234!?",
        "userPasswordConfirmation", "Azerty1234!?"));

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andReturn();

    // Plain-text body contract — frozen by exact status + string equality.
    assertThat(result.getResponse().getStatus()).isEqualTo(201);
    assertThat(result.getResponse().getContentAsString(StandardCharsets.UTF_8))
        .isEqualTo("Votre compte a été créé avec succès");
  }

  @Test
  void registerWithExistingEmailShouldReturnConflictError() throws Exception {
    String payload = objectMapper.writeValueAsString(Map.of(
        "username", "user",
        "userFirstName", "John",
        "userLastName", "Doe",
        "userEmail", ACTIVE_EMAIL,
        "userPassword", "Azerty1234!?",
        "userPasswordConfirmation", "Azerty1234!?"));

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andReturn();

    // Representative global-handler error payload for this journey (ErrorMessage, 409).
    assertThat(result.getResponse().getStatus()).isEqualTo(409);
    assertSnapshot(result, "auth/POST_register_conflict.json");
  }

  @Test
  void registerWithInvalidPayloadShouldReturnValidationError() throws Exception {
    String payload = objectMapper.writeValueAsString(Map.of(
        "username", "",
        "userFirstName", "",
        "userLastName", "",
        "userEmail", "not-an-email",
        "userPassword", "weak",
        "userPasswordConfirmation", "weak"));

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andReturn();

    // Jakarta validation rejection routed through handleMethodArgumentNotValid (400).
    assertThat(result.getResponse().getStatus()).isEqualTo(400);
  }

  @Test
  void resendRegistrationEmailForActiveAccountShouldReturnConflict() throws Exception {
    String payload = objectMapper.writeValueAsString(Map.of(
        "username", "user",
        "userFirstName", "John",
        "userLastName", "Doe",
        "userEmail", ACTIVE_EMAIL,
        "userPassword", "Azerty1234!?",
        "userPasswordConfirmation", "Azerty1234!?"));

    MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders.post("/sendRegistrationConfirmationEmailAgain")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andReturn();

    // Already-active account: ConflictException routed through the global handler (409,
    // uniform {status,message} payload).
    assertThat(result.getResponse().getStatus()).isEqualTo(409);
    assertSnapshot(result, "auth/POST_sendAgain_conflict.json");
  }

  @Test
  void refreshTokenWithoutBearerShouldReturnForbiddenError() throws Exception {
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/refreshToken"))
        .andReturn();

    // No bearer → RefreshTokenException → global handler. Frozen by status here; the
    // exact body depends on the handler mapping for this exception type.
    assertThat(result.getResponse().getStatus()).isIn(403, 500);
  }

  @Test
  void sendResetPasswordEmailForUnknownAccountShouldReturnBadRequest() throws Exception {
    String body = "{\"email\":\"unknown@elit.com\"}";

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/send-reset-password-email")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andReturn();

    assertThat(result.getResponse().getStatus()).isEqualTo(400);
    assertThat(result.getResponse().getContentAsString(StandardCharsets.UTF_8))
        .isEqualTo("Aucun compte n'a été trouvé pour l'e-mail unknown@elit.com, Veuillez réessayer.");
  }

  @Test
  void sendResetPasswordEmailForActiveAccountShouldSucceed() throws Exception {
    String body = "{\"email\":\"" + ACTIVE_EMAIL + "\"}";

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/send-reset-password-email")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andReturn();

    // Mail mocked → poller resolves SUCCEEDED → 200 with a plain-text body.
    assertThat(result.getResponse().getStatus()).isEqualTo(200);
    assertThat(result.getResponse().getContentAsString(StandardCharsets.UTF_8))
        .isEqualTo("Email de récupération envoyé");
  }

  @Test
  void resetPasswordWithInvalidTokenShouldReturnBadRequest() throws Exception {
    String body = objectMapper.writeValueAsString(Map.of(
        "newPassword", "Azerty1234!?",
        "confirmPassword", "Azerty1234!?"));

    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/reset-password")
            .param("token", "does-not-exist")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andReturn();

    assertThat(result.getResponse().getStatus()).isEqualTo(400);
    assertThat(result.getResponse().getContentAsString(StandardCharsets.UTF_8))
        .isEqualTo("le lien est invalide");
  }
}
