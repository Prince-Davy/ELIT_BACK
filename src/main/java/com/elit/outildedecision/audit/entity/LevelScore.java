package com.elit.outildedecision.audit.entity;

/**
 * Represents the evaluation level assigned to a category based on its score.
 * Levels are defined as :
 * {@code BON} – Excellent performance
 *  {@code MOYEN} – Average performance
 *  {@code MAUVAIS} – Poor performance
 */
public enum LevelScore {
  BON,
  MOYEN,
  MAUVAIS;


  /**
   * Determines the {@link LevelScore} based on a given score percentage.
   * The logic is defined as:
   *  {@code BON} if percentage ≥ 80
   *  {@code MOYEN} if percentage ≥ 50
   *  {@code MAUVAIS} otherwise
   *
   * @param percentage the score percentage to evaluate
   * @return the corresponding {@link LevelScore}
   */
  public static LevelScore fromPercentage(final double percentage) {
    if (percentage > 80) {
      return BON;
    } else if (percentage > 40) {
      return MOYEN;
    } else {
      return MAUVAIS;
    }
  }

}
