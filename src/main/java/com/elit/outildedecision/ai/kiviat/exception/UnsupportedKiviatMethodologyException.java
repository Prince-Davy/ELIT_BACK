package com.elit.outildedecision.ai.kiviat.exception;

import com.elit.outildedecision.shared.exception.ApiException;
import org.springframework.http.HttpStatus;

/**
 * Raised when an AI methodology has no Kiviat reference axis mapping. Defensive: the supported
 * methodologies are exhaustively handled, so reaching this signals an inconsistent state.
 */
public class UnsupportedKiviatMethodologyException extends ApiException {

  /**
   * Builds the exception with a 500 status (inconsistent server-side mapping).
   *
   * @param message the human readable message
   */
  public UnsupportedKiviatMethodologyException(final String message) {
    super(HttpStatus.INTERNAL_SERVER_ERROR, message);
  }
}
