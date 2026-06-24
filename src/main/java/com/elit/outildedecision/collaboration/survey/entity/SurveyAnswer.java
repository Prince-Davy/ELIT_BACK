package com.elit.outildedecision.collaboration.survey.entity;

import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.util.AnswerType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@Getter
@Setter
@EqualsAndHashCode(of = "id")
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
}
