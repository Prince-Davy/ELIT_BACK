package com.elit.outildedecision.shared.auth.service;

import com.elit.outildedecision.shared.kernel.entity.User;

/**
 * Composes and sends the e-mails tied to verification tokens (account confirmation and password
 * reset).
 *
 * <p>This is the single place where token-related e-mail content lives: it builds the front-end URL
 * and the branded HTML, then delegates delivery to the transverse mailer. The methods are pure side
 * effects (no database write), so they can run after the owning transaction has committed.
 */
public interface VerificationMailService {

  /**
   * Sends the account-confirmation e-mail carrying the given confirmation token.
   *
   * @param user  the recipient of the e-mail
   * @param token the confirmation token already persisted with the account
   * @throws MailDeliveryException if the e-mail could not be delivered
   */
  void sendAccountConfirmationEmail(User user, String token);

  /**
   * Sends the password-reset e-mail carrying the given recovery token.
   *
   * @param user  the recipient of the e-mail
   * @param token the password-reset token
   * @throws MailDeliveryException if the e-mail could not be delivered
   */
  void sendPasswordResetEmail(User user, String token);
}
