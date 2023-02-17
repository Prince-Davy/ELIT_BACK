package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PredictibilityAnalyseResponseModel implements Serializable {
  private Long predictibilityAnalysisId;
  private Boolean isFinished;
  private Integer predictibilityAnalysisIteration;
  private ExecutionPlanScoreResponseModel executionPlan;
}
