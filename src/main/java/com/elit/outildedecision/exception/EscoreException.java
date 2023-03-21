package com.elit.outildedecision.exception;

public class EscoreException extends Exception {
  /**
   * Constructor for EscoreException.
   * Exception thrown when an error occurs in the Escore calculation.
   * @param message the message to be displayed
   */
  public EscoreException(final String message) {
    super(message);
  }
}
