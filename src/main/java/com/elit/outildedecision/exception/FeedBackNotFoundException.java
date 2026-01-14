package com.elit.outildedecision.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception thrown when a requested feedback is not found.
 * Now extends ResponseStatusException to ensure a correct HTTP 404 is sent to the client
 * and to simplify message handling in the global exception handler.
 */
public class FeedBackNotFoundException extends ResponseStatusException {

  /**
   * Constructor for FeedBackNotFoundException.
   * @param message the exception message
   */
  public FeedBackNotFoundException(final String message) {
    super(HttpStatus.NOT_FOUND, message);
  }
}
