package com.elit.outildedecision.exception;

public class AuditNotFoundException extends Exception {
  /**
   * Constructor for AuditNotFoundException.
   * Exception thrown when a project is not found in the database.
   * @param message the message to be displayed
   */
  public AuditNotFoundException(final String message) {
    super(message);
  }
}
