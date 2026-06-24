package com.elit.outildedecision.snapshot;

import static org.assertj.core.api.Assertions.assertThat;

import com.azure.communication.email.EmailClient;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.user.repository.UserRepository;
import java.nio.charset.StandardCharsets;
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

/**
 * Snapshot journey for the user / profile endpoints (ENDPOINTS_BACKEND §2):
 * {@code /user/users}, {@code /user/{userId}}, {@code /user/avatar/{userId}} (GET/PUT/DELETE),
 * {@code /user/info/{userId}}, {@code /user/password/{userId}}.
 *
 * <p>A single deterministic user is seeded so the {@code /user/users} list snapshot is stable
 * (only the identity-generated {@code userId} is tolerated). Avatar GET is a binary endpoint —
 * asserted by status + body, not a JSON snapshot (§10.6). Multipart PUT endpoints use a fixed
 * {@link MockMultipartFile}; their plain-text responses are frozen by exact string equality.
 * Mail is mocked so no profile flow touches the network.
 */
@Transactional
class UserJourneySnapshotTest extends SnapshotTestSupport {

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
  void seed() throws Exception {
    MailMocks.stubSuccessfulSend(emailClient);
    // saveAvatar uses Files.copy without creating parents; the build-local upload dir must
    // exist. Created under target/ only (test profile), never on the host filesystem.
    java.nio.file.Files.createDirectories(java.nio.file.Path.of("target", "test-uploads"));

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
  void getAllUsersShouldReturnSeededUser() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);

    MvcResult result = mockMvc.perform(withBearer(
            MockMvcRequestBuilders.get("/user/users"), token))
        .andReturn();

    // userId is identity-generated (H2 counter not reset on rollback) → tolerated; the rest
    // of the contract (field set, order, null avatar) is STRICT.
    assertSnapshot(result, "user/GET_user_users.json",
        SnapshotComparators.tolerating("[0].userId"));
  }

  @Test
  void searchUsersByKeywordShouldFilter() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);

    MvcResult result = mockMvc.perform(withBearer(
            MockMvcRequestBuilders.get("/user/users").param("keyword", "user"), token))
        .andReturn();

    assertSnapshot(result, "user/GET_user_users_keyword.json",
        SnapshotComparators.tolerating("[0].userId"));
  }

  @Test
  void getUserByIdShouldReturnProfile() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);

    MvcResult result = mockMvc.perform(withBearer(
            MockMvcRequestBuilders.get("/user/{userId}", userId), token))
        .andReturn();

    assertSnapshot(result, "user/GET_user_byId.json",
        SnapshotComparators.tolerating("userId"));
  }

  @Test
  void getAvatarWhenNoneSetShouldReturnPlaceholderMessage() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);

    MvcResult result = mockMvc.perform(withBearer(
            MockMvcRequestBuilders.get("/user/avatar/{userId}", userId), token))
        .andReturn();

    // Binary endpoint (§10.6): when no avatar file is set the service still answers 200 with
    // the "not found" placeholder message. Frozen by status + plain body, no JSON snapshot.
    assertThat(result.getResponse().getStatus()).isEqualTo(200);
    assertThat(result.getResponse().getContentAsString(StandardCharsets.UTF_8))
        .isEqualTo("La photo de profil n'existe pas");
  }

  @Test
  void deleteAvatarWhenNoneSetShouldReturnNotFoundError() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);

    MvcResult result = mockMvc.perform(withBearer(
            MockMvcRequestBuilders.delete("/user/avatar/{userId}", userId), token))
        .andReturn();

    // Representative global-handler error for this journey: AvatarNotFoundException → 404.
    assertThat(result.getResponse().getStatus()).isEqualTo(404);
    assertSnapshot(result, "user/DELETE_user_avatar_notFound.json");
  }

  @Test
  void updateInfoShouldReturnSuccessMessage() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);

    MvcResult result = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .multipart("/user/info/{userId}", userId)
            .param("userFirstName", "Johnny")
            .param("userLastName", "Doe")
            .with(request -> {
              request.setMethod("PUT");
              return request;
            }), token))
        .andReturn();

    assertThat(result.getResponse().getStatus()).isEqualTo(200);
    assertThat(result.getResponse().getContentAsString(StandardCharsets.UTF_8))
        .isEqualTo("Vos modifications ont bien été enregistrées");
  }

  @Test
  void updateAvatarShouldStoreFileAndReturnSuccessMessage() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);
    MockMultipartFile avatar = new MockMultipartFile(
        "userAvatar", "avatar.png", MediaType.IMAGE_PNG_VALUE, "fixed-avatar-bytes".getBytes(StandardCharsets.UTF_8));

    MvcResult result = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .multipart("/user/avatar/{userId}", userId)
            .file(avatar)
            .with(request -> {
              request.setMethod("PUT");
              return request;
            }), token))
        .andReturn();

    assertThat(result.getResponse().getStatus()).isEqualTo(200);
    assertThat(result.getResponse().getContentAsString(StandardCharsets.UTF_8))
        .isEqualTo("Votre photo a bien été modifiée.");
  }

  @Test
  void updatePasswordWithWrongOldPasswordShouldReturnBadRequest() throws Exception {
    String token = login(OWNER_EMAIL, OWNER_PASSWORD);

    MvcResult result = mockMvc.perform(withBearer(MockMvcRequestBuilders
            .multipart("/user/password/{userId}", userId)
            .param("oldPass", "WrongOldPass1!?")
            .param("newPass", "BrandNewPass1!?")
            .with(request -> {
              request.setMethod("PUT");
              return request;
            }), token))
        .andReturn();

    // Wrong current password → BadRequestException routed through the global handler (400,
    // uniform {status,message} payload).
    assertThat(result.getResponse().getStatus()).isEqualTo(400);
    assertSnapshot(result, "user/PUT_user_password_wrongOld.json");
  }
}
