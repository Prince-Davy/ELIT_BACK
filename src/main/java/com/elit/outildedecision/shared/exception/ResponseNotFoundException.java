package com.elit.outildedecision.shared.exception;

/** Response not found exception class. */
public class ResponseNotFoundException extends NotFoundException {
  /**
   * Constructor for the ResponseNotFoundException class.
   * Exception thrown when a response is not found.
   * @param message the message to be displayed
   */
  public ResponseNotFoundException(final String message) {
    super(message);
  }
}
