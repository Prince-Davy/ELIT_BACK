package com.elit.outildedecision.exception;

public class StepWeightNotFoundException extends Exception {
  /**
   * Constructor for StepWeightNotFoundException.
   * Exception thrown when a step weight is not found.
   * @param message the message to be displayed
   */
  public StepWeightNotFoundException(final String message) {
    super(message);
  }
}
