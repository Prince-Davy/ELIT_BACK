package com.elit.outildedecision.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerIaKey implements Serializable {

  private String question;
  private String category;
  private String answer;
  private Long project;

  /**
   * get question.
   * @return question
   */
  public String getQuestion() {
    return question;
  }

  /**
   * set question.
   * @param question question
   */
  public void setQuestion(final String question) {
    this.question = question;
  }
}
