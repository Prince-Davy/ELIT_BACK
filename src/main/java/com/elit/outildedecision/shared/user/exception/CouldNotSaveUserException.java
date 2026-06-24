package com.elit.outildedecision.shared.user.exception;

import com.elit.outildedecision.shared.exception.BadRequestException;

/** Could not save user exception class. */
public class CouldNotSaveUserException extends BadRequestException {
  /**
   * Constructor for CouldNotSaveUserException.
   * Exception thrown when a user could not be saved.
   * @param message the message to be displayed
   */
  public CouldNotSaveUserException(final String message) {
    super(message);
  }
}
