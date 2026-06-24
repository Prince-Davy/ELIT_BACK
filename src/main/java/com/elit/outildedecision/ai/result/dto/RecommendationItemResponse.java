package com.elit.outildedecision.ai.result.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Outbound recommendation item returned by the AI result mapping endpoint.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationItemResponse {

  private String color;
  private String title;
  private String description;
}
