package com.elit.outildedecision.shared.user.exception;

import com.elit.outildedecision.shared.exception.ApiException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a user is not verified. Mapped to HTTP 403.
 */
public class UserNotVerifiedException extends ApiException {
  /**
   * Constructor for UserNotVerifiedException.
   * @param message the message to be displayed
   */
  public UserNotVerifiedException(final String message) {
    super(HttpStatus.FORBIDDEN, message);
  }
}
