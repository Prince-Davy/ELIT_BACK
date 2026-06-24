package com.elit.outildedecision.collaboration.multicriteria.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Response payload aggregating the computed scores of a multi-criteria analysis: one step-score
 * entry per form step.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MultiCriteriaAnalysisScoreResponse {

  /**
   * A list of multi-criteria step score responses associated with the multi-criteria analysis.
   */
  private List<MultiCriteriaStepScoreResponse> multiCriteriaStepScores;
}
