package com.elit.outildedecision.exception;

/**
 * This exception is thrown when a token is missing.
 */
public class TokenIsMissingException extends Exception {
  /**
   * Constructs a new TokenIsMissingException with the specified detail message.
   *
   * @param message the detail message
   */
  public TokenIsMissingException(final String message) {
    super(message);
  }
}
