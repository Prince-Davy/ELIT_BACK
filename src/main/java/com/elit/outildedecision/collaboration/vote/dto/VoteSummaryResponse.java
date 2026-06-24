package com.elit.outildedecision.collaboration.vote.dto;

import com.elit.outildedecision.shared.user.dto.UserSummaryResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * Outbound representation of a vote, embedded in the predictibility analysis response. Exposes the
 * voter as a minimal {@link UserSummaryResponse} instead of the full User entity.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class VoteSummaryResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  /** Unique identifier of the vote. */
  private Long voteId;

  /** The creation date of the vote. */
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Paris")
  private Date createdAt;

  /** The predictibility value of the vote. */
  private Integer predictibilityValue;

  /** The project-management methodology chosen in the vote. */
  private String chosenPMM;

  /** The justification of the vote. */
  private String justification;

  /** Whether the vote has been confirmed. */
  private Boolean isConfirmed;

  /** The author of the vote. */
  private UserSummaryResponse user;
}
