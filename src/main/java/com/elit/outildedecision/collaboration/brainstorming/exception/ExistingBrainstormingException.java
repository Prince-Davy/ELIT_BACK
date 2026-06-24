package com.elit.outildedecision.collaboration.brainstorming.exception;

import com.elit.outildedecision.shared.exception.ConflictException;

/** Existing brainstorming exception class. */
public class ExistingBrainstormingException extends ConflictException {
  /**
   * Constructor for ExistingBrainstormingException.
   * Exception thrown when a brainstorming with the same name already exists.
   * @param message the message to be displayed
   */
  public ExistingBrainstormingException(final String message) {
    super(message);
  }
}
