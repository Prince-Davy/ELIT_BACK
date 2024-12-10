package com.elit.outildedecision.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnswerIaTests {
  @Test
  void testToString(){

    AnswerIa  answerIa = new AnswerIa();
    answerIa.setAnswer("Answer");
    final String answerString = "Answer";
    assertEquals(answerString, answerIa.getAnswer());
  }

}
