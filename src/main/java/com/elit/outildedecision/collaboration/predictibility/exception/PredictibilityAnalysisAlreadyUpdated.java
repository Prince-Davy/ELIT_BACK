package com.elit.outildedecision.collaboration.predictibility.exception;

import com.elit.outildedecision.shared.exception.ConflictException;

/** Predictibility analysis already updated class. */
public class PredictibilityAnalysisAlreadyUpdated extends ConflictException {
  /**
   * Constructor for PredictibilityAnalysisAlreadyUpdated.
   * Exception thrown when a predictibility analysis is already updated.
   * @param message the message to be displayed
   */
  public PredictibilityAnalysisAlreadyUpdated(final String message) {
    super(message);
  }
}
