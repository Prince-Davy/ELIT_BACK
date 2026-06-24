package com.elit.outildedecision.collaboration.multicriteria.dto;

import com.elit.outildedecision.collaboration.multicriteria.entity.FormQuestion;
import com.elit.outildedecision.collaboration.multicriteria.entity.StepWeight;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

/**
 * Request payload carrying the updated state of a multi-criteria analysis step: its identifier,
 * name, eScore and the form questions and step weights edited by the contributor.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"stepWeights"})
public class MultiCriteriaStepRequest implements Serializable {

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
}
