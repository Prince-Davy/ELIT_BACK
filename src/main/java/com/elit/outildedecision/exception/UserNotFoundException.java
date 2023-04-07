package com.elit.outildedecision.exception;

public class UserNotFoundException extends Exception {
  /**
   * Constructor for UserNotFoundException.
   * Exception thrown when a user is not found.
   * @param message the message to be displayed
   */
  public UserNotFoundException(final String message) {
    super(message);
  }
}
