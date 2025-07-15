package com.elit.outildedecision.entity;

import com.elit.outildedecision.enumeration.AnswerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
