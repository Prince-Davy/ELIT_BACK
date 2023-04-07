package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * This class represents a multi-criteria analysis score model containing information about scores of multiple criteria within an execution plan.
 * It is used to store and manage data associated with multi-criteria analysis scores, including a list of multi-criteria step score models.
 * It uses Lombok annotations to generate constructors, getters, and setters.
 */

@AllArgsConstructor
@NoArgsConstructor
public class MultiCriteriaAnalysisScoreModel {

  /**
   * A list of multi-criteria step score models associated with the multi-criteria analysis.
   */
  private List<MultiCriteriaStepScoreModel> multiCriteriaStepScores;
  /**
   * gets The total score of the multi-criteria analysis.
   * @return the total score of the multi-criteria analysis
   */
  public List<MultiCriteriaStepScoreModel> getMultiCriteriaStepScores() {
    return multiCriteriaStepScores;
  }
  /**
   * updates The total score of the multi-criteria analysis.
   * @param multiCriteriaStepScores the total score of the multi-criteria analysis
   */
  public void setMultiCriteriaStepScores(final List<MultiCriteriaStepScoreModel> multiCriteriaStepScores) {
    this.multiCriteriaStepScores = multiCriteriaStepScores;
  }
}
