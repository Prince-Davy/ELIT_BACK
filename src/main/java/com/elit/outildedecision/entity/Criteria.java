package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;


/**
 * Entity representing a criteria used in evaluating a form question.
 * This class is mapped to the "criteria" table in the database.
 */
@Entity
@Table(name = "criteria")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "formQuestion")
public class Criteria implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Unique identifier for the criteria.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long criteriaId;

  /**
   * The name or label of the criteria.
   */
  private String criteriaName;

  /**
   * The value assigned to the criteria.
   */
  private int criteriaValue;

  /**
   * The score associated with this criteria.
   */
  private float score;

  /**
   * The form question to which this criteria belongs.
   * Eagerly loaded and excluded from JSON serialization to avoid circular references.
   */
  @JsonBackReference("question_criteria")
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "question_id", referencedColumnName = "questionId")
  private FormQuestion formQuestion;

  /**
   * Returns a string representation of the criteria.
   *
   * @return a string containing the ID, name, and value of the criteria
   */
  @Override
  public String toString() {
    return "Criteria{"
        + "criteriaId="
        + criteriaId
        + ", criteriaName='"
        + criteriaName
        + '\''
        + ", criteriaValue="
        + criteriaValue
        + '}';
  }
}
