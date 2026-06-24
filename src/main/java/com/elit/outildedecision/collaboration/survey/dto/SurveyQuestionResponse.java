package com.elit.outildedecision.collaboration.survey.dto;

/**
 * Outbound representation of a survey question.
 *
 * @param id the question identifier
 * @param question the text content of the question
 * @param type the type of the question
 */
public record SurveyQuestionResponse(Long id, String question, String type) {
}
