package com.elit.outildedecision.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Reusable business exception mapped to HTTP 403 (Forbidden).
 */
public class ForbiddenException extends ApiException {

  /**
   * Builds a 403 exception.
   *
   * @param message the human readable message
   */
  public ForbiddenException(final String message) {
    super(HttpStatus.FORBIDDEN, message);
  }
}
