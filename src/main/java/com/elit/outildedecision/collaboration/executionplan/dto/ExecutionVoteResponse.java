package com.elit.outildedecision.collaboration.executionplan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Response describing a method proposed for an execution plan vote, with its current votes.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionVoteResponse {

  /**
   * The name of the methodology associated with the vote.
   */
  private String methodologyName;

  /**
   * A unique identifier for the vote result.
   */
  private Long id;

  /**
   * The name of the method being voted on.
   */
  private String methodName;

  /**
   * The description of the method being voted on.
   */
  private String methodDescription;

  /**
   * The disadvantages of the method being voted on.
   */
  private String disadvantages;

  /**
   * The advantages of the method being voted on.
   */
  private String advantages;

  /**
   * A list of execution vote items containing information about individual votes.
   */
  private List<ExecutionVoteUserResponse> votes;
}
