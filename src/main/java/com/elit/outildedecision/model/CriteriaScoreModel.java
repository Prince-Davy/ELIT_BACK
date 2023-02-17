package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CriteriaScoreModel
 * this class is used to store criteria score
 * @author Elit
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CriteriaScoreModel {
  private String criteriaName;
  private float criteriaScore;
}
