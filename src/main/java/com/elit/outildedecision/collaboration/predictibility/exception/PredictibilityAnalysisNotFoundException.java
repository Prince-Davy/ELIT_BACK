package com.elit.outildedecision.collaboration.predictibility.exception;

import com.elit.outildedecision.shared.exception.NotFoundException;

/** Predictibility analysis not found exception class. */
public class PredictibilityAnalysisNotFoundException extends NotFoundException {
  /**
   * Constructor for PredictibilityAnalysisNotFoundException.
   * Exception thrown when a predictibility analysis is not found.
   * @param message the message to be displayed
   */
  public PredictibilityAnalysisNotFoundException(final String message) {
    super(message);
  }
}
