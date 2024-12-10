package com.elit.outildedecision.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StepScoreTests {
  @Test
  void testToString(){

    StepScore stepScore = new StepScore();
    stepScore.setCriteriaName("criteriaName");
    final String criteriaString = "criteriaName";
    assertEquals(criteriaString, stepScore.getCriteriaName());
  }
}
