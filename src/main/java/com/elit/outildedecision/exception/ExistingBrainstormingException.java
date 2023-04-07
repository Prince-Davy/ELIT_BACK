package com.elit.outildedecision.exception;

public class ExistingBrainstormingException extends Exception {
  /**
   * Constructor for ExistingBrainstormingException.
   * Exception thrown when a brainstorming with the same name already exists.
   * @param message the message to be displayed
   */
  public ExistingBrainstormingException(final String message) {
    super(message);
  }
}
