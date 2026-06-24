package com.elit.outildedecision.collaboration.multicriteria.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Response payload holding the computed score of a single multi-criteria form step: its name,
 * eScore and the per-criteria scores.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MultiCriteriaStepScoreResponse {

  /**
   * The name of the step.
   */
  private String stepName;

  /**
   * The eScore value associated with the step.
   */
  private float eScore;

  /**
   * A list of criteria score responses associated with the step.
   */
  private List<CriteriaScoreResponse> criteriaScores;
}
