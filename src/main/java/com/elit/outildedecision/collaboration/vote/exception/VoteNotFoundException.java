package com.elit.outildedecision.collaboration.vote.exception;

import com.elit.outildedecision.shared.exception.NotFoundException;

/** Vote not found exception class. */
public class VoteNotFoundException extends NotFoundException {
  /**
   * Constructor for VoteNotFoundException.
   * Exception thrown when a vote already exist for a given user and a given decision.
   * @param message the message to be displayed
   */
  public VoteNotFoundException(final String message) {
    super(message);
  }
}
