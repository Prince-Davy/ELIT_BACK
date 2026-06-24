package com.elit.outildedecision.shared.auth.exception;

/**
 * Raised when a JWT cannot be trusted (missing, malformed, expired or tampered with).
 *
 * <p>Handled locally by the security filters and the refresh endpoint to answer {@code 403};
 * it intentionally does not reach the global handler.
 */
public class InvalidJwtException extends RuntimeException {

  /**
   * Builds the exception with a human readable message.
   *
   * @param message the failure description
   */
  public InvalidJwtException(final String message) {
    super(message);
  }

  /**
   * Builds the exception with a message and the underlying cause.
   *
   * @param message the failure description
   * @param cause   the underlying cause
   */
  public InvalidJwtException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
