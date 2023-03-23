package com.elit.outildedecision.model;

import java.util.Map;

/**
 * Represents the result of a step in a decision-making process.
 */
public class StepResultModel {

  /**
   * The score calculated for the step.
   */
  private float eScore;

  /**
   * The scores calculated for the criteria evaluated in the step, as a map from criterion names to criterion scores.
   */
  private Map<String, Float> criteriaScores;
}
