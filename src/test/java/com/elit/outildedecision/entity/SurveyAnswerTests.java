package com.elit.outildedecision.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SurveyAnswerTests {
  @Test
  void testToString(){

    SurveyAnswer surveyAnswer = new SurveyAnswer();
    surveyAnswer.setAnswerValue("Answer");
    final String answerValueString = "Answer";
    assertEquals(answerValueString, surveyAnswer.getAnswerValue());
  }
}
