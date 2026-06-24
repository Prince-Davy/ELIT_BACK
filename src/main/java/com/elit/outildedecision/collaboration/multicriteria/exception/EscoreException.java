package com.elit.outildedecision.collaboration.multicriteria.exception;

import com.elit.outildedecision.shared.exception.ApiException;

import org.springframework.http.HttpStatus;

/**
 * Exception mapped to HTTP 406 (Not Acceptable).
 */
public class EscoreException extends ApiException {
  /**
   * Constructor for EscoreException.
   * Exception thrown when an error occurs in the Escore calculation.
   * @param message the message to be displayed
   */
  public EscoreException(final String message) {
    super(HttpStatus.NOT_ACCEPTABLE, message);
  }
}
