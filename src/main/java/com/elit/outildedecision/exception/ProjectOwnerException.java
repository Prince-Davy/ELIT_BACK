package com.elit.outildedecision.exception;

public class ProjectOwnerException extends Exception {
  public ProjectOwnerException() {
    super();
  }

  public ProjectOwnerException(String message) {
    super(message);
  }

  public ProjectOwnerException(String message, Throwable cause) {
    super(message, cause);
  }

  public ProjectOwnerException(Throwable cause) {
    super(cause);
  }

  protected ProjectOwnerException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
