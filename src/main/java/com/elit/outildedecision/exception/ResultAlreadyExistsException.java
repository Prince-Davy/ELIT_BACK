package com.elit.outildedecision.exception;

public class ResultAlreadyExistsException extends Exception {
  /**
   * Constructor for ResultAlreadyExistsException.
   * Exception thrown when a ResultAi is already in the database.
   * @param message the message to be displayed
   */
  public ResultAlreadyExistsException(final String message) {
    super(message);
  }
}
