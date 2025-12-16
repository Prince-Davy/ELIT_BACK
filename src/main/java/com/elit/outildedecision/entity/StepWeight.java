package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "step_weight")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StepWeight implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long weightId;
  private String criteriaName;
  private String criteriaDescription;
  private float weightValue;
  @ManyToOne()
  @JoinColumn(name = "stepId", referencedColumnName = "stepId")
  @JsonBackReference("step_weight")
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
