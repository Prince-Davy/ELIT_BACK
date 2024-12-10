package com.elit.outildedecision.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnswerIaKeyTests {

  @Test
  void testToString() {
    AnswerIaKey answerIaKey = new AnswerIaKey();
    answerIaKey.setQuestion("Question");
    final String questionString = "Question";
    assertEquals(questionString, answerIaKey.getQuestion());
  }
}
