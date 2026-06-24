package com.elit.outildedecision.ai.client.predictrf.dto;

import com.elit.outildedecision.ai.methodology.evaluation.service.ModelType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/** Predict rf request class. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictRfRequest {
  /**
   * the data .
   */
  private Map<String, String> data;

  /**
   * the model type .
   */
  @JsonProperty("model_type")
  private ModelType modelType;
}
