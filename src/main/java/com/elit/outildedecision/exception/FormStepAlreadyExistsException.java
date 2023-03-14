package com.elit.outildedecision.exception;

public class FormStepAlreadyExistsException extends Exception {
  /**
   * Constructor for FormStepAlreadyExistsException.
   * Exception thrown when a form step already exists.
   * @param message the message to be displayed
   */
  public FormStepAlreadyExistsException(final String message) {
    super(message);
  }
}
