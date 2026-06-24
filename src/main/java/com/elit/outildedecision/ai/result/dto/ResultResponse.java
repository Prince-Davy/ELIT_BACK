package com.elit.outildedecision.ai.result.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;

/**
 * Outbound payload exposing a stored AI prediction result for a project.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ResultResponse {

  private Long id;

  private String predictedMethod;

  private Double predictedProba;

  private Long projectId;
}
