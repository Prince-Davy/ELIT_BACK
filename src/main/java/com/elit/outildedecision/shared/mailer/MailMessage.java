package com.elit.outildedecision.shared.mailer;

/**
 * Immutable value object describing an outbound e-mail to be delivered by a {@code MailSender}.
 *
 * <p>It carries only the recipient-facing payload; the sender address and the transport mechanism
 * are owned by the {@code MailSender} implementation.
 *
 * @param to       the recipient e-mail address
 * @param subject  the e-mail subject
 * @param htmlBody the HTML body of the e-mail
 */
public record MailMessage(String to, String subject, String htmlBody) {
}
