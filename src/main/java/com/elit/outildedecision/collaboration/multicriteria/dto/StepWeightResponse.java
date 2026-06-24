package com.elit.outildedecision.collaboration.multicriteria.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Outbound representation of the weight of a criteria within a multi-criteria form step.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class StepWeightResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  /** Unique identifier for the step weight. */
  private Long weightId;

  /** The name of the weighted criteria. */
  private String criteriaName;

  /** The description of the weighted criteria. */
  private String criteriaDescription;

  /** The weight value of the criteria. */
  private float weightValue;
}
