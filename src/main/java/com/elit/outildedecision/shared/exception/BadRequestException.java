package com.elit.outildedecision.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Reusable business exception mapped to HTTP 400 (Bad Request).
 */
public class BadRequestException extends ApiException {

  /**
   * Builds a 400 exception.
   *
   * @param message the human readable message
   */
  public BadRequestException(final String message) {
    super(HttpStatus.BAD_REQUEST, message);
  }

  /**
   * Builds a 400 exception with an underlying cause.
   *
   * @param message the human readable message
   * @param cause   the underlying cause
   */
  public BadRequestException(final String message, final Throwable cause) {
    super(HttpStatus.BAD_REQUEST, message, cause);
  }
}
