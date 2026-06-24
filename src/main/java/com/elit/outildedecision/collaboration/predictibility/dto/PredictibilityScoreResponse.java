package com.elit.outildedecision.collaboration.predictibility.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Response payload exposing a predictibility analysis score: the average eScore, the per-methodology
 * scores, the underlying form steps, the current iteration and the final chosen methodology.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PredictibilityScoreResponse {

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

  /**
   * The final chosen methodology.
   */
  private String chosenMethodology;

  /**
   * Whether the project owner must still break a methodology tie: {@code true} when several
   * methodologies share the highest cumulated votes and no final methodology has been chosen yet.
   * The front-end disables the next step until this is {@code false}.
   */
  private Boolean tieBreakRequired;
}
