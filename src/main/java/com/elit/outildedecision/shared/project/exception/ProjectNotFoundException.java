package com.elit.outildedecision.shared.project.exception;

import com.elit.outildedecision.shared.exception.NotFoundException;

/** Project not found exception class. */
public class ProjectNotFoundException extends NotFoundException {
  /**
   * Constructor for ProjectNotFoundException.
   * Exception thrown when a project is not found in the database.
   * @param message the message to be displayed
   */
  public ProjectNotFoundException(final String message) {
    super(message);
  }
}
