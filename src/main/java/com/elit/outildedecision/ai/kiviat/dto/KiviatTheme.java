package com.elit.outildedecision.ai.kiviat.dto;

/**
 * Kiviat axes (themes) a matrix question can contribute to. Enum names are frozen: they are used
 * as map keys when averaging project answers per axis.
 */
// S115: constant names are the frozen JSON contract (deserialized from kiviat-project-matrix.json
// via Enum.name()), so they cannot be renamed to UPPER_SNAKE_CASE without breaking the matrix.
@SuppressWarnings("java:S115")
public enum KiviatTheme {
  FlexibiliteGestion,
  FlexibiliteSolution,
  ConfianceEtDelegation,
  MaitrisePerimetre,
  Coordination
}
