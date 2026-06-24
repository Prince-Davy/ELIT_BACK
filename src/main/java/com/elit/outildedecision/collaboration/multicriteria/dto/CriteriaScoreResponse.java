package com.elit.outildedecision.collaboration.multicriteria.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response payload holding the computed score of a single evaluation criterion.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CriteriaScoreResponse {

  /**
   * The name of the evaluation criterion.
   */
  private String criteriaName;

  /**
   * The score assigned to the evaluation criterion.
   */
  private float criteriaScore;
}
