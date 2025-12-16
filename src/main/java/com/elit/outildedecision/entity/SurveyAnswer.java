package com.elit.outildedecision.entity;

import com.elit.outildedecision.util.AnswerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * class used to build the survey answer table in database manage answer data in database.
 */
@Entity
@Table(name = "survey_answer")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurveyAnswer {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne() private User user;
  @ManyToOne() private SurveyQuestion question;

  @Enumerated(EnumType.STRING)
  private AnswerType type;

  private String answerValue;

  /**
   * get answer Value.
   * @return answerValue
   */
  public String getAnswerValue() {
    return answerValue;
  }

  /**
   * set answerValue.
   * @param answerValue answerValue
   */
  public void setAnswerValue(final String answerValue) {
    this.answerValue = answerValue;
  }
}
