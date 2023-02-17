package com.elit.outildedecision.exception;

public class ResponseNotFoundException extends Exception {

  public ResponseNotFoundException() {
    super();
  }

  public ResponseNotFoundException(String message) {
    super(message);
  }

  public ResponseNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public ResponseNotFoundException(Throwable cause) {
    super(cause);
  }
}
