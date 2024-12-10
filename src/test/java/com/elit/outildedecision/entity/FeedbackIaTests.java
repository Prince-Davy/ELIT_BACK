package com.elit.outildedecision.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FeedbackIaTests {
  @Test
  void testToString(){

    FeedbackIa feedbackIa = new FeedbackIa();
    feedbackIa.setMethodology("Agile");
    final String methodologyString = "Agile";
    assertEquals(methodologyString, feedbackIa.getMethodology());
  }
}
