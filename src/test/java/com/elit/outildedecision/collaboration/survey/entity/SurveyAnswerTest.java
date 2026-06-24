package com.elit.outildedecision.collaboration.survey.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SurveyAnswerTest {
  @Test
  void testToString(){

    SurveyAnswer surveyAnswer = new SurveyAnswer();
    surveyAnswer.setAnswerValue("Answer");
    final String answerValueString = "Answer";
    assertEquals(answerValueString, surveyAnswer.getAnswerValue());
  }
}
