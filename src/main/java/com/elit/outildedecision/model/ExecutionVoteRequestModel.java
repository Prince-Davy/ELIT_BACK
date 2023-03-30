package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents an execution vote request model containing information about a vote request in an execution plan.
 * It is used to store and manage data associated with vote requests in execution plans, including method and execution plan identifiers.
 * It uses Lombok annotations to generate constructors, getters, and setters.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionVoteRequestModel {

  /**
   * A unique identifier for the first method being voted on.
   */
  private Long methodId;

  /**
   * A unique identifier for the execution plan in which the vote is being cast.
   */
  private Long executionPlanId;

  /**
   * A unique identifier for the second method being voted on in case of a hybrid method.
   */
  private Long methodId2ForHybrid;
}
