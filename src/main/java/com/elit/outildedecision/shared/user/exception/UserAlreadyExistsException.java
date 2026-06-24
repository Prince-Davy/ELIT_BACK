package com.elit.outildedecision.shared.user.exception;

import com.elit.outildedecision.shared.exception.ConflictException;

/** User already exists exception class. */
public class UserAlreadyExistsException extends ConflictException {
  /**
   * Constructor for UserAlreadyExistsException.
   * Exception thrown when a user already exists in the database.
   * @param message the message to be displayed
   */
  public UserAlreadyExistsException(final String message) {
    super(message);
  }
}
