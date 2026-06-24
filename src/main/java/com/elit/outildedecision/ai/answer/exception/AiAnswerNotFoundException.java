package com.elit.outildedecision.ai.answer.exception;

import com.elit.outildedecision.shared.exception.NotFoundException;

/**
 * Exception thrown when an AI answer is not found. Mapped to HTTP 404.
 */
public class AiAnswerNotFoundException extends NotFoundException {
  /**
   * Constructor for AiAnswerNotFoundException.
   * @param message the message to be displayed
   */
  public AiAnswerNotFoundException(final String message) {
    super(message);
  }
}
