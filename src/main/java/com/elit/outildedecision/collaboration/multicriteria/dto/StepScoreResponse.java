package com.elit.outildedecision.collaboration.multicriteria.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Outbound representation of a per-criteria score within a multi-criteria form step.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class StepScoreResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  /** Unique identifier for the step score. */
  private Long id;

  /** The name of the scored criteria. */
  private String criteriaName;

  /** The score value. */
  private float value;
}
