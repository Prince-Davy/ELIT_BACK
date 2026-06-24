package com.elit.outildedecision.shared.attachment.exception;

import com.elit.outildedecision.shared.exception.BadRequestException;

/**
 * Thrown when an uploaded attachment is rejected by the upload validation (empty file, oversized
 * file, or a file type forbidden for security reasons). Mapped to HTTP 400.
 */
public class InvalidAttachmentException extends BadRequestException {

  /**
   * Builds the exception.
   *
   * @param message the human readable message (front-end contract, in French)
   */
  public InvalidAttachmentException(final String message) {
    super(message);
  }
}
