package com.elit.outildedecision.ai.feedback.exception;

import com.elit.outildedecision.shared.exception.NotFoundException;

/**
 * Exception thrown when a requested feedback is not found. Mapped to HTTP 404.
 */
public class FeedBackNotFoundException extends NotFoundException {

  /**
   * Constructor for FeedBackNotFoundException.
   * @param message the exception message
   */
  public FeedBackNotFoundException(final String message) {
    super(message);
  }
}
