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

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(exclude = {"stepWeights"})
public class MultiCriteriaStepModel implements Serializable {
  private Long stepId;
  private String stepName;
  private Float eScore;
  private Set<FormQuestion> formQuestions;
  private Set<StepWeight> stepWeights;

  /**
   * @return String
   */
  @Override
  public String toString() {
    return "FormStep{" + "stepId=" + stepId + ", stepName='" + stepName + '\'' + '}';
  }
}
