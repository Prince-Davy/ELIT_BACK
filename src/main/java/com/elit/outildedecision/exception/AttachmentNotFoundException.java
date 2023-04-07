package com.elit.outildedecision.exception;

public class AttachmentNotFoundException extends Exception {
  /**
   * Constructor for AttachmentNotFoundException.
   * Exception thrown when an attachment is not found.
   * @param message the message to be displayed
   */
  public AttachmentNotFoundException(final String message) {
    super(message);
  }
}
