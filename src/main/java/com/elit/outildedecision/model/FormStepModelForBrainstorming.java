package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormStepModelForBrainstorming {
  private Long stepId;
  private String stepName;
  private Long stepRef;
  private Float eScore;
  private Long multiCriteriaAnalysisId;
  private List<FormQuestionModel> formQuestions;
}
