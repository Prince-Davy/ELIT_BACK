package com.elit.outildedecision.collaboration.multicriteria.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * Outbound representation of a multi-criteria form step with its questions, weights and scores.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FormStepResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  /** Unique identifier for the step. */
  private Long stepId;

  /** The name of the step. */
  private String stepName;

  /** The functional reference of the step. */
  private Long stepRef;

  /** The computed E-score of the step. */
  private Float eScore;

  /** The questions of the step. */
  private List<FormQuestionResponse> formQuestions;

  /** The criteria weights of the step. */
  private List<StepWeightResponse> stepWeights;

  /** The per-criteria scores of the step. */
  private List<StepScoreResponse> stepScores;
}
