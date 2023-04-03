package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents a survey question model containing information about a question within a survey.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurveyQuestionModel {
  /**
   * The text content of the question.
   */
  private String question;
  /**
   * The type of the question.
   */
  private String type;
}
