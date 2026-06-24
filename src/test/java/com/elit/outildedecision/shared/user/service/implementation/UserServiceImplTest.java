package com.elit.outildedecision.shared.user.service.implementation;
import com.elit.outildedecision.shared.auth.service.VerificationTokenService;

import com.elit.outildedecision.shared.auth.service.VerificationMailService;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.auth.entity.VerificationToken;
import com.elit.outildedecision.shared.exception.BadRequestException;
import com.elit.outildedecision.shared.exception.ForbiddenException;
import com.elit.outildedecision.shared.user.exception.CouldNotSaveUserException;
import com.elit.outildedecision.shared.user.exception.UserAlreadyExistsException;
import com.elit.outildedecision.shared.user.exception.UserNotFoundException;
import com.elit.outildedecision.shared.auth.dto.UserRequest;
import com.elit.outildedecision.shared.user.dto.UserResponse;
import com.elit.outildedecision.shared.user.entity.UserAvatarContent;
import com.elit.outildedecision.shared.user.repository.UserAvatarContentRepository;
import com.elit.outildedecision.shared.user.repository.UserRepository;
import com.elit.outildedecision.shared.security.AppUserPrincipal;
import com.elit.outildedecision.shared.user.mapper.UserMapper;
import com.elit.outildedecision.shared.user.mapper.UserMapperImpl;
import com.elit.outildedecision.shared.user.service.implementation.UserServiceImpl;
import com.elit.outildedecision.shared.util.ModelSamples;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserServiceImplTest {
  @Mock
  private UserRepository userRepository;
  @Mock
  private MultipartFile file;
  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private VerificationTokenService verificationTokenService;

  @Mock
  private VerificationMailService verificationMailService;

  @Mock
  private org.springframework.context.ApplicationEventPublisher eventPublisher;

  @Mock
  private UserAvatarContentRepository userAvatarContentRepository;

  @Spy
  private UserMapper userMapper = new UserMapperImpl();

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
          .forEach(fileToDelete -> {
            boolean delete = fileToDelete.delete();
            if (delete) {
              log.trace("File {} successfully deleted", fileToDelete);
            }
            else {
              log.error("File {} could not be deleted, continuing anyway", fileToDelete);
            }
          });
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
    UserRequest usermodel = new UserRequest();
    usermodel.setUserEmail(USER_EMAIL);
    usermodel.setUserPassword("");
    usermodel.setUserPasswordConfirmation("");
    assertThrows(UserAlreadyExistsException.class, () -> userServiceImpl.saveUser(usermodel));
  }

  @Test
  void saveUser_ThrowsCouldNotSaveUserException() {
    UserRequest usermodel = new UserRequest();
    usermodel.setUserEmail(USER_EMAIL);
    usermodel.setUserPassword("");
    usermodel.setUserPasswordConfirmation("");
    when(userRepository.save(user)).thenReturn(user);
    assertThrows(CouldNotSaveUserException.class, () -> userServiceImpl.saveUser(usermodel));
  }

  @Test
  void updatePasswordDifferent() throws Exception {
    UserRequest usermodel = new UserRequest();
    usermodel.setUserEmail(USER_EMAIL);
    usermodel.setUserPassword("123456789");
    usermodel.setUserPasswordConfirmation("987654321");
    assertThrows(CouldNotSaveUserException.class, () -> userServiceImpl.saveUser(usermodel));
  }

  @Test
  void updatePasswordUserById_WrongOldPassword_ThrowsBadRequest() {
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    assertThrows(BadRequestException.class,
        () -> userServiceImpl.updatePasswordUserById(USER_ID, OLD_PASS, NEW_PASS, USER_EMAIL));
  }

  @Test
  void updatePasswordUserById_NotOwner_ThrowsForbidden() {
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    assertThrows(ForbiddenException.class,
        () -> userServiceImpl.updatePasswordUserById(USER_ID, OLD_PASS, NEW_PASS, "intruder@example.com"));
  }

  @Test
  void updatePasswordUserById_UserNotFoundException() {
    when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
    assertThrows(UserNotFoundException.class, () -> userServiceImpl.updatePasswordUserById(USER_ID, OLD_PASS, NEW_PASS, USER_EMAIL));
  }

  @Test
  void updatePasswordUserById_Success() throws Exception {
    user.setUserPassword(passwordEncoder.encode(OLD_PASS));
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    String currentPass = user.getUserPassword();
    when(passwordEncoder.matches(OLD_PASS, currentPass)).thenReturn(true);
    user.setUserPassword(passwordEncoder.encode(NEW_PASS));
    when(userRepository.save(user)).thenReturn(user);
    assertEquals("Votre nouveau mot de passe a bien été enregistré.",
        userServiceImpl.updatePasswordUserById(USER_ID, OLD_PASS, NEW_PASS, USER_EMAIL));
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
    MockMultipartFile avatarFile
            = new MockMultipartFile(
            "file",
            "avatar.png",
            MediaType.IMAGE_PNG_VALUE,
            "fake-image-bytes".getBytes()
    );
    assertEquals("Votre photo a bien été modifiée.",
        userServiceImpl.updateAvatarByIdUser(USER_ID, avatarFile));
  }

  @Test
  void updateAvatarByIdUser_InvalidMimeType() {
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    MockMultipartFile avatarFile
            = new MockMultipartFile(
            "file",
            "hello.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "Hello, World!".getBytes()
    );
    assertThrows(CouldNotSaveUserException.class,
            () -> userServiceImpl.updateAvatarByIdUser(USER_ID, avatarFile));
  }

  @Test
  void updateAvatarByIdUser_TooLarge() {
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    MockMultipartFile avatarFile
            = new MockMultipartFile(
            "file",
            "avatar.png",
            MediaType.IMAGE_PNG_VALUE,
            new byte[2 * 1024 * 1024 + 1]
    );
    assertThrows(CouldNotSaveUserException.class,
            () -> userServiceImpl.updateAvatarByIdUser(USER_ID, avatarFile));
  }

  @Test
  void updateInfosByIdUser_UserNotFoundException() {
    UserRequest usermodel = new UserRequest();
    usermodel.setUserEmail(USER_EMAIL);
    when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
    assertThrows(UserNotFoundException.class, () -> userServiceImpl.updateInfosByIdUser(USER_ID, "firstname", "lastname"));
  }

  @Test
  void updateInfosByIdUser_Success() throws Exception {
    UserRequest usermodel = new UserRequest();
    usermodel.setUserEmail(USER_EMAIL);
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(userRepository.save(user)).thenReturn(user);
    assertEquals("Vos modifications ont bien été enregistrées",
        userServiceImpl.updateInfosByIdUser(USER_ID, "", ""));
  }

  @Test
  void getAllInfosUserById_Success() {
    when(userRepository.getReferenceById(USER_ID)).thenReturn(user);
    assertEquals(UserResponse.builder()
            .userId(user.getUserId())
            .userFirstName(user.getUserFirstName())
            .userLastName(user.getUserLastName())
            .username(user.getUsername())
            .userEmail(user.getUserEmail())
            .build(), userServiceImpl.getAllInfosUserById(USER_ID));
  }

  @Test
  void saveAvatar_Success() throws IOException {
    MockMultipartFile avatarFile
            = new MockMultipartFile(
            "avatar.png",
            "avatar.png",
            MediaType.IMAGE_PNG_VALUE,
            "avatar.png".getBytes());
    assertEquals("null" + avatarFile.getOriginalFilename(), userServiceImpl.saveAvatar(avatarFile));

  }

  @Test
  void findUsersByKeyworld_SuccesskeyworldNullFour() throws IOException {
    List<UserResponse> foundUsers = new ArrayList<>();
    String keyworld = null;
    user.setUserAvatarPath(avatarUploadDir + "avatar.png");
    byte[] avatarBytes = "avatar-bytes".getBytes();
    List<User> users = new ArrayList<>();
    users.add(user);
    when(userRepository.findAll()).thenReturn(users);
    when(userAvatarContentRepository.findById(user.getUserId()))
            .thenReturn(Optional.of(new UserAvatarContent(user.getUserId(), avatarBytes)));
    UserResponse userResponseModel =
            UserResponse.builder()
                    .userId(user.getUserId())
                    .userFirstName(user.getUserFirstName())
                    .userLastName(user.getUserLastName())
                    .username(user.getUsername())
                    .userEmail(user.getUserEmail())
                    .userAvatar(avatarBytes)
                    .build();
    foundUsers.add(userResponseModel);
    assertEquals(foundUsers, userServiceImpl.findUsersByKeyword(keyworld));
  }

  @Test
  void findUserByKeyworld_SuccessKeyworldNullThree() throws Exception {
    List<UserResponse> foundUsers = new ArrayList<>();
    String keyworld = "";
    user.setUserAvatarPath(null);
    List<User> users = new ArrayList<>();
    users.add(user);
    when(userRepository.findAll()).thenReturn(users);
    UserResponse userResponseModel =
            UserResponse.builder()
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
    List<UserResponse> foundUsers = new ArrayList<>();
    String keyworld = "";
    user.setUserAvatarPath(avatarUploadDir + "avatar.png");
    byte[] avatarBytes = "avatar-bytes".getBytes();
    List<User> users = new ArrayList<>();
    users.add(user);
    when(userRepository.findAll()).thenReturn(users);
    when(userAvatarContentRepository.findById(user.getUserId()))
            .thenReturn(Optional.of(new UserAvatarContent(user.getUserId(), avatarBytes)));
    UserResponse userResponseModel =
            UserResponse.builder()
                    .userId(user.getUserId())
                    .userFirstName(user.getUserFirstName())
                    .userLastName(user.getUserLastName())
                    .username(user.getUsername())
                    .userEmail(user.getUserEmail())
                    .userAvatar(avatarBytes)
                    .build();
    foundUsers.add(userResponseModel);
    assertEquals(foundUsers, userServiceImpl.findUsersByKeyword(keyworld));
  }

  @Test
  void findUserByKeyworld_SuccessKeyworldNullTwo() throws Exception {
    List<UserResponse> foundUsers = new ArrayList<>();
    String keyworld = null;
    user.setUserAvatarPath(null);
    List<User> users = new ArrayList<>();
    users.add(user);
    when(userRepository.findAll()).thenReturn(users);
    UserResponse userResponseModel =
            UserResponse.builder()
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
    List<UserResponse> foundUsers = new ArrayList<>();
    String keyworld = "test";
    user.setUserAvatarPath(avatarUploadDir + "avatar.png");
    byte[] avatarBytes = "avatar-bytes".getBytes();
    List<User> users = new ArrayList<>();
    users.add(user);
    when(userRepository.findAllByUserEmailOrUsernameContaining(keyworld)).thenReturn(users);
    when(userAvatarContentRepository.findById(user.getUserId()))
            .thenReturn(Optional.of(new UserAvatarContent(user.getUserId(), avatarBytes)));
    UserResponse userResponseModel =
            UserResponse.builder()
                    .userId(1L)
                    .userFirstName(user.getUserFirstName())
                    .userLastName(user.getUserLastName())
                    .username(user.getUsername())
                    .userEmail(user.getUserEmail())
                    .userAvatar(avatarBytes)
                    .build();
    foundUsers.add(userResponseModel);
    assertEquals(foundUsers, userServiceImpl.findUsersByKeyword(keyworld));
  }

  @Test
  void sendRegistrationConfirmationEmail_Success() {
    // Given
    UserServiceImpl localUserService = new UserServiceImpl(userRepository, passwordEncoder, verificationTokenService, userMapper, verificationMailService, eventPublisher, userAvatarContentRepository);
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setToken("token");
    verificationToken.setUser(user);
    verificationToken.setType("confirm-account");
    when(verificationTokenService.createToken(any(), any())).thenReturn(verificationToken);

    // When
    localUserService.sendRegistrationConfirmationEmail(user);

    // Then
    verify(verificationTokenService, times(1)).createToken(user, "confirm-account");
    verify(verificationMailService, times(1)).sendAccountConfirmationEmail(user, "token");
  }
}
