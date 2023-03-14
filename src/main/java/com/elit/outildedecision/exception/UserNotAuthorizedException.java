package com.elit.outildedecision.exception;

public class UserNotAuthorizedException extends Throwable {
  /**
   * Constructor for UserNotAuthorizedException.
   * Exception thrown when a user is not authorized to perform an action.
   * @param message the message to be displayed
   */
  public UserNotAuthorizedException(final String message) {
    super(message);
  }
}
