package com.elit.outildedecision.collaboration.executionplan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response describing a single execution plan form entry (question and current answer).
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionPlanFormResponse {

  /**
   * The text content of the answer provided in the execution plan form.
   */
  private String answer;

  /**
   * The title of the execution plan form.
   */
  private String title;

  /**
   * The description of the execution plan form.
   */
  private String description;

  /**
   * A unique identifier for the associated execution plan question.
   */
  private Long executionQuestionId;
}
