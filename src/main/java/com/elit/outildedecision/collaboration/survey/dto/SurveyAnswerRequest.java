package com.elit.outildedecision.collaboration.survey.dto;

import com.elit.outildedecision.shared.util.AnswerType;
import jakarta.validation.constraints.NotBlank;

/**
 * Inbound payload representing a user's answer to a survey question.
 *
 * @param questionId the identifier of the survey question
 * @param userId the identifier of the user who provided the answer
 * @param answerType the type of the answer: either a selected value or a free answer
 * @param answerValue the answer value
 */
public record SurveyAnswerRequest(
    long questionId,
    long userId,
    AnswerType answerType,
    @NotBlank(message = "La reponse ne doit pas être vide") String answerValue) {
}
