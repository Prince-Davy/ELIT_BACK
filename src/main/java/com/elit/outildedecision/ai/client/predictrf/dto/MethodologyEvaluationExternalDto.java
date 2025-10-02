package com.elit.outildedecision.ai.client.predictrf.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MethodologyEvaluationExternalDto {

  @JsonProperty("predicted_methodology")
  private String predictedMethodology;

  @JsonProperty("predicted_score")
  private Double predictedScore;

  @JsonProperty("feature_importante")
  private Map<String, Double> featureImportante;
}
