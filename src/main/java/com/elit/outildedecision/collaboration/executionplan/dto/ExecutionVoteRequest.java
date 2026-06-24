package com.elit.outildedecision.collaboration.executionplan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request payload carrying a methodology vote inside an execution plan.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionVoteRequest {

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
