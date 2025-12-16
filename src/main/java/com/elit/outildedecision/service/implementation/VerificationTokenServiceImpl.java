package com.elit.outildedecision.service.implementation;

import com.azure.communication.email.EmailClient;
import com.azure.communication.email.models.EmailMessage;
import com.azure.communication.email.models.EmailSendResult;
import com.azure.communication.email.models.EmailSendStatus;
import com.azure.core.exception.AzureException;
import com.azure.core.util.polling.LongRunningOperationStatus;
import com.azure.core.util.polling.PollResponse;
import com.azure.core.util.polling.SyncPoller;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.entity.VerificationToken;
import com.elit.outildedecision.exception.UserNotVerifiedException;
import com.elit.outildedecision.model.PasswordChangeModel;
import com.elit.outildedecision.repository.UserRepository;
import com.elit.outildedecision.repository.VerificationTokenRepository;
import com.elit.outildedecision.service.VerificationTokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;


/**
 * Service implementation for handling verification tokens.
 */
@Service
@Transactional
public class VerificationTokenServiceImpl implements VerificationTokenService {

    /**
     * The sender email address used for sending emails.
     */
    private static final String FORGOT_PASSWORD = "";

    /**
     * Value of username email.
     */
    @Value("${spring.mail.username}")
    private String senderEmail;

    /**
     * Value of frontend url.
     */
    @Value("${frontend.value-url}")
    private String frontEndUrl;

    /**
     * The password encoder used for encoding passwords.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * The repository for managing verification tokens.
     */
    private final VerificationTokenRepository verificationTokenRepository;

    /**
     * The repository for managing users.
     */
    private final UserRepository userRepository;

    /**
     * EmailClient instance to send emails.
     */
    private final EmailClient emailClient;

    /**
     * The expiration time for account confirmation tokens.
     */
    private static final Long EXPIRATION_TIME_CONFIRMATION =
            2 * 24 * 60 * 60 * 1000L;

    /**
     * The expiration time for password recovery tokens.
     */
    private static final Long TOKEN_EXPIRATION_TIME = 30 * 60 * 1000L;

    /**
     * The logger for this class.
     */
    private final Logger logger;


    /**
     * Constructs a new instance of the verification token service implementation.
     *
     * @param passwordEncoder             The password encoder used for encoding passwords.
     * @param verificationTokenRepository The repository for managing verification tokens.
     * @param userRepository              The repository for managing users.
     * @param emailClient                 EmailClient instance for sending emails.
     */

    public VerificationTokenServiceImpl(final PasswordEncoder passwordEncoder,
                                        final VerificationTokenRepository verificationTokenRepository,
                                        final UserRepository userRepository,
                                        final EmailClient emailClient) {
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenRepository = verificationTokenRepository;
        this.userRepository = userRepository;
        this.emailClient = emailClient;
        this.logger = LoggerFactory.getLogger(VerificationTokenServiceImpl.class);
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
                    new Date(System.currentTimeMillis() + EXPIRATION_TIME_CONFIRMATION));
        } else if (Objects.equals(type, FORGOT_PASSWORD)) {
            verificationToken.setExpiryDate(
                    new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME));
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
                || !Objects.equals(verificationToken.getType(),
                "confirm-account")) {
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
     * @param token               The token that is used to reinstate a forgotten password
     * @param changePasswordModel The model that contains the new password and the confirmed one
     * @return A response entity with a message indicating whether the password update was successful or not
     */
    @Override
    public ResponseEntity<String> reinstateForgotPassword(final String token,
                                                          final PasswordChangeModel changePasswordModel) {
        VerificationToken verificationToken = findByToken(token);
        if (verificationToken == null
                ||
                !Objects.equals(verificationToken.getType(), FORGOT_PASSWORD)) {
            return ResponseEntity.badRequest().body("le lien est invalide");
        }
        if (verificationToken.getExpiryDate().before(new Date())) {
            removeByToken(token);
            return ResponseEntity.badRequest().body("le lien est expiré");
        }
        if (!Objects.equals(
                changePasswordModel.getNewPassword(),
                changePasswordModel.getConfirmPassword())) {
            return ResponseEntity.badRequest()
                    .body("les mots de passes ne sont pas identiques");
        }
        User user = verificationToken.getUser();
        user.setUserPassword(
                passwordEncoder.encode(changePasswordModel.getNewPassword()));
        return ResponseEntity.ok("Le mot de passe est mis à jour");
    }

    /**
     * This method is used to initiate and send a reset password email request.
     * It reads the user email from the JSON string, retrieves the user from the database, and creates a verification token.
     * It then sends an email to the user email address with a link to reset his password.
     *
     * @param jsonRequestBody The JSON string containing the user email
     * @return A response entity with a message indicating whether the forgot password request was successful or not
     * @throws JsonProcessingException  If the JSON string cannot be processed
     * @throws UserNotVerifiedException If the user has not verified their email address
     */
    @SuppressWarnings("checkstyle:LocalVariableName")
    @Override
    public ResponseEntity<String> sendResetPasswordEmail(final String jsonRequestBody)
            throws JsonProcessingException, UserNotVerifiedException {
        JsonNode emailNode = new ObjectMapper().readTree(jsonRequestBody);
        String userEmail = emailNode.path("email").asText();
        User user = userRepository.findByUserEmail(userEmail);
        if (Objects.isNull(user)) {
            return ResponseEntity.badRequest()
                    .body("Aucun compte n'a été trouvé pour l'e-mail " + userEmail + ", Veuillez réessayer.");
        }
        if (Boolean.FALSE.equals(user.getIsEmailVerified())) {
            throw new UserNotVerifiedException(
                    "Votre compte n'est pas vérifié");
        }
        VerificationToken verificationToken = createToken(user, FORGOT_PASSWORD);
        String recoveryUrl =
                frontEndUrl + "/reset-password?token=" + verificationToken.getToken();
        String mailSubject = "ELIT : Réinitialisez votre mot de passe";
        ClassPathResource logoFile = new ClassPathResource("avatars/LogoImageEmail.png");
      String base64Image = "";
      try {
        byte[] bytes = Files.readAllBytes(Path.of(logoFile.getURI()));
        base64Image = Base64.getEncoder().encodeToString(bytes);
      } catch (IOException e) {
        throw new RuntimeException("Erreur lors de la lecture du fichier image", e);
      }
      String mailBody = "<html><body style='margin: 0; padding: 0; background-color: #f4f4f4; font-family: Arial, sans-serif;'> "
        + "<div style='max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden;'> "
        + "<div style='background-color: #e9f2fb; padding: 50px; text-align: center;'> "
        + "<img src='data:image/png;base64," + base64Image + "' alt='ELIT' style='display: block; margin: auto;'> "
        + "</div> "
        + "<div style='padding: 20px; text-align: left; font-size: medium;'> "
        + "<p>Bonjour " + user.getUserFirstName() + ",</p> "
        + "<p>Vous y êtes presque ! </p> "
        + "<p>Veuillez cliquer sur le lien ci-dessous afin de réinitialiser votre mot de passe :</p> "
        + "<div style='text-align: center;'> "
        + "<a href='" + recoveryUrl + "' style='display: inline-block; padding: 14px 20px; margin: 20px 0; font-size: 16px; color: #ffffff; background-color: #116E9C; text-decoration: none; border-radius: 8px;'>Réinitialiser le mot de passe</a> "
        + "</div> "
        + "<p>Attention, le lien a une durée de validité de 48 heures.</p> "
        + "<p>Cordialement, L'équipe ELIT</p> "
        + "</div> "
        + "<hr style='border: none; height: 5px; background-color: #12abdb; width: calc(100% - 40px);'> "
        + "<div style='display: flex; align-items: center; padding: 2px 20px 20px 2px;'> "
        + "<img src='data:image/png;base64," + base64Image + "' alt='ELIT' style='margin-right: 20px; height: 30px;'> "
        + "<span style='font-size: 12px; color: #666666;'>La méthodologie à la mesure de votre projet</span> "
        + "</div> "
        + "</div> "
        + "</body></html>";

      try {
            EmailMessage message = new EmailMessage()
                    .setSenderAddress(senderEmail)
                    .setToRecipients(user.getUserEmail())
                    .setSubject(mailSubject)
                    .setBodyHtml(mailBody);

            SyncPoller<EmailSendResult, EmailSendResult> poller =
                    emailClient.beginSend(message, null);
            PollResponse<EmailSendResult> pollResponse = null;
            Duration timeElapsed = Duration.ofSeconds(0);
            Duration pollerWaitTime = Duration.ofSeconds(10);

            while (pollResponse == null
                    || pollResponse.getStatus() == LongRunningOperationStatus.NOT_STARTED
                    || pollResponse.getStatus() == LongRunningOperationStatus.IN_PROGRESS) {
                try {
                    pollResponse = poller.poll();
                    Thread.sleep(pollerWaitTime.toMillis());
                    timeElapsed = timeElapsed.plus(pollerWaitTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread sleep interrupted", e);
                }

                if (timeElapsed.compareTo(pollerWaitTime.multipliedBy(18)) >= 0) {
                    throw new RuntimeException("Polling timed out.");
                }
            }

            if (poller.getFinalResult().getStatus() == EmailSendStatus.SUCCEEDED) {
                return ResponseEntity.ok("Email de récupération envoyé");
            } else {
                throw new RuntimeException(poller.getFinalResult().getError().getMessage());
            }

        } catch (AzureException e) {
            logger.error(
                    "Erreur lors de l'envoi du mail de récupération de mot de passe", e);
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                    .body("Une erreur est survenue lors de l'envoi du mail de récupération de mot de passe." + " Veuillez réessayer plus tard;");
        }

    }
}
