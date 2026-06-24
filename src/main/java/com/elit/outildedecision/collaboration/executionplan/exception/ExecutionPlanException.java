package com.elit.outildedecision.collaboration.executionplan.exception;

import com.elit.outildedecision.shared.exception.ConflictException;

/**
 * the exception for execution plan.
 */
public class ExecutionPlanException extends ConflictException {
  /**
   * the constructor for ExecutionPlanException.
   * @param message the message
   */
  public ExecutionPlanException(final String message) {
    super(message);
  }
}
