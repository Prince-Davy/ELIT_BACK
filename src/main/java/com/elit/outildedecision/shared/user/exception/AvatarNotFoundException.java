package com.elit.outildedecision.shared.user.exception;

import com.elit.outildedecision.shared.exception.NotFoundException;

/** Avatar not found exception class. */
public class AvatarNotFoundException extends NotFoundException {
  /**
   * Constructor for AvatarNotFoundException.
   * Exception thrown when an attachment is not found.
   * @param message the message to be displayed
   */
  public AvatarNotFoundException(final String message) {
    super(message);
  }
}
