package com.elit.outildedecision.exception;

public class BrainstormingNotFoundException extends Exception {
  /**
   * Constructor for BrainstormingNotFoundException.
   * Exception thrown when a brainstorming is not found.
   * @param message the message to display
   */
  public BrainstormingNotFoundException(final String message) {
    super(message);
  }
}
