package com.elit.outildedecision.ai.result.dto;

/**
 * Recommendation level possible for {@link RecommendationModel}.
 */
// S115: constant names are the frozen JSON contract — the value is read back via valueOf(...) and
// flows to the front as the response "color" (RecommendationItemResponse), so renaming to
// UPPER_SNAKE_CASE would break both the parsing and the client payload.
@SuppressWarnings("java:S115")
public enum RecommendationLevel {
  Green,
  Yellow,
  Red
}
