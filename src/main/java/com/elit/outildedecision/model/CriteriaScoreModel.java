package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * this class is used to store criteria score.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CriteriaScoreModel {
  private String criteriaName;
  private float criteriaScore;
}
