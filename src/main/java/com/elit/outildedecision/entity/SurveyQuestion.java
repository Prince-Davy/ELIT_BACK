package com.elit.outildedecision.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;

/**
 * class used to build the survey question table in database manage question's data in database.
 */
@Entity
@Table(name = "survey_question")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyQuestion implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String question;
  private String type;

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
