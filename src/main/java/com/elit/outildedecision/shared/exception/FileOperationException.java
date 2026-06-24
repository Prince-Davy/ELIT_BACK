package com.elit.outildedecision.shared.exception;

/**
 * Unchecked exception raised when a file system operation fails (read, copy or delete)
 * and cannot be recovered.
 *
 * <p>It is intentionally not an {@link ApiException}: it bubbles up to the generic
 * 500 handler so internal file system details are never exposed to the client.
 */
public class FileOperationException extends RuntimeException {

  /**
   * Builds the exception with a message and the underlying cause.
   *
   * @param message the message describing the file operation failure
   * @param cause   the underlying cause
   */
  public FileOperationException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
