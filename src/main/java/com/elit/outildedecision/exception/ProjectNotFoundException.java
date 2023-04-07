package com.elit.outildedecision.exception;

public class ProjectNotFoundException extends Exception {
  /**
   * Constructor for ProjectNotFoundException.
   * Exception thrown when a project is not found in the database.
   * @param message the message to be displayed
   */
  public ProjectNotFoundException(final String message) {
    super(message);
  }
}
