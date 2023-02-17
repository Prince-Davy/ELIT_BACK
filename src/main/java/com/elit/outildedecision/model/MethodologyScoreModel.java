package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MethodologyScoreModel {
  private String name;
  private float value;
  private Integer valueVoteSum;
  private Integer nbVotes;
}
