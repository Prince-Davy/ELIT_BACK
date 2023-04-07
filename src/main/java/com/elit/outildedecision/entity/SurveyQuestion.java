package com.elit.outildedecision.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Table;
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
}
