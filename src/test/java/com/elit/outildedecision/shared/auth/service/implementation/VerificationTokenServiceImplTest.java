package com.elit.outildedecision.shared.auth.service.implementation;
import com.elit.outildedecision.shared.auth.service.VerificationTokenService;

import com.elit.outildedecision.shared.auth.service.ConfirmationOutcome;
import com.elit.outildedecision.shared.auth.service.PasswordResetOutcome;
import com.elit.outildedecision.shared.auth.service.ResetEmailOutcome;
import com.elit.outildedecision.shared.auth.service.ResetPasswordEmailResult;
import com.elit.outildedecision.shared.auth.service.VerificationMailService;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.auth.entity.VerificationToken;
import com.elit.outildedecision.shared.auth.dto.PasswordChangeRequest;
import com.elit.outildedecision.shared.user.repository.UserRepository;
import com.elit.outildedecision.shared.auth.repository.VerificationTokenRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationTokenServiceImplTest {
  @InjectMocks
  VerificationTokenServiceImpl verificationTokenServiceImpl;
  @Mock
  VerificationTokenRepository verificationTokenRepository;
  @Mock
  PasswordEncoder passwordEncoder;
  @Mock
  UserRepository userRepository;

  @Mock
  VerificationTokenService verificationTokenService;
  @Mock
  VerificationMailService verificationMailService;

  private String token;
  private User user;
  private String type;
  private VerificationToken tokenFixture;
  private static final Long TOKEN_EXPIRATION_TIME = 30 * 60 * 1000L;
  @BeforeEach
  void setUp() {
    token = "token";
    user = new User();
    user.setUserId(1L);
    user.setUsername("David");
    user.setUserEmail("email@gmail.com");
    type = "type";
    tokenFixture = null;
  }


  @Test
  void findByToken() {
    when(verificationTokenRepository.findByToken(token)).thenReturn(tokenFixture);
    Assertions.assertEquals(tokenFixture,verificationTokenServiceImpl.findByToken(token) );

  }

  @Test
  void removeByToken() {
    verificationTokenRepository.removeByToken(token);
    verify(verificationTokenRepository, times(1)).removeByToken(token);
  }

  @Test
  void createToken() {
    token = passwordEncoder.encode(user.getUsername() + user.getUserEmail());
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setToken(token);
    verificationToken.setUser(user);
    verificationToken.setType(type);
    when(verificationTokenRepository.save(verificationToken)).thenReturn(verificationToken);
    Assertions.assertEquals(verificationToken,verificationTokenServiceImpl.createToken(user,type) );
  }

  @Test
  void confirmUser() {
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setToken(token);
    verificationToken.setUser(user);
    verificationToken.setType("confirm-account");
    verificationToken.setExpiryDate(
      new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME));
    when(verificationTokenRepository.findByToken(token)).thenReturn(verificationToken);
    Assertions.assertEquals(ConfirmationOutcome.CONFIRMED, verificationTokenServiceImpl.confirmUser(token));
  }

  @Test
  void confirmUser_whenInvalidToken() {
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setToken(token);
    verificationToken.setUser(user);
    verificationToken.setType(type);
    verificationToken.setExpiryDate(
      new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME));
    when(verificationTokenRepository.findByToken(token)).thenReturn(verificationToken);
    Assertions.assertEquals(ConfirmationOutcome.INVALID_TOKEN, verificationTokenServiceImpl.confirmUser(token));
  }

  @Test
  void confirmUser_whenTokenExpired() {
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setToken(token);
    verificationToken.setUser(user);
    verificationToken.setType("confirm-account");
    verificationToken.setExpiryDate( new Date(System.currentTimeMillis() - 18000));
    when(verificationTokenRepository.findByToken(token)).thenReturn(verificationToken);
    Assertions.assertTrue(verificationToken.getExpiryDate().before(new Date()));
    Assertions.assertEquals(ConfirmationOutcome.EXPIRED, verificationTokenServiceImpl.confirmUser(token));
  }

  @Test
  void reinstateForgotPassword() {
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setToken(token);
    verificationToken.setUser(user);
    verificationToken.setType("");
    verificationToken.setExpiryDate( new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME));
    PasswordChangeRequest changePasswordModel = new PasswordChangeRequest();
    user.setUserPassword(passwordEncoder.encode(changePasswordModel.getNewPassword()));
    when(verificationTokenRepository.findByToken(token)).thenReturn(verificationToken);
    Assertions.assertEquals(PasswordResetOutcome.UPDATED, verificationTokenServiceImpl.reinstateForgotPassword(token, changePasswordModel));
  }

  @Test
  void reinstateForgotPassword_whenInvalidLink() {
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setToken(token);
    verificationToken.setUser(user);
    verificationToken.setType(type);
    verificationToken.setExpiryDate( new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME));
    PasswordChangeRequest changePasswordModel = new PasswordChangeRequest();
    user.setUserPassword(passwordEncoder.encode(changePasswordModel.getNewPassword()));
    when(verificationTokenRepository.findByToken(token)).thenReturn(verificationToken);
    Assertions.assertEquals(PasswordResetOutcome.INVALID_LINK, verificationTokenServiceImpl.reinstateForgotPassword(token, changePasswordModel));
  }
  @Test
  void reinstateForgotPassword_whenExpiredLink() {
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setToken(token);
    verificationToken.setUser(user);
    verificationToken.setType("");
    verificationToken.setExpiryDate( new Date(System.currentTimeMillis() - 18000));
    PasswordChangeRequest changePasswordModel = new PasswordChangeRequest();
    user.setUserPassword(passwordEncoder.encode(changePasswordModel.getNewPassword()));
    when(verificationTokenRepository.findByToken(token)).thenReturn(verificationToken);
    Assertions.assertEquals(PasswordResetOutcome.EXPIRED_LINK, verificationTokenServiceImpl.reinstateForgotPassword(token, changePasswordModel));
  }

  @Test
  void reinstateForgotPassword_whenNewPasswordAreDiffrente() {
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setToken(token);
    verificationToken.setUser(user);
    verificationToken.setType("");
    verificationToken.setExpiryDate( new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME));
    PasswordChangeRequest changePasswordModel = new PasswordChangeRequest();
    changePasswordModel.setNewPassword("mdp1");
    changePasswordModel.setConfirmPassword("mdp2");
    user.setUserPassword(passwordEncoder.encode(changePasswordModel.getNewPassword()));
    when(verificationTokenRepository.findByToken(token)).thenReturn(verificationToken);
    Assertions.assertEquals(PasswordResetOutcome.PASSWORD_MISMATCH, verificationTokenServiceImpl.reinstateForgotPassword(token, changePasswordModel));
  }

  @Test
  void sendResetPasswordEmail_UserNotFound() {
    // Given
    VerificationTokenServiceImpl serviceUnderTest = new VerificationTokenServiceImpl(passwordEncoder, verificationTokenRepository, userRepository, verificationMailService);
    String userEmail = "test@example.com";
    when(userRepository.findByUserEmail(userEmail)).thenReturn(null);
    // When
    ResetPasswordEmailResult result = serviceUnderTest.sendResetPasswordEmail("{\"email\":\"" + userEmail + "\"}");
    // Then
    assertEquals(ResetEmailOutcome.ACCOUNT_NOT_FOUND, result.outcome());
    assertEquals(userEmail, result.detail());
  }

  @Test
  void sendResetPasswordEmail_UserNotVerified() {
    // Given
    VerificationTokenServiceImpl serviceUnderTest = new VerificationTokenServiceImpl(passwordEncoder, verificationTokenRepository, userRepository, verificationMailService);
    String userEmail = "test@example.com";
    User localUser = new User();
    localUser.setUserEmail(userEmail);
    localUser.setIsEmailVerified(false);
    when(userRepository.findByUserEmail(userEmail)).thenReturn(localUser);
    // When
    ResetPasswordEmailResult result = serviceUnderTest.sendResetPasswordEmail("{\"email\":\"" + userEmail + "\"}");
    // Then
    assertEquals(ResetEmailOutcome.NOT_VERIFIED, result.outcome());
  }
  /* @Test
  void forgotPassword() throws UserNotVerifiedException, JsonProcessingException {
     user.setIsEmailVerified(true);
    String email = "{\"email\":\"" + user.getUserEmail() + "\"}";
    Map result = new ObjectMapper().readValue(email, Map.class);
    when(userRepository.findByUserEmail((String) result.get("email"))).thenReturn(user);
     Assertions.assertEquals(ResponseEntity.ok("Email de récupération envoyé"),verificationTokenServiceImpl.sendForgotPasswordEmail(email));
  }

  @Test
  void forgotPassword_whenUserDoseNotExists() throws UserNotVerifiedException, JsonProcessingException {
    user.setIsEmailVerified(true);
    String email = "{\"email\":\"" + user.getUserEmail() + "\"}";
    Map result = new ObjectMapper().readValue(email, Map.class);
    when(userRepository.findByUserEmail((String) result.get("email"))).thenReturn(null);
    Assertions.assertEquals(ResponseEntity.badRequest().body("Cet utilisateur n'existe pas !"),verificationTokenServiceImpl.sendForgotPasswordEmail(email));
  }

  @Test
  void forgotPassword_whenEmailIsNotVerify() throws JsonProcessingException {
    user.setIsEmailVerified(false);
    String email = "{\"email\":\"" + "email@" + "\"}";
    Map result = new ObjectMapper().readValue(email, Map.class);
    when(userRepository.findByUserEmail((String) result.get("email"))).thenReturn(user);
    UserNotVerifiedException e = Assertions.assertThrows(UserNotVerifiedException.class, () -> verificationTokenServiceImpl.sendForgotPasswordEmail(email));
    Assertions.assertEquals("Votre compte n'est pas vérifié", e.getMessage());
  }*/

}
