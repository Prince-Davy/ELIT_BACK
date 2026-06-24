package com.elit.outildedecision.shared.auth.service.implementation;

import com.elit.outildedecision.shared.auth.service.VerificationMailService;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.mailer.MailMessage;
import com.elit.outildedecision.shared.mailer.service.MailSender;
import com.elit.outildedecision.shared.mailer.template.BrandedMailTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Default {@link VerificationMailService} composing the token e-mails from the branded template and
 * delegating delivery to the {@link MailSender}.
 */
@Service
@Slf4j
public class VerificationMailServiceImpl implements VerificationMailService {

  /**
   * Subject of the account-confirmation e-mail.
   */
  private static final String CONFIRMATION_SUBJECT = "ELIT : Activez votre compte";

  /**
   * Subject of the password-reset e-mail.
   */
  private static final String RESET_SUBJECT = "ELIT : Réinitialisez votre mot de passe";

  /**
   * Body sentence of the account-confirmation e-mail.
   */
  private static final String CONFIRMATION_BODY =
      "Veuillez cliquer sur le lien ci-dessous afin de finaliser la création de votre compte.";

  /**
   * Body sentence of the password-reset e-mail.
   */
  private static final String RESET_BODY =
      "Veuillez cliquer sur le lien ci-dessous afin de réinitialiser votre mot de passe :";

  /**
   * Call-to-action label of the account-confirmation e-mail.
   */
  private static final String CONFIRMATION_CTA = "Confirmation d'inscription";

  /**
   * Call-to-action label of the password-reset e-mail.
   */
  private static final String RESET_CTA = "Réinitialiser le mot de passe";

  /**
   * Classpath of the logo used in the account-confirmation e-mail.
   */
  private static final String CONFIRMATION_LOGO = "avatars/logoImage.png";

  /**
   * Classpath of the logo used in the password-reset e-mail.
   */
  private static final String RESET_LOGO = "avatars/LogoImageEmail.png";

  /**
   * Front-end base URL used to build the confirmation and reset links.
   */
  @Value("${frontend.value-url}")
  private String frontEndUrl;

  /**
   * Transport used to deliver the composed e-mails.
   */
  private final MailSender mailSender;

  /**
   * Builder of the branded HTML body.
   */
  private final BrandedMailTemplate template;

  /**
   * Builds the service.
   *
   * @param mailSender the e-mail transport
   * @param template   the branded HTML template builder
   */
  public VerificationMailServiceImpl(final MailSender mailSender, final BrandedMailTemplate template) {
    this.mailSender = mailSender;
    this.template = template;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void sendAccountConfirmationEmail(final User user, final String token) {
    String confirmationUrl =
        frontEndUrl + "/confirm-account?token=" + token + "&id=" + user.getUserId();
    log.info("Sending registration confirmation email for userId={}", user.getUserId());
    String htmlBody = template.build(
        user.getUserFirstName(), CONFIRMATION_BODY, CONFIRMATION_CTA, confirmationUrl, CONFIRMATION_LOGO);
    mailSender.send(new MailMessage(user.getUserEmail(), CONFIRMATION_SUBJECT, htmlBody));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void sendPasswordResetEmail(final User user, final String token) {
    String recoveryUrl = frontEndUrl + "/reset-password?token=" + token;
    log.info("Sending password reset email for userId={}", user.getUserId());
    String htmlBody = template.build(
        user.getUserFirstName(), RESET_BODY, RESET_CTA, recoveryUrl, RESET_LOGO);
    mailSender.send(new MailMessage(user.getUserEmail(), RESET_SUBJECT, htmlBody));
  }
}
