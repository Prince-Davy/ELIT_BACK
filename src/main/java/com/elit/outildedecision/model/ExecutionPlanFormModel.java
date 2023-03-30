package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents an execution plan form model containing information about an execution plan form.
 * It is used to store and manage data associated with execution plan forms, including answers, titles, descriptions, and question identifiers.
 * It uses Lombok annotations to generate constructors, getters, and setters.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionPlanFormModel {

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
