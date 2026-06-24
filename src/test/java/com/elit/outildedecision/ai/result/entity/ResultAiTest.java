package com.elit.outildedecision.ai.result.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResultAiTest {
  @Test
  void testToString(){

    ResultAi resultAi = new ResultAi();
    resultAi.setPredictedMethod("Classique");
    final String predectedMethodString = "Classique";
    assertThat(resultAi.getPredictedMethod()).isEqualTo(predectedMethodString);
  }
}
