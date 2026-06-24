package com.elit.outildedecision.shared.user.exception;

import com.elit.outildedecision.shared.exception.NotFoundException;

/** User not found exception class. */
public class UserNotFoundException extends NotFoundException {
  /**
   * Constructor for UserNotFoundException.
   * Exception thrown when a user is not found.
   * @param message the message to be displayed
   */
  public UserNotFoundException(final String message) {
    super(message);
  }
}
