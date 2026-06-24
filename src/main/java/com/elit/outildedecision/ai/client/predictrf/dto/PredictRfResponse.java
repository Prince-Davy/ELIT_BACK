package com.elit.outildedecision.ai.client.predictrf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Predict rf response class. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictRfResponse {
  /**
   * list of  predictions .
   */
  private List<MethodologyEvaluationExternalDto> predictions;
}
