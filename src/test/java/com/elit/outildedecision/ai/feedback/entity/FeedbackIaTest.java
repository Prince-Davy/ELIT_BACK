package com.elit.outildedecision.ai.feedback.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FeedbackIaTest {

  @Test
  void testToString() {
    FeedbackIa feedbackIa = new FeedbackIa();
    feedbackIa.setMethodology("Agile");
    final String methodologyString = "Agile";
    assertThat(feedbackIa.getMethodology()).isEqualTo(methodologyString);
  }
}
