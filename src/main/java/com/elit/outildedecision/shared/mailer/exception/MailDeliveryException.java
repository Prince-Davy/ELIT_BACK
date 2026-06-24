package com.elit.outildedecision.shared.mailer.exception;

/**
 * Unchecked exception raised when an outbound email could not be delivered
 * (transport failure, polling timeout or interruption).
 *
 * <p>It is intentionally not an {@code ApiException}: it bubbles up to the generic
 * 500 handler so the underlying provider message is never exposed to the client.
 */
public class MailDeliveryException extends RuntimeException {

  /**
   * Builds the exception with a human readable message.
   *
   * @param message the message describing the delivery failure
   */
  public MailDeliveryException(final String message) {
    super(message);
  }

  /**
   * Builds the exception with a message and the underlying cause.
   *
   * @param message the message describing the delivery failure
   * @param cause   the underlying cause
   */
  public MailDeliveryException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
