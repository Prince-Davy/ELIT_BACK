/**
 * A model class representing a user's answer to a survey question.
 */
package com.elit.outildedecision.model;

import com.elit.outildedecision.util.AnswerType;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class SurveyAnswerRequestModel {

  /** The ID of the survey question. */
  private long questionId;

  /** The ID of the user who provided the answer. */
  private long userId;

  /** The type of the answer: either a selected value or a free answer. */
  private AnswerType answerType;

  /** The answer value, if it is a free answer. */
  @NotBlank(message = "La reponse ne doit pas être vide")
  private String answerValue;
}
