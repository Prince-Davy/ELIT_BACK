package com.elit.outildedecision.exception;

public class AvatarNotFoundException extends Exception {
  /**
   * Constructor for AvatarNotFoundException.
   * Exception thrown when an attachment is not found.
   * @param message the message to be displayed
   */
  public AvatarNotFoundException(final String message) {
    super(message);
  }
}
