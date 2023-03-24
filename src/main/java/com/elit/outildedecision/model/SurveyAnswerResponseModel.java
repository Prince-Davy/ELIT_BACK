package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a response to a survey answer by a user.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyAnswerResponseModel {

  /**
   * The ID of the user who provided the response.
   */
  private Long userId;

  /**
   * The ID of the question to which the response corresponds.
   */
  private Long questionId;

  /**
   * The user's answer to the question.
   */
  private String answer;
}
