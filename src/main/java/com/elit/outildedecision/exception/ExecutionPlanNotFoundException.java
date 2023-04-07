package com.elit.outildedecision.exception;

public class ExecutionPlanNotFoundException extends Exception {
  /**
   * Constructor for ExecutionPlanNotFoundException.
   * Exception thrown when the execution plan is not found.
   * @param message the message to be displayed
   */
  public ExecutionPlanNotFoundException(final String message) {
    super(message);
  }
}
