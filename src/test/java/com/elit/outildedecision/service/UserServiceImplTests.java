package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.CouldNotSaveUserException;
import com.elit.outildedecision.exception.UserAlreadyExistsException;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.model.UserModel;
import com.elit.outildedecision.model.UserResponseModel;
import com.elit.outildedecision.repository.UserRepository;
import com.elit.outildedecision.service.implementation.UserServiceImpl;
import com.elit.outildedecision.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class UserServiceImplTests {
  @Mock
  private UserRepository userRepository;
  @Mock
  private MultipartFile file;
  @Mock
  private PasswordEncoder passwordEncoder;
  private final String avatarUploadDir="src/main/resources/avatars/";

  @InjectMocks
  private UserServiceImpl userServiceImpl;
  private static final String USER_EMAIL = "test@example.com";
  private static final Long USER_ID = 1L;
  private static final String OLD_PASS = "old_pass";
  private static final String NEW_PASS = "new_pass";
  private User user;

  @BeforeEach
  public void setUp() {
    user = new User();
    user.setUsername(USER_EMAIL);
    user.setUserEmail(USER_EMAIL);
    user.setUserId(1L);
    user.setUserPassword(passwordEncoder.encode(OLD_PASS));
  }

  @Test
  void loadUserByUsername_Success(){
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
    assertEquals("null"+file.getOriginalFilename(), userServiceImpl.saveAvatar(file));

  }

  @Test
  void findUsersByKeyworld_SuccesskeyworldNullFour() throws IOException {
    List<UserResponseModel> foundUsers = new ArrayList<>();
    String keyworld = null;
    user.setUserAvatarPath(avatarUploadDir+"avatar.png");
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
    assertEquals(foundUsers,userServiceImpl.findUsersByKeyword(keyworld));
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
    assertEquals(foundUsers,userServiceImpl.findUsersByKeyword(keyworld));
  }

  @Test
  void findUsersByKeyworld_SuccesskeyworldNullOne() throws IOException {
    List<UserResponseModel> foundUsers = new ArrayList<>();
    String keyworld = "";
    user.setUserAvatarPath(avatarUploadDir+"avatar.png");
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
    assertEquals(foundUsers,userServiceImpl.findUsersByKeyword(keyworld));
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
    assertEquals(foundUsers,userServiceImpl.findUsersByKeyword(keyworld));
  }

  @Test
  void shouldfindUsersByKeyworld_Success() throws Exception {
    List<UserResponseModel> foundUsers = new ArrayList<>();
    String keyworld = "test";
    user.setUserAvatarPath(avatarUploadDir+"avatar.png");
    List<User> users = new ArrayList<>();
    users.add(user);
    when(userRepository.findAllByUserEmailOrUsernameContaining(keyworld)).thenReturn(users);
    UserResponseModel userResponseModel =
      UserResponseModel.builder()
        .userFirstName(user.getUserFirstName())
        .userLastName(user.getUserLastName())
        .username(user.getUsername())
        .userEmail(user.getUserEmail())
        .userAvatar(Files.readAllBytes(Paths.get(user.getUserAvatarPath())))
        .build();
    foundUsers.add(userResponseModel);
    assertEquals(foundUsers,userServiceImpl.findUsersByKeyword(keyworld));
  }

  @Test
  void updateUser_USERNOTFOUND() throws CouldNotSaveUserException {
    UserModel usermodel = new UserModel();
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn("test@test.com");
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(userRepository.findByUserEmail(anyString())).thenReturn(null);
    Map<String, String> errorMap = new HashMap<>();
    errorMap.put("NOT_FOUND", Constants.USER_NOT_FOUND);
    assertEquals(errorMap.entrySet(),userServiceImpl.updateUser(usermodel).getBody().entrySet());
  }

  @Test
  void updateUser_EmailNotTheSame() throws CouldNotSaveUserException {
    UserModel usermodel = new UserModel();
    usermodel.setUsername(USER_EMAIL);
    usermodel.setUserPassword("");
    usermodel.setUserPasswordConfirmation("");
    usermodel.setUserEmail(USER_EMAIL);
    user.setUserPassword("");
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(USER_EMAIL);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(userRepository.findByUserEmail(anyString())).thenReturn(user);
    Map<String, String> errorMap = new HashMap<>();
    errorMap.put(Constants.CONFLICT, "Les emails doivent être identiques");
    assertEquals(errorMap.entrySet(),userServiceImpl.updateUser(usermodel).getBody().entrySet());
  }

  @Test
  void updateUser_Success() throws CouldNotSaveUserException {
    UserModel usermodel = new UserModel();
    usermodel.setUsername(USER_EMAIL);
    usermodel.setUserEmailConfirmation(USER_EMAIL);
    usermodel.setUserPassword("");
    usermodel.setUserPasswordConfirmation("");
    usermodel.setUserEmail(USER_EMAIL);
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(USER_EMAIL);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(userRepository.findByUserEmail(anyString())).thenReturn(user);
    Map<String, String> errorMap = new HashMap<>();
    errorMap.put("SUCCESS", "Utilisateur mis à jour");
    assertEquals(errorMap.entrySet(),userServiceImpl.updateUser(usermodel).getBody().entrySet());
  }

}
