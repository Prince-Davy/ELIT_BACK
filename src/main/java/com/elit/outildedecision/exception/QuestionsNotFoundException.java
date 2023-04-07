package com.elit.outildedecision.exception;

public class QuestionsNotFoundException extends Exception {
  /**
   * Constructor for QuestionsNotFoundException.
   * Exception thrown when questions are not found.
   * @param message the message to be displayed
   */
  public QuestionsNotFoundException(final String message) {
    super(message);
  }
}
