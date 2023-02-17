package com.elit.outildedecision.exception;

public class UserNotVerifiedException extends Exception {
  public UserNotVerifiedException() {
    super();
  }

  public UserNotVerifiedException(String message) {
    super(message);
  }

  public UserNotVerifiedException(String message, Throwable cause) {
    super(message, cause);
  }

  public UserNotVerifiedException(Throwable cause) {
    super(cause);
  }

  protected UserNotVerifiedException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
