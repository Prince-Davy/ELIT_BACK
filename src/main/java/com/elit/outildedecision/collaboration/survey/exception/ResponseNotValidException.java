package com.elit.outildedecision.collaboration.survey.exception;

import com.elit.outildedecision.shared.exception.BadRequestException;

/** Response not valid exception class. */
public class ResponseNotValidException extends BadRequestException {
  /**
   * Constructor for ResponseNotValidException.
   * Exception thrown when the response from the API is not valid.
   * @param message the message to be displayed
   */
  public ResponseNotValidException(final String message) {
    super(message);
  }
}
