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

  /**
   * question.
   */
  private String question;

  /**
   * answer .
   */
  private String answer;

  /**
   * the score of the question .
   */
  @JsonProperty("question_score")
  private Double questionScore;
}
