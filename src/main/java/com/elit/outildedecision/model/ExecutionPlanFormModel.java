package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionPlanFormModel {
  private String answer;
  private String title;
  private String description;
  private Long executionQuestionId;
}
