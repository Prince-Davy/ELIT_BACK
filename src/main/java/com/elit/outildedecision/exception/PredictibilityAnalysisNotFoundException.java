package com.elit.outildedecision.exception;

public class PredictibilityAnalysisNotFoundException extends Exception {
  /**
   * Constructor for PredictibilityAnalysisNotFoundException.
   * Exception thrown when a predictibility analysis is not found.
   * @param message the message to be displayed
   */
  public PredictibilityAnalysisNotFoundException(final String message) {
    super(message);
  }
}
