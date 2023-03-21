package com.elit.outildedecision.exception;

public class VoteAlreadyExistException extends Exception {
  /**
   * Constructor for VoteAlreadyExistException.
   * Exception thrown when a vote already exist for a given user and a given decision.
   * @param message the message to be displayed
   */
  public VoteAlreadyExistException(final String message) {
    super(message);
  }
}
