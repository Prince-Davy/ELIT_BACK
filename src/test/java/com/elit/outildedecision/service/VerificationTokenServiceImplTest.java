package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.entity.VerificationToken;
import com.elit.outildedecision.exception.UserNotVerifiedException;
import com.elit.outildedecision.model.PasswordChangeModel;
import com.elit.outildedecision.repository.UserRepository;
import com.elit.outildedecision.repository.VerificationTokenRepository;
import com.elit.outildedecision.service.implementation.VerificationTokenServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

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

  private String token;
  private User user;
  private String type;
  private VerificationToken verificationToken;
  private static final Long TOKEN_EXPIRATION_TIME = 30 * 60 * 1000L;
  @BeforeEach
  public void setUp() {
    token = "token";
    user = new User();
    user.setUserId(1L);
    user.setUsername("David");
    user.setUserEmail("email@gmail.com");
    type = "type";
    verificationToken = null;
  }


  @Test
  void findByToken() {
    when(verificationTokenRepository.findByToken(token)).thenReturn(verificationToken);
    Assertions.assertEquals(verificationToken,verificationTokenServiceImpl.findByToken(token) );

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
    Assertions.assertEquals(ResponseEntity.ok("Account confirmed"), verificationTokenServiceImpl.confirmUser(token));
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
    Assertions.assertEquals(ResponseEntity.badRequest().body("Invalid token"), verificationTokenServiceImpl.confirmUser(token));
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
    Assertions.assertEquals(ResponseEntity.badRequest().body("Token expired"), verificationTokenServiceImpl.confirmUser(token));
  }

  @Test
  void reinstateForgotPassword() {
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setToken(token);
    verificationToken.setUser(user);
    verificationToken.setType("");
    verificationToken.setExpiryDate( new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME));
    PasswordChangeModel changePasswordModel = new PasswordChangeModel();
    user.setUserPassword(passwordEncoder.encode(changePasswordModel.getNewPassword()));
    when(verificationTokenRepository.findByToken(token)).thenReturn(verificationToken);
    Assertions.assertEquals(ResponseEntity.ok("Le mot de passe est mis à jour"), verificationTokenServiceImpl.reinstateForgotPassword(token, changePasswordModel));
  }

  @Test
  void reinstateForgotPassword_whenInvalidLink() {
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setToken(token);
    verificationToken.setUser(user);
    verificationToken.setType(type);
    verificationToken.setExpiryDate( new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME));
    PasswordChangeModel changePasswordModel = new PasswordChangeModel();
    user.setUserPassword(passwordEncoder.encode(changePasswordModel.getNewPassword()));
    when(verificationTokenRepository.findByToken(token)).thenReturn(verificationToken);
    Assertions.assertEquals(ResponseEntity.badRequest().body("le lien est invalide"), verificationTokenServiceImpl.reinstateForgotPassword(token, changePasswordModel));
  }
  @Test
  void reinstateForgotPassword_whenExpiredLink() {
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setToken(token);
    verificationToken.setUser(user);
    verificationToken.setType("");
    verificationToken.setExpiryDate( new Date(System.currentTimeMillis() - 18000));
    PasswordChangeModel changePasswordModel = new PasswordChangeModel();
    user.setUserPassword(passwordEncoder.encode(changePasswordModel.getNewPassword()));
    when(verificationTokenRepository.findByToken(token)).thenReturn(verificationToken);
    Assertions.assertEquals(ResponseEntity.badRequest().body("le lien est expiré"), verificationTokenServiceImpl.reinstateForgotPassword(token, changePasswordModel));
  }

  @Test
  void reinstateForgotPassword_whenNewPasswordAreDiffrente() {
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setToken(token);
    verificationToken.setUser(user);
    verificationToken.setType("");
    verificationToken.setExpiryDate( new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME));
    PasswordChangeModel changePasswordModel = new PasswordChangeModel();
    changePasswordModel.setNewPassword("mdp1");
    changePasswordModel.setConfirmPassword("mdp2");
    user.setUserPassword(passwordEncoder.encode(changePasswordModel.getNewPassword()));
    when(verificationTokenRepository.findByToken(token)).thenReturn(verificationToken);
    Assertions.assertEquals(ResponseEntity.badRequest().body("les mots de passes ne sont pas identiques"), verificationTokenServiceImpl.reinstateForgotPassword(token, changePasswordModel));
  }

  @Test
  void forgotPassword() throws UserNotVerifiedException, JsonProcessingException {
     user.setIsEmailVerified(true);
    String email = "{\"email\":\"" + user.getUserEmail() + "\"}";
    Map result = new ObjectMapper().readValue(email, Map.class);
    when(userRepository.findByUserEmail((String) result.get("email"))).thenReturn(user);
     Assertions.assertEquals(ResponseEntity.ok("Email de récupération envoyé"),verificationTokenServiceImpl.forgotPassword(email));
  }

  @Test
  void forgotPassword_whenUserDoseNotExists() throws UserNotVerifiedException, JsonProcessingException {
    user.setIsEmailVerified(true);
    String email = "{\"email\":\"" + user.getUserEmail() + "\"}";
    Map result = new ObjectMapper().readValue(email, Map.class);
    when(userRepository.findByUserEmail((String) result.get("email"))).thenReturn(null);
    Assertions.assertEquals(ResponseEntity.badRequest().body("Cet utilisateur n'existe pas !"),verificationTokenServiceImpl.forgotPassword(email));
  }

  @Test
  void forgotPassword_whenEmailIsNotVerify() throws JsonProcessingException {
    user.setIsEmailVerified(false);
    String email = "{\"email\":\"" + "email@" + "\"}";
    Map result = new ObjectMapper().readValue(email, Map.class);
    when(userRepository.findByUserEmail((String) result.get("email"))).thenReturn(user);
    UserNotVerifiedException e = Assertions.assertThrows(UserNotVerifiedException.class, () -> verificationTokenServiceImpl.forgotPassword(email));
    Assertions.assertEquals("Votre compte n'est pas vérifié", e.getMessage());
  }

}
