package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents an execution plan answer model containing information about an answer related to an execution plan question.
 * It is used to store and manage data associated with the answers to execution plan questions.
 * It uses Lombok annotations to generate constructors, getters, and setters.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionPlanAnswerModel {

  /**
   * A unique identifier for the associated execution plan question.
   */
  private Long executionPlanQuestionId;

  /**
   * The text content of the answer.
   */
  private String answer;
}
