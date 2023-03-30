package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * This class represents a form step model for brainstorming containing information about a step within a form, including associated questions.
 * It is used to store and manage data associated with form steps in brainstorming context, including step name, step reference, eScore, multi-criteria analysis ID, and a list of form questions.
 * It uses Lombok annotations to generate constructors, getters, and setters, as well as a builder pattern.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormStepModelForBrainstorming {

  /**
   * A unique identifier for the step.
   */
  private Long stepId;

  /**
   * The name of the step.
   */
  private String stepName;

  /**
   * A reference to another step or document related to this step.
   */
  private Long stepRef;

  /**
   * The eScore value associated with the step.
   */
  private Float eScore;

  /**
   * A unique identifier for the multi-criteria analysis associated with the step.
   */
  private Long multiCriteriaAnalysisId;

  /**
   * A list of form question models associated with the step for brainstorming purposes.
   */
  private List<FormQuestionModel> formQuestions;
}
