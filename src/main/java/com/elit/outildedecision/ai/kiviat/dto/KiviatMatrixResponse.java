package com.elit.outildedecision.ai.kiviat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * One scored answer of a Kiviat matrix question (loaded from {@code kiviat-project-matrix.json}).
 * Holds the answer id, its label and the note granted to the matching project answer.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KiviatMatrixResponse {

  private KiviatResponseId id;
  private String label;
  private Integer note;
}
