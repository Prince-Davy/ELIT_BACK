package com.elit.outildedecision.exception;

public class ProjectFolderAlreadyExistsException extends Exception {
  /**
   * Constructor for ProjectFolderAlreadyExistsException.
   * Exception thrown when a project folder already exists.
   * @param message the message to be displayed
   */
  public ProjectFolderAlreadyExistsException(final String message) {
    super(message);
  }
}
