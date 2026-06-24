package com.elit.outildedecision.shared.exception;

/** User not authorized exception class. */
public class UserNotAuthorizedException extends Exception {
  /**
   * Constructor for UserNotAuthorizedException.
   * Exception thrown when a user is not authorized to perform an action.
   * @param message the message to be displayed
   */
  public UserNotAuthorizedException(final String message) {
    super(message);
  }
}
