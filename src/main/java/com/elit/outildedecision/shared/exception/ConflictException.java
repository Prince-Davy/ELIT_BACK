package com.elit.outildedecision.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Reusable business exception mapped to HTTP 409 (Conflict).
 */
public class ConflictException extends ApiException {

  /**
   * Builds a 409 exception.
   *
   * @param message the human readable message
   */
  public ConflictException(final String message) {
    super(HttpStatus.CONFLICT, message);
  }
}
