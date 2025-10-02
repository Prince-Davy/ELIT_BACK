package com.elit.outildedecision.ai.methodology.evaluation.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MethodologyEvaluationDto {

  private String methodology;

  private Double score;

  @JsonProperty("explanatory_factor")
  private List<ExplanatoryFactorDto> explanatoryFactorDto;
}
