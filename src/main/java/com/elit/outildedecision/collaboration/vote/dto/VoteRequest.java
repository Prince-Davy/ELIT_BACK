package com.elit.outildedecision.collaboration.vote.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a request to submit a vote.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class VoteRequest {

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
  @NotNull(message = "Vous devez renseigner l'id de l'analyse de prévisibilité.")
  @Min(1)
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
