package com.elit.outildedecision.exception;

public class OpinionAlreadyExistException extends Exception {
  /**
   * Constructor for OpinionAlreadyExistException.
   * Exception thrown when an opinion already exist in the database.
   * @param message the message to be displayed
   */
  public OpinionAlreadyExistException(final String message) {
    super(message);
  }
}
