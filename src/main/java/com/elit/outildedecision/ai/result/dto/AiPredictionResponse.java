package com.elit.outildedecision.ai.result.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Typed view of the external AI prediction service response. Unknown fields are ignored so the
 * service stays resilient to additive changes in the AI payload.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiPredictionResponse {

  @JsonProperty("predicted_method")
  private String predictedMethod;

  @JsonProperty("predicted_proba")
  private double predictedProba;
}
