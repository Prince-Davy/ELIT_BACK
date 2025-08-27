package com.elit.outildedecision.ai.answer.exception;

public class AiAnswerNotFoundException extends RuntimeException {
  /**
   * Constructor for IaAnswerNotFoundException.
   * Exception thrown when a IaAnswer is not found.
   * @param message the message to be displayed
   */
  public AiAnswerNotFoundException(final String message) {
    super(message);
  }
}
