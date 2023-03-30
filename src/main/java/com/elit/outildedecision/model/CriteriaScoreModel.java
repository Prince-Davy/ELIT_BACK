package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents a criteria score model containing information about a specific evaluation criterion's score.
 * It is used to store and manage data associated with the scores of criteria used to evaluate questions or other project elements.
 * It uses Lombok annotations to generate constructors, getters, and setters.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CriteriaScoreModel {

  /**
   * The name of the evaluation criterion.
   */
  private String criteriaName;

  /**
   * The score assigned to the evaluation criterion.
   */
  private float criteriaScore;
}
