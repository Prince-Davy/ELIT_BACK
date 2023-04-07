package com.elit.outildedecision.exception;

public class PredictibilityAnalysisAlreadyUpdated extends Exception {
  /**
   * Constructor for PredictibilityAnalysisAlreadyUpdated.
   * Exception thrown when a predictibility analysis is already updated.
   * @param message the message to be displayed
   */
  public PredictibilityAnalysisAlreadyUpdated(final String message) {
    super(message);
  }
}
