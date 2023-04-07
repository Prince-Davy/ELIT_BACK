package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * This class represents a survey question model containing information about a question within a survey.
 */

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

  /**
   * The text content of the question.
   * @return the text content of the question
   */
  public String getQuestion() {
    return question;
  }
  /**
   * The text content of the question.
   * @param question the text content of the question
   */
  public void setQuestion(final String question) {
    this.question = question;
  }

  /**
   * The type of the question.
   * @return the type of the question
   */
  public String getType() {
    return type;
  }
  /**
   * The type of the question.
   * @param type the type of the question
   */
  public void setType(final String type) {
    this.type = type;
  }
}
