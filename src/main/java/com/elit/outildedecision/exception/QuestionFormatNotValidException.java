package com.elit.outildedecision.exception;

public class QuestionFormatNotValidException extends Exception {
  /**
   * Constructor of QuestionFormatNotValidException.
   * Exception thrown when the question format is not valid.
   * @param message the message to display
   */
  public QuestionFormatNotValidException(final String message) {
    super(message);
  }
}
