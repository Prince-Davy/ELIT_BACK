package com.elit.outildedecision.exception;

public class ResponseNotValidException extends Exception {
  /**
   * Constructor for ResponseNotValidException.
   * Exception thrown when the response from the API is not valid.
   * @param message the message to be displayed
   */
  public ResponseNotValidException(final String message) {
    super(message);
  }
}
