package com.elit.outildedecision.audit.model;

public enum ScoringType {
  DIRECT {
    @Override
    public int computeEffectiveValue(final int answerValue) {
      return answerValue;
    }
  },
  REVERSED {
    @Override
    public int computeEffectiveValue(final int answerValue) {
      return 2 - answerValue;
    }
  };

  /**
   * compute the current score of the question with the good scoring type.
   * @param answerValue the answer value
   * @return the result
   */
  public abstract int computeEffectiveValue(int answerValue);
}
