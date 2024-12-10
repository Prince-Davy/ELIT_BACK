package com.elit.outildedecision.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResultAiTests {
  @Test
  void testToString(){

    ResultAi resultAi = new ResultAi();
    resultAi.setPredictedMethod("Classique");
    final String predectedMethodString = "Classique";
    assertEquals(predectedMethodString, resultAi.getPredictedMethod());
  }
}
