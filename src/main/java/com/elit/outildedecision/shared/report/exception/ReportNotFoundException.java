package com.elit.outildedecision.shared.report.exception;

import com.elit.outildedecision.shared.exception.NotFoundException;

/**
 * Exception thrown when a report cannot be found. Mapped to HTTP 404 by the global handler.
 */
public class ReportNotFoundException extends NotFoundException {
  /**
   * Constructor for the ReportNotFoundException class.
   * Exception thrown when a report is not found.
   * @param message the message to be displayed
   */
  public ReportNotFoundException(final String message) {
    super(message);
  }
}
