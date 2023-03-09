package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.entity.VerificationToken;
import com.elit.outildedecision.exception.UserNotVerifiedException;
import com.elit.outildedecision.model.PasswordChangeModel;
import com.elit.outildedecision.repository.UserRepository;
import com.elit.outildedecision.repository.VerificationTokenRepository;
import com.elit.outildedecision.service.VerificationTokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Service
@Transactional
public class VerificationTokenServiceImpl implements VerificationTokenService {
  @Value("${spring.mail.username}")
  private static String SENDER_EMAIL;

  private final PasswordEncoder passwordEncoder;
  private final VerificationTokenRepository verificationTokenRepository;
  private final UserRepository userRepository;
  private final JavaMailSender javaMailSender;

  private final Long EXPIRATION_TIME_CONFIRMATION = 2 * 24 * 60 * 60 * 1000L;
  private final Long EXPIRATION_TIME_PASSWORD_RECOVERY = 30 * 60 * 1000L;

  public VerificationTokenServiceImpl(
      PasswordEncoder passwordEncoder,
      VerificationTokenRepository verificationTokenRepository,
      UserRepository userRepository,
      JavaMailSender javaMailSender) {
    this.passwordEncoder = passwordEncoder;
    this.verificationTokenRepository = verificationTokenRepository;
    this.userRepository = userRepository;

    this.javaMailSender = javaMailSender;
  }

  /**
   * @param token
   * @return VerificationToken
   */
  @Override
  public VerificationToken findByToken(String token) {
    return verificationTokenRepository.findByToken(token);
  }

  @Override
  public void removeByToken(String token) {
    verificationTokenRepository.removeByToken(token);
  }

  @Override
  public VerificationToken createToken(User user, String type) {
    String token = passwordEncoder.encode(user.getUsername() + user.getUserEmail());
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setToken(token);
    verificationToken.setUser(user);
    verificationToken.setType(type);
    if (Objects.equals(type, "confirm-account")) {
      verificationToken.setExpiryDate(
          new Date(System.currentTimeMillis() + EXPIRATION_TIME_CONFIRMATION));
    } else if (Objects.equals(type, "")) {
      verificationToken.setExpiryDate(
          new Date(System.currentTimeMillis() + EXPIRATION_TIME_PASSWORD_RECOVERY));
    }
    verificationTokenRepository.save(verificationToken);
    return verificationToken;
  }

  @Override
  public ResponseEntity<String> confirmUser(String token) {
    VerificationToken verificationToken = findByToken(token);
    if (verificationToken == null
        || !Objects.equals(verificationToken.getType(), "confirm-account")) {
      return ResponseEntity.badRequest().body("Invalid token");
    }
    if (verificationToken.getExpiryDate().before(new Date())) {
      removeByToken(token);
      return ResponseEntity.badRequest().body("Token expired");
    }
    removeByToken(token);
    User user = verificationToken.getUser();
    user.setIsUserActive(true);
    user.setIsEmailVerified(true);
    userRepository.save(user);
    return ResponseEntity.ok("Account confirmed");
  }

  @Override
  public ResponseEntity<String> reinstateForgotPassword(
      String token, PasswordChangeModel changePasswordModel) {
    VerificationToken verificationToken = findByToken(token);
    if (verificationToken == null
        || !Objects.equals(verificationToken.getType(), "")) {
      return ResponseEntity.badRequest().body("le lien est invalide");
    }
    if (verificationToken.getExpiryDate().before(new Date())) {
      removeByToken(token);
      return ResponseEntity.badRequest().body("le lien est expiré");
    }
    if (!Objects.equals(
        changePasswordModel.getNewPassword(), changePasswordModel.getConfirmPassword())) {
      return ResponseEntity.badRequest().body("les mots de passes ne sont pas identiques");
    }
    User user = verificationToken.getUser();
    user.setUserPassword(passwordEncoder.encode(changePasswordModel.getNewPassword()));
    return ResponseEntity.ok("Le mot de passe est mis à jour");
  }

  @Override
  public ResponseEntity<String> forgotPassword(String email)
      throws JsonProcessingException, UserNotVerifiedException {
    Map result = new ObjectMapper().readValue(email, Map.class);
    User user = userRepository.findByUserEmail((String) result.get("email"));
    if (Objects.isNull(user)) {
      return ResponseEntity.badRequest().body("Cet utilisateur n'existe pas !");
    }
    if (!user.getIsEmailVerified()) {
      throw new UserNotVerifiedException("Votre compte n'est pas vérifié");
    }
    VerificationToken verificationToken = createToken(user, "");
    String recoveryUrl =
        "http://localhost:8080/reset-password?token=" + verificationToken.getToken(); // TODO : to
    // change in
    // production
    String mailBody =
        "Bonjour "
            + " "
            + user.getUserFirstName()
            + " "
            + user.getUserLastName()
            + ",\n\n"
            + "Veuillez cliquer sur le lien ci-dessous pour changer le mot de passe de votre compte :\n"
            + recoveryUrl
            + "\n\n"
            + "Cordialement,\n"
            + "L'équipe Outil de décision ELIT";
    String mailSubject = "ELIT : Récupération de mot de passe";
    try {
      // Creating a simple mail message
      SimpleMailMessage mailMessage = new SimpleMailMessage();
      // Setting up necessary details
      mailMessage.setFrom(SENDER_EMAIL);
      mailMessage.setTo(user.getUserEmail());
      mailMessage.setText(mailBody);
      mailMessage.setSubject(mailSubject);

      // Sending the mail
      javaMailSender.send(mailMessage);
    } catch (Exception e) {
      throw new RuntimeException("Exception occurred while sending email", e);
    }
    return ResponseEntity.ok("Email de récupération envoyé");
  }
}
