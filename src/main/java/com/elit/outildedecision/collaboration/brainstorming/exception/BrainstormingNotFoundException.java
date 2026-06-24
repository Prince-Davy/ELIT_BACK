package com.elit.outildedecision.collaboration.brainstorming.exception;

import com.elit.outildedecision.shared.exception.NotFoundException;

/** Brainstorming not found exception class. */
public class BrainstormingNotFoundException extends NotFoundException {
  /**
   * Constructor for BrainstormingNotFoundException.
   * Exception thrown when a brainstorming is not found.
   * @param message the message to display
   */
  public BrainstormingNotFoundException(final String message) {
    super(message);
  }
}
