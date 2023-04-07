package com.elit.outildedecision.exception;

public class ReportNotFoundException extends Exception {
  /**
   * Constructor for the ReportNotFoundException class.
   * Exception thrown when a report is not found.
   * @param message the message to be displayed
   */
  public ReportNotFoundException(final String message) {
    super(message);
  }
}
