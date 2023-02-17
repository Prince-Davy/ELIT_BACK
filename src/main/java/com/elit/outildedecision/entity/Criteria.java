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

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "formQuestion")
public class Criteria {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long criteriaId;

  private String criteriaName;

  private int criteriaValue;

  private float score;

  @JsonBackReference("question_criteria")
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "question_id", referencedColumnName = "questionId")
  private FormQuestion formQuestion;

  /**
   * @return String
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
