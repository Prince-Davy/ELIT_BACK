package com.elit.outildedecision.audit.exception;

import com.elit.outildedecision.shared.exception.NotFoundException;

/** Audit not found exception class. */
public class AuditNotFoundException extends NotFoundException {
  /**
   * Constructor for AuditNotFoundException.
   * Exception thrown when a project is not found in the database.
   * @param message the message to be displayed
   */
  public AuditNotFoundException(final String message) {
    super(message);
  }
}
