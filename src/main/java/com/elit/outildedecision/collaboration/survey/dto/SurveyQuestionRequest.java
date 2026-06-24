package com.elit.outildedecision.collaboration.survey.dto;

/**
 * Inbound payload used to create or modify a survey question.
 *
 * @param question the text content of the question
 * @param type the type of the question
 */
public record SurveyQuestionRequest(String question, String type) {
}
