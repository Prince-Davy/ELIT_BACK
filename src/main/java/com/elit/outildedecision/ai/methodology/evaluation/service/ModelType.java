package com.elit.outildedecision.ai.methodology.evaluation.service;

import com.elit.outildedecision.ai.methodology.evaluation.exception.UnsupportedModelTypeException;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ModelType {
  AGILE("agile"),
  TRADITIONAL("traditional");

  private final String value;

  /**
   * convert methodology type for AI API conventions.
   *
   * @param predictedMethodology the method
   * @return Model Type
   * @throws UnsupportedModelTypeException exception UnsupportedModelTypeException
   */
  public static ModelType fromPredictedMethod(final String predictedMethodology) throws UnsupportedModelTypeException {
    if (predictedMethodology == null) {
      throw new IllegalArgumentException("Predicted methodology is absent");
    }
    switch (predictedMethodology) {
      case "Agile":
        return AGILE;
      case "Traditionnelle":
        return TRADITIONAL;
      case "Hybride":
        throw new UnsupportedModelTypeException("Hybride is not supported yet");
      default:
        throw new UnsupportedModelTypeException(
          String.format("Predicted methodology '%s' is not supported", predictedMethodology)
        );
    }
  }

  /**
   * Getter.
   *
   * @return value.
   */
  @JsonValue
  public String getValue() {
    return value;
  }
}
