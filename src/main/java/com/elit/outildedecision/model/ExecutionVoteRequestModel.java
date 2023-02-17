package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionVoteRequestModel {
  private Long methodId;
  private Long executionPlanId;
  private Long methodId2ForHybrid;
}
