package com.elit.outildedecision.ai.result.exception;

import com.elit.outildedecision.shared.exception.ApiException;
import org.springframework.http.HttpStatus;

/**
 * Exception mapped to HTTP 409 (Conflict): an AI result already exists for the project, so a new
 * generation conflicts with the current state.
 */
public class ResultAlreadyExistsException extends ApiException {
  /**
   * Constructor for ResultAlreadyExistsException.
   * Exception thrown when a ResultAi is already in the database.
   * @param message the message to be displayed
   */
  public ResultAlreadyExistsException(final String message) {
    super(HttpStatus.CONFLICT, message);
  }
}
