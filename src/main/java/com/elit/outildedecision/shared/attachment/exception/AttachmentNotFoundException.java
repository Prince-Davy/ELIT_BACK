package com.elit.outildedecision.shared.attachment.exception;

import com.elit.outildedecision.shared.exception.NotFoundException;

/** Attachment not found exception class. */
public class AttachmentNotFoundException extends NotFoundException {
  /**
   * Constructor for AttachmentNotFoundException.
   * Exception thrown when an attachment is not found.
   * @param message the message to be displayed
   */
  public AttachmentNotFoundException(final String message) {
    super(message);
  }
}
