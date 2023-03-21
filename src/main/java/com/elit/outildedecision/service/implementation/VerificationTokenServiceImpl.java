
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

/**
 * Service implementation for handling verification tokens.
 */
@Service
@Transactional
public class VerificationTokenServiceImpl implements VerificationTokenService {

  /** The sender email address used for sending emails. */
  @Value("${spring.mail.username}")
  private static String senderEmail;

  /** The password encoder used for encoding passwords. */
  private final PasswordEncoder passwordEncoder;

  /** The repository for managing verification tokens. */
  private final VerificationTokenRepository verificationTokenRepository;

  /** The repository for managing users. */
  private final UserRepository userRepository;

  /** The mail sender used for sending emails. */
  private final JavaMailSender javaMailSender;

  /** The expiration time for account confirmation tokens. */
  private final Long expirationTimeConfirmation = 2 * 24 * 60 * 60 * 1000L;

  /** The expiration time for password recovery tokens. */
  private final Long expirationTimePasswordRecovery = 30 * 60 * 1000L;

  /**
   * Constructs a new instance of the verification token service implementation.
   *
   * @param passwordEncoder The password encoder used for encoding passwords.
   * @param verificationTokenRepository The repository for managing verification tokens.
   * @param userRepository The repository for managing users.
   * @param javaMailSender The mail sender used for sending emails.
   */
  public VerificationTokenServiceImpl(final PasswordEncoder passwordEncoder,
                                      final VerificationTokenRepository verificationTokenRepository,
                                      final UserRepository userRepository,
                                      final JavaMailSender javaMailSender) {
    this.passwordEncoder = passwordEncoder;
    this.verificationTokenRepository = verificationTokenRepository;
    this.userRepository = userRepository;
    this.javaMailSender = javaMailSender;
  }

  /**
   * Finds a verification token by its token string.
   *
   * @param token The token string to search for.
   * @return The verification token if found, or null if not found.
   */
  @Override
  public VerificationToken findByToken(final String token) {
    return verificationTokenRepository.findByToken(token);
  }

  /**
   * Removes a verification token by its token string.
   *
   * @param token The token string to remove.
   */
  @Override
  public void removeByToken(final String token) {
    verificationTokenRepository.removeByToken(token);
  }

  /**
   * Creates a new verification token for the specified user and type.
   *
   * @param user The user to create the token for.
   * @param type The type of the token to create.
   * @return The newly created verification token.
   */
  @Override
  public VerificationToken createToken(final User user, final String type) {
    String token = passwordEncoder.encode(user.getUsername() + user.getUserEmail());
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setToken(token);
    verificationToken.setUser(user);
    verificationToken.setType(type);
    if (Objects.equals(type, "confirm-account")) {
      verificationToken.setExpiryDate(
        new Date(System.currentTimeMillis() + expirationTimeConfirmation));
    } else if (Objects.equals(type, "")) {
      verificationToken.setExpiryDate(
        new Date(System.currentTimeMillis() + expirationTimePasswordRecovery));
    }
    verificationTokenRepository.save(verificationToken);
    return verificationToken;
  }

  /**
   * Confirms a user's account by validating the token provided.
   *
   * @param token The token string to validate.
   * @return A response entity indicating the result of the operation.
   */
  @Override
  public ResponseEntity<String> confirmUser(final String token) {
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

  /**
   * This method is used to reinstate a forgotten password. It checks whether the token is valid and
   * not expired. It also checks whether the new password and confirm password match, and if they do,
   * it updates the user's password.
   *
   * @param token              The token that is used to reinstate a forgotten password
   * @param changePasswordModel The model that contains the new password and the confirm password
   * @return A response entity with a message indicating whether the password update was successful or not
   */
  @Override
  public ResponseEntity<String> reinstateForgotPassword(final String token,
                                                        final PasswordChangeModel changePasswordModel) {
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
  /**
   * This method is used to initiate a forgot password request. It reads the user email from the JSON
   * string, retrieves the user from the database, and creates a verification token. It then sends an
   * email to the user with a link to reset their password.
   *
   * @param email The JSON string containing the user email
   * @return A response entity with a message indicating whether the forgot password request was successful or not
   * @throws JsonProcessingException If the JSON string cannot be processed
   * @throws UserNotVerifiedException If the user has not verified their email address
   */
  @Override
  public ResponseEntity<String> forgotPassword(final String email)
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
        "http://localhost:8080/reset-password?token=" + verificationToken.getToken();
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
      SimpleMailMessage mailMessage = new SimpleMailMessage();
      mailMessage.setFrom(senderEmail);
      mailMessage.setTo(user.getUserEmail());
      mailMessage.setText(mailBody);
      mailMessage.setSubject(mailSubject);

      javaMailSender.send(mailMessage);
    } catch (Exception e) {
      throw new RuntimeException("Exception occurred while sending email", e);
    }
    return ResponseEntity.ok("Email de récupération envoyé");
  }
}
