package com.elit.outildedecision.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Base type for all business exceptions handled by the global exception handler.
 *
 * <p>Each instance carries the {@link HttpStatus} that must be returned to the
 * client, so the global handler can map any business error generically without
 * importing concrete feature exceptions (plan §6, Étape 5bis).
 */
public abstract class ApiException extends RuntimeException {

  private final HttpStatus status;

  /**
   * Builds an exception carrying the HTTP status to expose to the client.
   *
   * @param status  the HTTP status to return
   * @param message the human readable message
   */
  protected ApiException(final HttpStatus status, final String message) {
    super(message);
    this.status = status;
  }

  /**
   * Builds an exception carrying the HTTP status and the underlying cause.
   *
   * @param status  the HTTP status to return
   * @param message the human readable message
   * @param cause   the underlying cause
   */
  protected ApiException(final HttpStatus status, final String message, final Throwable cause) {
    super(message, cause);
    this.status = status;
  }

  /**
   * Returns the HTTP status the global handler must expose for this exception.
   *
   * @return the HTTP status
   */
  public HttpStatus getStatus() {
    return status;
  }
}
