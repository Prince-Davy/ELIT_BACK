package com.elit.outildedecision.ai.kiviat.dto;

/**
 * Possible answer ids in the Kiviat project matrix. Enum names are frozen: they are matched against
 * the stored AI answer values when scoring a project.
 */
// S115: constant names are the frozen JSON contract (deserialized from kiviat-project-matrix.json
// via Enum.name()), so they cannot be renamed to UPPER_SNAKE_CASE without breaking the matrix.
@SuppressWarnings("java:S115")
public enum KiviatResponseId {
  low,
  medium,
  moderate,
  high,
  veryHigh
}
