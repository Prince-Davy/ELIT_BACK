package com.elit.outildedecision.exception;

/**
 * Exception thrown when data in recommandation json is not matching data stored in AnswerIa.
 *
 * @implNote It *must* extend RuntimeException to enable the use of lambdas in RecommandationServiceImpl
 */
public class MalformedDataException extends RuntimeException {

  /**
   * Constructor for MalformedDataException.
   * Exception thrown when a user is not found.
   * @param message the message to be displayed
   */
  public MalformedDataException(final String message) {
    super(message);
  }
}
