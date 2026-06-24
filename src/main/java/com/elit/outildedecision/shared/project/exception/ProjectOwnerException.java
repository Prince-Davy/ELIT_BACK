package com.elit.outildedecision.shared.project.exception;

import com.elit.outildedecision.shared.exception.NotFoundException;

/** Project owner exception class. */
public class ProjectOwnerException extends NotFoundException {
  /**
   * Constructor of ProjectOwnerException.
   * @param message the message to be displayed
   */
  public ProjectOwnerException(final String message) {
    super(message);
  }
}
