package com.elit.outildedecision.ai.kiviat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * One row of the Kiviat project matrix (loaded from {@code kiviat-project-matrix.json}): an AI answer
 * question mapped to a {@link KiviatTheme} with the per-answer notes used to score a project.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KiviatMatrixQuestion {

  private String questionNumber;
  private String questionText;
  private KiviatTheme theme;
  private List<KiviatMatrixResponse> responses;
}
