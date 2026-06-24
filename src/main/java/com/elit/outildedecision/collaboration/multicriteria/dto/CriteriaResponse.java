package com.elit.outildedecision.collaboration.multicriteria.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Outbound representation of a criteria attached to a multi-criteria form question.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CriteriaResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  /** Unique identifier for the criteria. */
  private Long criteriaId;

  /** The name or label of the criteria. */
  private String criteriaName;

  /** The value assigned to the criteria. */
  private int criteriaValue;

  /** The score associated with this criteria. */
  private float score;
}
