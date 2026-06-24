package com.elit.outildedecision.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Reusable business exception mapped to HTTP 404 (Not Found).
 */
public class NotFoundException extends ApiException {

  /**
   * Builds a 404 exception.
   *
   * @param message the human readable message
   */
  public NotFoundException(final String message) {
    super(HttpStatus.NOT_FOUND, message);
  }
}
