package com.elit.outildedecision.collaboration.survey.dto;

/**
 * Outbound representation of a user's answer to a survey question.
 *
 * @param userId the identifier of the user who provided the answer
 * @param questionId the identifier of the answered question
 * @param answer the user's answer to the question
 */
public record SurveyAnswerResponse(Long userId, Long questionId, String answer) {
}
