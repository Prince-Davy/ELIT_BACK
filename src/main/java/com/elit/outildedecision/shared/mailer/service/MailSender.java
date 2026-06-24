package com.elit.outildedecision.shared.mailer.service;

import com.elit.outildedecision.shared.mailer.MailMessage;

/**
 * Generic abstraction over the e-mail provider.
 *
 * <p>Callers build a {@link MailMessage} and delegate delivery here, staying decoupled from the
 * concrete transport (Azure Communication Services today).
 */
public interface MailSender {

  /**
   * Delivers the given message synchronously.
   *
   * @param message the message to deliver
   * @throws MailDeliveryException if the message could not be delivered
   */
  void send(MailMessage message);
}
