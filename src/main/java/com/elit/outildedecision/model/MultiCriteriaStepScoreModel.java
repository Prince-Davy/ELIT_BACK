package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MultiCriteriaStepScoreModel {
  private String stepName;
  private float eScore;
  private List<CriteriaScoreModel> criteriaScores;
}
