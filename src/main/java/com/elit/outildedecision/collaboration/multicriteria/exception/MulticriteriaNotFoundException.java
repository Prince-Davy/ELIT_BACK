package com.elit.outildedecision.collaboration.multicriteria.exception;

import com.elit.outildedecision.shared.exception.NotFoundException;

/** Multicriteria not found exception class. */
public class MulticriteriaNotFoundException extends NotFoundException {
  /**
   * Constructor for MulticriteriaNotFoundException.
   * Exception thrown when a multicriteria analysis is not found.
   * @param message the message to be displayed
   */
  public MulticriteriaNotFoundException(final String message) {
    super(message);
  }
}
