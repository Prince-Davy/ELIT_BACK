package com.elit.outildedecision.collaboration.executionplan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response describing an individual vote (vote id and, for project owners, the voter id).
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionVoteUserResponse {

  /**
   * A unique identifier for the vote.
   */
  private Long id;

  /**
   * A unique identifier for the user who cast the vote.
   */
  private Long userId;
}
