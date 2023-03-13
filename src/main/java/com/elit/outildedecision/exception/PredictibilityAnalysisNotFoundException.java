package com.elit.outildedecision.exception;

public class PredictibilityAnalysisNotFoundException extends Exception {
  public PredictibilityAnalysisNotFoundException() {}

  public PredictibilityAnalysisNotFoundException(String message) {
    super(message);
  }

  public PredictibilityAnalysisNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public PredictibilityAnalysisNotFoundException(Throwable cause) {
    super(cause);
  }

  public PredictibilityAnalysisNotFoundException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
