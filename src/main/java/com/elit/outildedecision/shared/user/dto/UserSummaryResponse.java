package com.elit.outildedecision.shared.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Minimal outbound representation of a user (identity only), used when a user is referenced from
 * another aggregate's response (e.g. the author of a vote) without exposing the full account.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserSummaryResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  /** Unique identifier of the user. */
  private Long userId;

  /** The first name of the user. */
  private String userFirstName;

  /** The last name of the user. */
  private String userLastName;
}
