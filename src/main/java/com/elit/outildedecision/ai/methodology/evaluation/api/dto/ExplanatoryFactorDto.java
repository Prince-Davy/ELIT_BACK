package com.elit.outildedecision.ai.methodology.evaluation.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExplanatoryFactorDto {

  private String question;

  private String answer;

  @JsonProperty("question_score")
  private Double questionScore;
}
