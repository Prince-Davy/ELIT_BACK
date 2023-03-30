package com.elit.outildedecision.model;

import com.elit.outildedecision.entity.FormQuestion;
import com.elit.outildedecision.entity.StepWeight;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * This class represents a multi-criteria step model containing information about a step within a multi-criteria analysis context.
 * It is used to store and manage data associated with multi-criteria steps, including step ID, step name, eScore, form questions, and step weights.
 * It uses Lombok annotations to generate constructors, getters, and setters, as well as a builder pattern and a custom equals method.
 * It implements Serializable for compatibility with serialization.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(exclude = {"stepWeights"})
public class MultiCriteriaStepModel implements Serializable {

  /**
   * A unique identifier for the step.
   */
  private Long stepId;

  /**
   * The name of the step.
   */
  private String stepName;

  /**
   * The eScore value associated with the step.
   */
  private Float eScore;

  /**
   * A set of form questions associated with the step.
   */
  private Set<FormQuestion> formQuestions;

  /**
   * A set of step weights associated with the step.
   */
  private Set<StepWeight> stepWeights;

  /**
   * Returns a string representation of the multi-criteria step model.
   *
   * @return String
   */
  @Override
  public String toString() {
    return "FormStep{" + "stepId=" + stepId + ", stepName='" + stepName + '\'' + '}';
  }
}
