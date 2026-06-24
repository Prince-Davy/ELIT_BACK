package com.elit.outildedecision.audit.entity;

/**
 * Scoring strategy of an audit question, persisted on {@link Question} as a string.
 * Encapsulates how a raw answer value is turned into an effective score.
 */
public enum ScoringType {
  /** Direct. */
  DIRECT {
    /** {@inheritDoc} */
    @Override
    public int computeEffectiveValue(final int answerValue) {
      return answerValue;
    }
  },
  /** Reversed. */
  REVERSED {
    /** {@inheritDoc} */
    @Override
    public int computeEffectiveValue(final int answerValue) {
      return 2 - answerValue;
    }
  };

  /**
   * Compute the effective value of the answer for this scoring type.
   * @param answerValue the raw answer value
   * @return the effective value
   */
  public abstract int computeEffectiveValue(int answerValue);
}
