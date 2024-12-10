package com.elit.outildedecision.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PredectibilityResultsTests {
  @Test
  void testToString(){

   PredictibilityResults predictibilityResults = new PredictibilityResults();
    predictibilityResults.setName("namePredict");
    final String nameString = "namePredict";
    assertEquals(nameString, predictibilityResults.getName());
  }
}
