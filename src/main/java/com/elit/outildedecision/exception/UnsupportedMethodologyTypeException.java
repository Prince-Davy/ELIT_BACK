package com.elit.outildedecision.exception;

/**
 * Runtime exception when methodology cannot be converted to MethodologyType.
 */
public class UnsupportedMethodologyTypeException extends RuntimeException {
  /**
   * Constructor for ResultAiNotFoundException.
   * Exception thrown when a ResultAi is not found.
   * @param message the message to be displayed
   */
  public UnsupportedMethodologyTypeException(final String message) {
    super(message);
  }
}
