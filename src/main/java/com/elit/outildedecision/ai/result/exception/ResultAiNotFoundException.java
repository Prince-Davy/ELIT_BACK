package com.elit.outildedecision.ai.result.exception;

import com.elit.outildedecision.shared.exception.NotFoundException;

/** Result ai not found exception class. */
public class ResultAiNotFoundException extends NotFoundException {
  /**
   * Constructor for ResultAiNotFoundException.
   * Exception thrown when a ResultAi is not found.
   * @param message the message to be displayed
   */
  public ResultAiNotFoundException(final String message) {
    super(message);
  }
}
