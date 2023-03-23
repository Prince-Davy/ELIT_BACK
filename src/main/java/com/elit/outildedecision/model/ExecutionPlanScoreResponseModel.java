package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * This class represents an execution plan score response model containing information about the results of an execution plan scoring process.
 * It is used to store and manage data associated with the execution plan score, such as the execution plan ID, completion status, chosen methods, vote counts, iterations, and related method and predictability results.
 * It uses Lombok annotations to generate constructors, getters, and setters.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionPlanScoreResponseModel {

  /**
   * A unique identifier for the execution plan.
   */
  private Long executionPlanId;

  /**
   * A boolean flag indicating whether the execution plan scoring process is finished.
   */
  private Boolean isFinished;

  /**
   * A list of chosen methods for the execution plan.
   */
  private List<String> chooseMethod;

  /**
   * The number of votes in the execution plan.
   */
  private Integer countVotes;

  /**
   * The number of iterations in the execution plan scoring process.
   */
  private Integer executionPlanIteration;

  /**
   * A list of all available methods for the execution plan.
   */
  private List<MethodModel> allMethods;

  /**
   * A list of predictability results associated with the execution plan.
   */
  private List<PredictibilityResultsResponseModel> predictibilityResults;
}
