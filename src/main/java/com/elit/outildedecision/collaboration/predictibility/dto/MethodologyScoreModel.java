package com.elit.outildedecision.collaboration.predictibility.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents a methodology score model containing information about a particular methodology's score within an execution plan.
 * It is used to store and manage data associated with methodology scores, including methodology name, score value, sum of vote values, and the number of votes received.
 * It uses Lombok annotations to generate constructors, getters, and setters, as well as a builder pattern.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MethodologyScoreModel {

  /**
   * The name of the methodology.
   */
  private String name;

  /**
   * The score value associated with the methodology.
   */
  private float value;

  /**
   * The sum of vote values received for the methodology.
   */
  private Integer valueVoteSum;

  /**
   * The number of votes received for the methodology.
   */
  private Integer nbVotes;
}
