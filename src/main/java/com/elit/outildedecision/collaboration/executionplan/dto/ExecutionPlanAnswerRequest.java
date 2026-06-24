package com.elit.outildedecision.collaboration.executionplan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request payload carrying an answer to an execution plan question.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionPlanAnswerRequest {

  /**
   * A unique identifier for the associated execution plan question.
   */
  private Long executionPlanQuestionId;

  /**
   * The text content of the answer.
   */
  private String answer;
}
