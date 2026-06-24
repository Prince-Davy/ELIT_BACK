package com.elit.outildedecision.collaboration.survey.exception;

import com.elit.outildedecision.shared.exception.BadRequestException;

/** Question format not valid exception class. */
public class QuestionFormatNotValidException extends BadRequestException {
  /**
   * Constructor of QuestionFormatNotValidException.
   * Exception thrown when the question format is not valid.
   * @param message the message to display
   */
  public QuestionFormatNotValidException(final String message) {
    super(message);
  }
}
