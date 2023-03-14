package com.elit.outildedecision.exception;

public class ResponseNotFoundException extends Exception {
  /**
   * Constructor for the ResponseNotFoundException class.
   * Exception thrown when a response is not found.
   * @param message the message to be displayed
   */
  public ResponseNotFoundException(final String message) {
    super(message);
  }
}
