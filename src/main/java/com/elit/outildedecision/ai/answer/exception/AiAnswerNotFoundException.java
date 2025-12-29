package com.elit.outildedecision.ai.answer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


public class AiAnswerNotFoundException extends ResponseStatusException {
  /**
   * Constructor for IaAnswerNotFoundException.
   * Exception thrown when a IaAnswer is not found.
   * @param message the message to be displayed
   */
  public AiAnswerNotFoundException(final String message) {
    super(HttpStatus.NOT_FOUND, message);
  }
}
