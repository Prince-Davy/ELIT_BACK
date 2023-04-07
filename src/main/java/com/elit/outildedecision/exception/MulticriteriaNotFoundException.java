package com.elit.outildedecision.exception;

public class MulticriteriaNotFoundException extends Exception {
  /**
   * Constructor for MulticriteriaNotFoundException.
   * Exception thrown when a multicriteria analysis is not found.
   * @param message the message to be displayed
   */
  public MulticriteriaNotFoundException(final String message) {
    super(message);
  }
}
