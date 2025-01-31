package com.elit.outildedecision.exception;

public class ResultAiNotFoundException extends Exception {
  /**
   * Constructor for ResultAiNotFoundException.
   * Exception thrown when a ResultAi is not found.
   * @param message the message to be displayed
   */
  public ResultAiNotFoundException(final String message) {
    super(message);
  }
}
