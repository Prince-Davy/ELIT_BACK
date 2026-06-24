package com.elit.outildedecision.shared.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Standard error payload returned to clients.
 * The `status` field contains the HTTP numeric status code (e.g. 404),
 * the `message` field contains a plain human-readable message (no status prefix).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorMessage {

  /**
   * Numeric HTTP status code.
   */
  private int status;

  /**
   * Human readable message.
   */
  private String message;

  /**
   * Constructor with HttpStatus.
   * @param status The HTTP status code
   * @param message The error message
   */
  public ErrorMessage(final HttpStatus status, final String message) {
    this.status = status != null ? status.value() : 500;
    this.message = message;
  }
}
