/**
 * Transverse mailer module: generic e-mail transport and reusable branded HTML template.
 *
 * <p>This package knows nothing about tokens, users or any bounded context. It exposes a
 * {@code MailSender} abstraction over the e-mail provider and a {@code BrandedMailTemplate} builder;
 * callers compose their domain-specific content and hand a {@link com.elit.outildedecision.shared.mailer.MailMessage}
 * to the sender.
 */
package com.elit.outildedecision.shared.mailer;
