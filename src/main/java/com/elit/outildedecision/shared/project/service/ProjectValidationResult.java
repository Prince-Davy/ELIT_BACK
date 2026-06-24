package com.elit.outildedecision.shared.project.service;

/**
 * Outcome of an admin validation/rejection request on a project. The controller maps each
 * outcome to the appropriate HTTP status and message, keeping the service free of HTTP types.
 */
public enum ProjectValidationResult {
  /**
   * The project was already confirmed; the request is a no-op.
   */
  ALREADY_CONFIRMED,
  /**
   * The project has just been confirmed.
   */
  VALIDATED,
  /**
   * The project was already rejected; the request is a no-op.
   */
  ALREADY_REJECTED,
  /**
   * The project has just been rejected.
   */
  REJECTED
}
