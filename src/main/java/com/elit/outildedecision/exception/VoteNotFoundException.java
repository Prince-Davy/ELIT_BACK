package com.elit.outildedecision.exception;

public class VoteNotFoundException extends Exception {
  /**
   * Constructor for VoteNotFoundException.
   * Exception thrown when a vote already exist for a given user and a given decision.
   * @param message the message to be displayed
   */
  public VoteNotFoundException(final String message) {
    super(message);
  }
}
