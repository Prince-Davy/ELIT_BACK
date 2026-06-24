package com.elit.outildedecision.collaboration.survey.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SurveyQuestionTest {
  @Test
  void testToString(){

  SurveyQuestion surveyQuestion = new SurveyQuestion();
  surveyQuestion.setQuestion("Question");
    final String questionString = "Question";
    assertEquals(questionString, surveyQuestion.getQuestion());
  }
}
