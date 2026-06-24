package com.elit.outildedecision.shared.exception;

/** Could not mail user exception class. */
public class CouldNotMailUserException extends Exception {
  /**
   * Constructor for CouldNotSaveUserException.
   * Exception thrown when a user could not be saved.
   * @param message the message to be displayed
   */
  public CouldNotMailUserException(final String message) {
    super(message);
  }
}
