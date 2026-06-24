package com.elit.outildedecision.collaboration.vote.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a response containing vote information.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class VoteResponse {

  /**
   * The ID of the vote.
   */
  private Long voteId;

  /**
   * The chosen PMM for the vote.
   */
  private String chosenPMM;

  /**
   * The justification for the vote.
   */
  private String justification;

  /**
   * The predictability value for the vote.
   */
  private Integer predictibilityValue;

  /**
   * The ID of the predictability analysis for the vote.
   */
  private Long predictibilityAnalysisId;

  /**
   * Whether the vote has been confirmed.
   */
  private Boolean isConfirmed;

  /**
   * The ID of the user who submitted the vote.
   */
  private Long userId;
}
