package com.elit.outildedecision.exception;

public class UserAlreadyExistsException extends Exception {
  /**
   * Constructor for UserAlreadyExistsException.
   * Exception thrown when a user already exists in the database.
   * @param message the message to be displayed
   */
  public UserAlreadyExistsException(final String message) {
    super(message);
  }
}
