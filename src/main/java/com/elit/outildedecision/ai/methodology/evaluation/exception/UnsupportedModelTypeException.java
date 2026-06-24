package com.elit.outildedecision.ai.methodology.evaluation.exception;

import com.elit.outildedecision.shared.exception.ApiException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when the requested model type is unsupported. Mapped to HTTP 422.
 */
public class UnsupportedModelTypeException extends ApiException {
  /**
   * Exceptions to handle unsupported model type.
   *
   * @param message message.
   */
  public UnsupportedModelTypeException(final String message) {
    super(HttpStatus.UNPROCESSABLE_ENTITY, message);
  }
}
