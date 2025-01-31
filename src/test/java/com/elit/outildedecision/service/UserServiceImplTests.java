package com.elit.outildedecision.service;

import com.azure.communication.email.EmailClient;
import com.azure.communication.email.models.EmailMessage;
import com.azure.communication.email.models.EmailSendResult;
import com.azure.communication.email.models.EmailSendStatus;
import com.azure.core.util.polling.LongRunningOperationStatus;
import com.azure.core.util.polling.PollResponse;
import com.azure.core.util.polling.SyncPoller;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.entity.VerificationToken;
import com.elit.outildedecision.exception.CouldNotSaveUserException;
import com.elit.outildedecision.exception.UserAlreadyExistsException;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.model.UserModel;
import com.elit.outildedecision.model.UserResponseModel;
import com.elit.outildedecision.repository.UserRepository;
import com.elit.outildedecision.security.AppUserPrincipal;
import com.elit.outildedecision.service.implementation.UserServiceImpl;
import com.elit.outildedecision.util.Constants;
import com.elit.outildedecision.util.ModelSamples;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserServiceImplTests {
  @Mock
  private UserRepository userRepository;
  @Mock
  private MultipartFile file;
  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private VerificationTokenService verificationTokenService;

  @Mock
  private EmailClient emailClientMock;

  @Captor
  private ArgumentCaptor<EmailMessage> emailMessageCaptor;

  private final String avatarUploadDir = "src/main/resources/avatars/";

  @InjectMocks
  private UserServiceImpl userServiceImpl;
  private static final String USER_EMAIL = "test@example.com";
  private static final Long USER_ID = 1L;
  private static final String OLD_PASS = "old_pass";
  private static final String NEW_PASS = "Azertyuio12!";
  private User user;

  @AfterAll
  static void cleanUp() {
      try (Stream<Path> files = Files.walk(Path.of("."))) {
        files
          .filter(path -> path.getFileName().toString().matches("null.*"))
          .map(Path::toFile)
          .forEach(file -> {
            boolean delete = file.delete();
            if (delete) {
              log.trace("File {} successfully deleted", file);
            }
            else {
              log.error("File {} could not be deleted, continuing anyway", file);
            }
          });
        ;
      } catch (IOException e) {
        log.error("Error cleaning up, will not reattempt it", e);
      }
  }

  @BeforeEach
  public void setUp() {
    user = new User();
    user.setUsername(USER_EMAIL);
    user.setUserEmail(USER_EMAIL);
    user.setUserId(1L);
    user.setUserPassword(passwordEncoder.encode(OLD_PASS));
    user.setUserPassword(passwordEncoder.encode(OLD_PASS));
  }

  @Test
  void loadUserByUsername_Success() {
    when(userRepository.findByUserEmailAndIsVerified(USER_EMAIL)).thenReturn(user);
    UserDetails result = userServiceImpl.loadUserByUsername(USER_EMAIL);
    assertEquals(USER_EMAIL, result.getUsername());
  }

  @Test
  void findByEmail_Success() {
    when(userRepository.findByUserEmail(USER_EMAIL)).thenReturn(user);
    User result = userServiceImpl.findByEmail(USER_EMAIL);
    assertEquals(result, user);
  }

  @Test
  void saveUser_ThrowsUserAlreadyExistsException() {
    when(userRepository.existsByUserEmail(USER_EMAIL)).thenReturn(true);
    UserModel usermodel = new UserModel();
    usermodel.setUserEmail(USER_EMAIL);
    usermodel.setUserPassword("");
    usermodel.setUserPasswordConfirmation("");
    assertThrows(UserAlreadyExistsException.class, () -> userServiceImpl.saveUser(usermodel));
  }

  @Test
  void saveUser_ThrowsCouldNotSaveUserException() {
    UserModel usermodel = new UserModel();
    usermodel.setUserEmail(USER_EMAIL);
    usermodel.setUserPassword("");
    usermodel.setUserPasswordConfirmation("");
    when(userRepository.save(user)).thenReturn(user);
    assertThrows(CouldNotSaveUserException.class, () -> userServiceImpl.saveUser(usermodel));
  }

  @Test
  void updatePasswordDifferent() throws Exception {
    UserModel usermodel = new UserModel();
    usermodel.setUserEmail(USER_EMAIL);
    usermodel.setUserPassword("123456789");
    usermodel.setUserPasswordConfirmation("987654321");
    assertThrows(CouldNotSaveUserException.class, () -> userServiceImpl.saveUser(usermodel));
  }

  @Test
  void updatePasswordUserById_httpNotAcceptable() throws Exception {
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    assertEquals(HttpStatus.NOT_ACCEPTABLE, userServiceImpl.updatePasswordUserById(USER_ID, OLD_PASS, NEW_PASS).getStatusCode());
  }

  @Test
  void updatePasswordUserById_UserNotFoundException() {
    when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
    assertThrows(UserNotFoundException.class, () -> userServiceImpl.updatePasswordUserById(USER_ID, OLD_PASS, NEW_PASS));
  }

  @Test
  void updatePasswordUserById_Success() throws Exception {
    user.setUserPassword(passwordEncoder.encode(OLD_PASS));
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    String currentPass = user.getUserPassword();
    when(passwordEncoder.matches(OLD_PASS, currentPass)).thenReturn(true);
    user.setUserPassword(passwordEncoder.encode(NEW_PASS));
    when(userRepository.save(user)).thenReturn(user);
    assertEquals(HttpStatus.OK, userServiceImpl.updatePasswordUserById(USER_ID, OLD_PASS, NEW_PASS).getStatusCode());
  }

  @Test
  void updateAvatarByIdUser_UserNotFoundException() {
    when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
    assertThrows(UserNotFoundException.class, () -> userServiceImpl.updateAvatarByIdUser(USER_ID, file));
  }

  @Test
  void updateAvatarByIdUser_Success() throws Exception {
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(userRepository.save(user)).thenReturn(user);
    MockMultipartFile file
            = new MockMultipartFile(
            "file",
            "hello.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "Hello, World!".getBytes()
    );
    assertEquals(HttpStatus.OK, userServiceImpl.updateAvatarByIdUser(USER_ID, file).getStatusCode());
  }

  @Test
  void updateInfosByIdUser_UserNotFoundException() {
    UserModel usermodel = new UserModel();
    usermodel.setUserEmail(USER_EMAIL);
    when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
    assertThrows(UserNotFoundException.class, () -> userServiceImpl.updateInfosByIdUser(USER_ID, "firstname", "lastname"));
  }

  @Test
  void updateInfosByIdUser_Success() throws Exception {
    UserModel usermodel = new UserModel();
    usermodel.setUserEmail(USER_EMAIL);
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(userRepository.save(user)).thenReturn(user);
    assertEquals(HttpStatus.OK, userServiceImpl.updateInfosByIdUser(USER_ID, "", "").getStatusCode());
  }

  @Test
  void getAllInfosUserById_Success() {
    when(userRepository.getById(USER_ID)).thenReturn(user);
    assertEquals(UserResponseModel.builder()
            .userId(user.getUserId())
            .userFirstName(user.getUserFirstName())
            .userLastName(user.getUserLastName())
            .username(user.getUsername())
            .userEmail(user.getUserEmail())
            .build(), userServiceImpl.getAllInfosUserById(USER_ID));
  }

  @Test
  void saveAvatar_Success() throws IOException {
    MockMultipartFile file
            = new MockMultipartFile(
            "avatar.png",
            "avatar.png",
            MediaType.IMAGE_PNG_VALUE,
            "avatar.png".getBytes());
    assertEquals("null" + file.getOriginalFilename(), userServiceImpl.saveAvatar(file));

  }

  @Test
  void findUsersByKeyworld_SuccesskeyworldNullFour() throws IOException {
    List<UserResponseModel> foundUsers = new ArrayList<>();
    String keyworld = null;
    user.setUserAvatarPath(avatarUploadDir + "avatar.png");
    List<User> users = new ArrayList<>();
    users.add(user);
    File file = new File(user.getUserAvatarPath());
    when(userRepository.findAll()).thenReturn(users);
    UserResponseModel userResponseModel =
            UserResponseModel.builder()
                    .userId(user.getUserId())
                    .userFirstName(user.getUserFirstName())
                    .userLastName(user.getUserLastName())
                    .username(user.getUsername())
                    .userEmail(user.getUserEmail())
                    .userAvatar(Files.readAllBytes(file.toPath()))
                    .build();
    foundUsers.add(userResponseModel);
    assertEquals(foundUsers, userServiceImpl.findUsersByKeyword(keyworld));
  }

  @Test
  void findUserByKeyworld_SuccessKeyworldNullThree() throws Exception {
    List<UserResponseModel> foundUsers = new ArrayList<>();
    String keyworld = "";
    user.setUserAvatarPath(null);
    List<User> users = new ArrayList<>();
    users.add(user);
    when(userRepository.findAll()).thenReturn(users);
    UserResponseModel userResponseModel =
            UserResponseModel.builder()
                    .userId(user.getUserId())
                    .userFirstName(user.getUserFirstName())
                    .userLastName(user.getUserLastName())
                    .username(user.getUsername())
                    .userEmail(user.getUserEmail())
                    .build();
    foundUsers.add(userResponseModel);
    assertEquals(foundUsers, userServiceImpl.findUsersByKeyword(keyworld));
  }

  @Test
  void findUsersByKeyworld_SuccesskeyworldNullOne() throws IOException {
    List<UserResponseModel> foundUsers = new ArrayList<>();
    String keyworld = "";
    user.setUserAvatarPath(avatarUploadDir + "avatar.png");
    List<User> users = new ArrayList<>();
    users.add(user);
    File file = new File(user.getUserAvatarPath());
    when(userRepository.findAll()).thenReturn(users);
    UserResponseModel userResponseModel =
            UserResponseModel.builder()
                    .userId(user.getUserId())
                    .userFirstName(user.getUserFirstName())
                    .userLastName(user.getUserLastName())
                    .username(user.getUsername())
                    .userEmail(user.getUserEmail())
                    .userAvatar(Files.readAllBytes(file.toPath()))
                    .build();
    foundUsers.add(userResponseModel);
    assertEquals(foundUsers, userServiceImpl.findUsersByKeyword(keyworld));
  }

  @Test
  void findUserByKeyworld_SuccessKeyworldNullTwo() throws Exception {
    List<UserResponseModel> foundUsers = new ArrayList<>();
    String keyworld = null;
    user.setUserAvatarPath(null);
    List<User> users = new ArrayList<>();
    users.add(user);
    when(userRepository.findAll()).thenReturn(users);
    UserResponseModel userResponseModel =
            UserResponseModel.builder()
                    .userId(user.getUserId())
                    .userFirstName(user.getUserFirstName())
                    .userLastName(user.getUserLastName())
                    .username(user.getUsername())
                    .userEmail(user.getUserEmail())
                    .build();
    foundUsers.add(userResponseModel);
    assertEquals(foundUsers, userServiceImpl.findUsersByKeyword(keyworld));
  }

  @Test
  void shouldfindUsersByKeyworld_Success() throws Exception {
    List<UserResponseModel> foundUsers = new ArrayList<>();
    String keyworld = "test";
    user.setUserAvatarPath(avatarUploadDir + "avatar.png");
    List<User> users = new ArrayList<>();
    users.add(user);
    when(userRepository.findAllByUserEmailOrUsernameContaining(keyworld)).thenReturn(users);
    UserResponseModel userResponseModel =
            UserResponseModel.builder()
                    .userId(1L)
                    .userFirstName(user.getUserFirstName())
                    .userLastName(user.getUserLastName())
                    .username(user.getUsername())
                    .userEmail(user.getUserEmail())
                    .userAvatar(Files.readAllBytes(Paths.get(user.getUserAvatarPath())))
                    .build();
    foundUsers.add(userResponseModel);
    assertEquals(foundUsers, userServiceImpl.findUsersByKeyword(keyworld));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"}, username = "user@user.com")
  void updateUser_USERNOTFOUND() throws CouldNotSaveUserException {
    UserModel usermodel = ModelSamples.UserSamples.getUserModel();
    usermodel.setUserEmail("super@elite.com");
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(user));
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(userRepository.findByUserEmail(anyString())).thenReturn(null);
    Map<String, String> errorMap = new HashMap<>();
    errorMap.put("NOT_FOUND", Constants.USER_NOT_FOUND);
    assertEquals(errorMap.entrySet(), userServiceImpl.updateUser(usermodel).getBody().entrySet());
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"}, username = "user@user.com")
  void updateUser_EmailNotTheSame() throws CouldNotSaveUserException {
    UserModel usermodel = new UserModel();
    usermodel.setUsername(USER_EMAIL);
    usermodel.setUserPassword("");
    usermodel.setUserPasswordConfirmation("");
    usermodel.setUserEmail(USER_EMAIL);
    user.setUserPassword("");
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(user));
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(userRepository.findByUserEmail(anyString())).thenReturn(user);
    Map<String, String> errorMap = new HashMap<>();
    errorMap.put(Constants.CONFLICT, "Les emails doivent être identiques");
    assertEquals(errorMap.entrySet(), userServiceImpl.updateUser(usermodel).getBody().entrySet());
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"}, username = "user@user.com")
  void updateUser_Success() throws CouldNotSaveUserException {
    UserModel usermodel = new UserModel();
    usermodel.setUsername(USER_EMAIL);
    usermodel.setUserEmailConfirmation(USER_EMAIL);
    usermodel.setUserPassword("");
    usermodel.setUserPasswordConfirmation("");
    usermodel.setUserEmail(USER_EMAIL);
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(user));
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(userRepository.findByUserEmail(anyString())).thenReturn(user);
    Map<String, String> errorMap = new HashMap<>();
    errorMap.put("SUCCESS", "Utilisateur mis à jour");
    assertEquals(errorMap.entrySet(), userServiceImpl.updateUser(usermodel).getBody().entrySet());
  }


  @Test
  void sendRegistrationConfirmationEmail_Success() {
    // Given
    UserServiceImpl userServiceImpl = new UserServiceImpl(userRepository, passwordEncoder, verificationTokenService, emailClientMock);
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setToken("token");
    verificationToken.setUser(user);
    verificationToken.setType("type");
    verificationToken.setExpiryDate(new Date(System.currentTimeMillis() + 2 * 24 * 60 * 60 * 1000L));
    ReflectionTestUtils.setField(userServiceImpl, "senderEmail", "exemple@test.com");
    ReflectionTestUtils.setField(userServiceImpl, "frontEndUrl", "test.com");

    SyncPoller<EmailSendResult, EmailSendResult> poller = mock(SyncPoller.class);
    EmailSendResult simulatedEmailSendResult = new EmailSendResult("1", EmailSendStatus.SUCCEEDED, null);
    PollResponse<EmailSendResult> simulatedPollResponse = new PollResponse<>(LongRunningOperationStatus.SUCCESSFULLY_COMPLETED, simulatedEmailSendResult);
    // When
    when(verificationTokenService.createToken(any(), any())).thenReturn(verificationToken);
    when(emailClientMock.beginSend(any(), eq(null))).thenReturn(poller);
    when(poller.poll()).thenReturn(simulatedPollResponse);

    userServiceImpl.sendRegistrationConfirmationEmail(user);
    // Then
    verify(emailClientMock, times(1)).beginSend(emailMessageCaptor.capture(), eq(null));
    EmailMessage capturedEmailMessage = emailMessageCaptor.getValue();
    assertNotNull(capturedEmailMessage);
    assertEquals("exemple@test.com", capturedEmailMessage.getSenderAddress());
    assertEquals("ELIT : Activez votre compte", capturedEmailMessage.getSubject());
  }
}
