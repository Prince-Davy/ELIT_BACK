package com.elit.outildedecision.collaboration.multicriteria.exception;

import com.elit.outildedecision.shared.exception.NotFoundException;

/** Step form not found exception class. */
public class StepFormNotFoundException extends NotFoundException {
  /**
   * Constructor for StepFormNotFoundException.
   * Exception thrown when a step form is not found.
   * @param message the message to be displayed
   */
  public StepFormNotFoundException(final String message) {
    super(message);
  }
}
