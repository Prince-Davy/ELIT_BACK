package com.elit.outildedecision.ai.client.predictrf.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


/**
 * Data Transfer Object representing the result of an external methodology evaluation.
 * Lombok annotations used:
 * - @Data: Generates getters, setters, equals, hashCode, and toString methods.
 * - @Builder: Enables the builder pattern for object creation.
 * - @AllArgsConstructor: Generates a constructor with all fields.
 * - @NoArgsConstructor: Generates a no-argument constructor.
 * Fields:
 * - predictedMethodology: The name of the predicted methodology.
 * - predictedScore: The score associated with the predicted methodology.
 * - featureImportante: A map of feature names to their importance scores.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MethodologyEvaluationExternalDto {

  /**
   * The name of the predicted methodology.
   */
  @JsonProperty("predicted_methodology")
  private String predictedMethodology;

  /**
   * The score associated with the predicted methodology.
   */
  @JsonProperty("predicted_score")
  private Double predictedScore;

  /**
   * A map containing feature names and their corresponding importance scores.
   */
  @JsonProperty("feature_importante")
  private Map<String, Double> featureImportante;
}
