package com.elit.outildedecision.exception;

public class StepFormNotFoundException extends Exception {
  /**
   * Constructor for StepFormNotFoundException.
   * Exception thrown when a step form is not found.
   * @param message the message to be displayed
   */
  public StepFormNotFoundException(final String message) {
    super(message);
  }
}
