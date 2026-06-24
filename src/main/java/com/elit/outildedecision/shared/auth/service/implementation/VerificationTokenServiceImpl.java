package com.elit.outildedecision.shared.auth.service.implementation;

import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.auth.entity.VerificationToken;
import com.elit.outildedecision.shared.auth.dto.PasswordChangeRequest;
import com.elit.outildedecision.shared.auth.service.ConfirmationOutcome;
import com.elit.outildedecision.shared.auth.service.PasswordResetOutcome;
import com.elit.outildedecision.shared.auth.service.ResetEmailOutcome;
import com.elit.outildedecision.shared.auth.service.ResetPasswordEmailResult;
import com.elit.outildedecision.shared.user.repository.UserRepository;
import com.elit.outildedecision.shared.auth.repository.VerificationTokenRepository;
import com.elit.outildedecision.shared.auth.service.VerificationMailService;
import com.elit.outildedecision.shared.auth.service.VerificationTokenService;
import com.elit.outildedecision.shared.mailer.exception.MailDeliveryException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * Reusable JSON mapper (thread-safe once configured).
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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
     * Service composing and sending the token-related e-mails.
     */
    private final VerificationMailService verificationMailService;

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
     * @param verificationMailService     Service composing and sending the token-related e-mails.
     */

    public VerificationTokenServiceImpl(final PasswordEncoder passwordEncoder,
                                        final VerificationTokenRepository verificationTokenRepository,
                                        final UserRepository userRepository,
                                        final VerificationMailService verificationMailService) {
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenRepository = verificationTokenRepository;
        this.userRepository = userRepository;
        this.verificationMailService = verificationMailService;
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
     * @return the business outcome of the confirmation.
     */
    @Override
    public ConfirmationOutcome confirmUser(final String token) {
        VerificationToken verificationToken = findByToken(token);
        if (verificationToken == null
                || !Objects.equals(verificationToken.getType(),
                "confirm-account")) {
            return ConfirmationOutcome.INVALID_TOKEN;
        }
        if (verificationToken.getExpiryDate().before(new Date())) {
            removeByToken(token);
            return ConfirmationOutcome.EXPIRED;
        }
        removeByToken(token);
        User user = verificationToken.getUser();
        user.setIsUserActive(true);
        user.setIsEmailVerified(true);
        userRepository.save(user);
        return ConfirmationOutcome.CONFIRMED;
    }

    /**
     * This method is used to reinstate a forgotten password. It checks whether the token is valid and
     * not expired. It also checks whether the new password and confirm password match, and if they do,
     * it updates the user's password.
     *
     * @param token               The token that is used to reinstate a forgotten password
     * @param changePasswordModel The model that contains the new password and the confirmed one
     * @return the business outcome of the password reset
     */
    @Override
    public PasswordResetOutcome reinstateForgotPassword(final String token,
                                                          final PasswordChangeRequest changePasswordModel) {
        VerificationToken verificationToken = findByToken(token);
        if (verificationToken == null
                ||
                !Objects.equals(verificationToken.getType(), FORGOT_PASSWORD)) {
            return PasswordResetOutcome.INVALID_LINK;
        }
        if (verificationToken.getExpiryDate().before(new Date())) {
            removeByToken(token);
            return PasswordResetOutcome.EXPIRED_LINK;
        }
        if (!Objects.equals(
                changePasswordModel.getNewPassword(),
                changePasswordModel.getConfirmPassword())) {
            return PasswordResetOutcome.PASSWORD_MISMATCH;
        }
        User user = verificationToken.getUser();
        user.setUserPassword(
                passwordEncoder.encode(changePasswordModel.getNewPassword()));
        userRepository.save(user);
        removeByToken(token);
        return PasswordResetOutcome.UPDATED;
    }

    /**
     * This method is used to initiate and send a reset password email request.
     * It reads the user email from the JSON string, retrieves the user from the database, and creates a verification token.
     * It then sends an email to the user email address with a link to reset his password.
     *
     * @param jsonRequestBody The JSON string containing the user email
     * @return the business outcome of the reset-email request (and the message detail when needed)
     */
    @Override
    public ResetPasswordEmailResult sendResetPasswordEmail(final String jsonRequestBody) {
        String userEmail;
        try {
            JsonNode emailNode = OBJECT_MAPPER.readTree(jsonRequestBody);
            userEmail = emailNode.path("email").asText();
        } catch (JsonProcessingException e) {
            return new ResetPasswordEmailResult(ResetEmailOutcome.INVALID_JSON, e.getMessage());
        }
        User user = userRepository.findByUserEmail(userEmail);
        if (Objects.isNull(user)) {
            return new ResetPasswordEmailResult(ResetEmailOutcome.ACCOUNT_NOT_FOUND, userEmail);
        }
        if (Boolean.FALSE.equals(user.getIsEmailVerified())) {
            return new ResetPasswordEmailResult(
                    ResetEmailOutcome.NOT_VERIFIED, "Votre compte n'est pas vérifié");
        }
        VerificationToken verificationToken = createToken(user, FORGOT_PASSWORD);
        try {
            verificationMailService.sendPasswordResetEmail(user, verificationToken.getToken());
            return ResetPasswordEmailResult.of(ResetEmailOutcome.SENT);
        } catch (MailDeliveryException e) {
            logger.error(
                    "Erreur lors de l'envoi du mail de récupération de mot de passe pour userId={}",
                    user.getUserId(), e);
            return ResetPasswordEmailResult.of(ResetEmailOutcome.MAIL_FAILED);
        }
    }
}
