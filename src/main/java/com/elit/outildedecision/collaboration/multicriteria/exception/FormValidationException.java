package com.elit.outildedecision.collaboration.multicriteria.exception;

import com.elit.outildedecision.shared.exception.BadRequestException;

/**
 * Exception thrown when a multi-criteria analysis form validation fails.
 * This exception contains information about which questions have validation errors,
 * including missing text answers and missing evaluation criteria.
 */
public class FormValidationException extends BadRequestException {

  /**
   * Constructor for FormValidationException.
   *
   * @param message the error message describing the validation errors
   */
  public FormValidationException(final String message) {
    super(message);
  }

  /**
   * Constructor for FormValidationException with cause.
   *
   * @param message the error message describing the validation errors
   * @param cause the cause of the validation exception
   */
  public FormValidationException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
