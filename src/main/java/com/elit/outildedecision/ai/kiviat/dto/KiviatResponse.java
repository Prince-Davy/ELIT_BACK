package com.elit.outildedecision.ai.kiviat.dto;

import com.elit.outildedecision.shared.util.MethodologyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Wire DTO carrying the five Kiviat radar axes used by the front to draw the diagram.
 *
 * <p>Field names are frozen: Jackson derives the JSON keys ({@code flexibiliteGestion},
 * {@code flexibiliteSolution}, {@code confianceEtDelegation}, {@code maitrisePerimetre},
 * {@code coordination}) from these getters and the front reads them by name.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KiviatResponse {

  private MethodologyType id;
  private double flexibiliteGestion;
  private double flexibiliteSolution;
  private double confianceEtDelegation;
  private double maitrisePerimetre;
  private double coordination;
}
