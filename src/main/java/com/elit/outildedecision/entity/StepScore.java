package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import java.io.Serializable;

@Entity
@Table(name = "step_score")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StepScore implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String criteriaName;
  private float value;

  @ManyToOne()
  @JoinColumn(name = "step_id", referencedColumnName = "stepId")
  @JsonBackReference("step_score")
  private FormStep formStep;

  /**
   * get criteriaName.
   * @return criteriaName
   */
  public String getCriteriaName() {
    return criteriaName;
  }

  /**
   * set criteriaName.
   * @param criteriaName criteriaName
   */
  public void setCriteriaName(final String criteriaName) {
    this.criteriaName = criteriaName;
  }
}
