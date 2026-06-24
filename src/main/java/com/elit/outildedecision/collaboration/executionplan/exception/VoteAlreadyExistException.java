package com.elit.outildedecision.collaboration.executionplan.exception;

import com.elit.outildedecision.shared.exception.ConflictException;

/** Vote already exist exception class. */
public class VoteAlreadyExistException extends ConflictException {
  /**
   * Constructor for VoteAlreadyExistException.
   * Exception thrown when a vote already exist for a given user and a given decision.
   * @param message the message to be displayed
   */
  public VoteAlreadyExistException(final String message) {
    super(message);
  }
}
