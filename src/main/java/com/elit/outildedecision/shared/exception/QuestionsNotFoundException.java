package com.elit.outildedecision.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception mapped to HTTP 302 (Found), preserving the project's historical contract.
 */
public class QuestionsNotFoundException extends ApiException {
  /**
   * Constructor for QuestionsNotFoundException.
   * Exception thrown when questions are not found.
   * @param message the message to be displayed
   */
  public QuestionsNotFoundException(final String message) {
    super(HttpStatus.FOUND, message);
  }
}
