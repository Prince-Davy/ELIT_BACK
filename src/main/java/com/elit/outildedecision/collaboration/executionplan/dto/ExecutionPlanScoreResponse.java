package com.elit.outildedecision.collaboration.executionplan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * Response describing the scoring outcome of an execution plan (votes, chosen methods, iteration
 * and the related predictibility results).
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionPlanScoreResponse implements Serializable {

  private static final long serialVersionUID = 1L;

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
