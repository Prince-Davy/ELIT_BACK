package com.elit.outildedecision.exception;

public class AnswerIaNotFoundException extends Exception {
  /**
   * Constructor for AnswerIaNotFoundException.
   * Exception thrown when a AnswerIa is not found.
   * @param message the message to be displayed
   */
  public AnswerIaNotFoundException(final String message) {
    super(message);
  }
}
