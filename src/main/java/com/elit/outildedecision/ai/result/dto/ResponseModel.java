package com.elit.outildedecision.ai.result.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Response model class. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseModel {

  private String id;
  private String label;

  @JsonProperty("recommandations")
  private List<RecommendationModel> recommendations;

}
