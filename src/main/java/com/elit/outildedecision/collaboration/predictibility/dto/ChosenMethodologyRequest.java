package com.elit.outildedecision.collaboration.predictibility.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request payload carrying the final methodology chosen for a predictibility analysis.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChosenMethodologyRequest {

  /**
   * The chosen methodology label (e.g. {@code SCRUM}).
   */
  @NotBlank(message = "Vous devez renseigner la méthodologie choisie.")
  private String chosenMethodology;
}
