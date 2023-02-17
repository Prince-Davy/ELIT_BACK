package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoteModelResponse {
  private Long voteId;
  private String chosenPMM;
  private String justification;
  private Integer predictibilityValue;
  private Long predictibilityAnalysisId;
  private Boolean isConfirmed;
  private Long userId;
}
