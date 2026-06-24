package com.elit.outildedecision.collaboration.brainstorming.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Lightweight outbound representation of a brainstorming entry, embedded in the multi-criteria
 * and predictibility analysis responses (no author/relations, only the entry's own data).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BrainstormingSummaryResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  /** Unique identifier for the brainstorming entry. */
  private Long brainstormingId;

  /** The text content of the brainstorming idea. */
  private String brainstormingText;

  /** Indicates whether the brainstorming idea has been marked as checked. */
  private Boolean isChecked;
}
