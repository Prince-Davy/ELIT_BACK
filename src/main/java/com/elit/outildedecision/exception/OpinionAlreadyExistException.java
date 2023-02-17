package com.elit.outildedecision.exception;

public class OpinionAlreadyExistException extends Exception {

  public OpinionAlreadyExistException() {
    super();
  }

  public OpinionAlreadyExistException(String message) {
    super(message);
  }

  public OpinionAlreadyExistException(String message, Throwable cause) {
    super(message, cause);
  }

  public OpinionAlreadyExistException(Throwable cause) {
    super(cause);
  }
}
