package com.elit.outildedecision.exception;

public class UserNotVerifiedException extends Exception {
  /**
   * Constructor for UserNotVerifiedException.
   * Exception thrown when a user is not verified.
   * @param message the message to be displayed
   */
  public UserNotVerifiedException(final String message) {
    super(message);
  }
}
