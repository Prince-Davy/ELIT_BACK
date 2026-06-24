package com.elit.outildedecision.collaboration.multicriteria.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StepWeightTest {
  @Test
  void testToString(){

   StepWeight stepWeight = new StepWeight();
   stepWeight.setCriteriaName("name");
   final String criteriaNameString = "name";
    assertEquals(criteriaNameString, stepWeight.getCriteriaName());
  }
}
