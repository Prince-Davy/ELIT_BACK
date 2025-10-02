package com.elit.outildedecision.ai.client.predictrf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictRfResponse {
  private List<MethodologyEvaluationExternalDto> predictions;
}
