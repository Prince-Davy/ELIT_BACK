package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * This class represents a form question model containing information about a question within a form.
 * It is used to store and manage data associated with form questions, including question text, answers, references, step identifiers, criteria, and brainstorming information.
 * It uses Lombok annotations to generate constructors, getters, and setters and implements Serializable for compatibility with serialization.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormQuestionModel implements Serializable {

  /**
   * A unique identifier for the question.
   */
  private Long questionId;

  /**
   * The text content of the question.
   */
  private String questionText;

  /**
   * The text content of the answer provided for the question.
   */
  private String answerText;

  /**
   * A reference to another question or document related to this question.
   */
  private String questionRef;

  /**
   * A unique identifier for the step associated with the question.
   */
  private Long stepId;

  /**
   * A list of criteria models associated with the question for evaluation purposes.
   */
  private List<CriteriaModel> criterias;

  /**
   * A list of brainstorming models associated with the question for generating ideas and discussions.
   */
  private List<BrainstormingModel> brainstormings;
}
