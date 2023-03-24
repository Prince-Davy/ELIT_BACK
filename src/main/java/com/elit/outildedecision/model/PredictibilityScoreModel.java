package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a predictibility score model containing information about a predictibility score.
 * It is used to store and manage data associated with predictibility scores, including the average, a list of methodology scores, a list of form step models, and the predictibility iteration number.
 * It uses Lombok annotations to generate constructors, getters, and setters.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictibilityScoreModel {

  /**
   * The average predictibility score.
   */
  private Float average;

  /**
   * A list of methodology score models.
   */
  private ArrayList<MethodologyScoreModel> methodologies;

  /**
   * A list of form step models.
   */
  private List<FormStepModel> formSteps;

  /**
   * The predictibility iteration number.
   */
  private Integer predictibilityIteration;
}
