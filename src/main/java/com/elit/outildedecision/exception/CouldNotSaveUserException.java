package com.elit.outildedecision.exception;

public class CouldNotSaveUserException extends Exception {
  /**
   * Constructor for CouldNotSaveUserException.
   * Exception thrown when a user could not be saved.
   * @param message the message to be displayed
   */
  public CouldNotSaveUserException(final String message) {
    super(message);
  }
}
