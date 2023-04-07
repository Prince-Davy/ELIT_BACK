package com.elit.outildedecision.exception;

public class FileCouldNotBeSaved extends Exception {
  /**
   * Constructor for FileCouldNotBeSaved.
   * @param message String the message to be sent to the user
   */
  public FileCouldNotBeSaved(final String message) {
    super(message);
  }
}
