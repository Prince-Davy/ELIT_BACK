package com.elit.outildedecision.ai.methodology.evaluation.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * Data Transfer Object representing the evaluation of a methodology.
 * Lombok annotations used:
 * - @Data: Generates getters, setters, equals, hashCode, and toString methods.
 * - @Builder: Enables the builder pattern for object creation.
 * - @AllArgsConstructor: Generates a constructor with all fields.
 * - @NoArgsConstructor: Generates a no-argument constructor.
 * Fields:
 * - methodology: The name of the evaluated methodology.
 * - score: The score assigned to the methodology.
 * - explanatoryFactorDto: A list of explanatory factors contributing to the evaluation.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MethodologyEvaluationDto {

  /**
   * The name of the evaluated methodology.
   */
  private String methodology;

  /**
   * The score assigned to the methodology.
   */
  private Double score;

  /**
   * A list of explanatory factors contributing to the evaluation.
   */
  @JsonProperty("explanatory_factor")
  private List<ExplanatoryFactorDto> explanatoryFactorDto;
}

