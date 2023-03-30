package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * This class represents a multi-criteria step score model containing information about a step score within a multi-criteria analysis context.
 * It is used to store and manage data associated with multi-criteria step scores, including step name, eScore, and a list of criteria score models.
 * It uses Lombok annotations to generate constructors, getters, and setters, as well as a builder pattern.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MultiCriteriaStepScoreModel {

  /**
   * The name of the step.
   */
  private String stepName;

  /**
   * The eScore value associated with the step.
   */
  private float eScore;

  /**
   * A list of criteria score models associated with the step.
   */
  private List<CriteriaScoreModel> criteriaScores;
}
