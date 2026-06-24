package com.elit.outildedecision.collaboration.multicriteria.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;

/** Step weight class. */
@Entity
@Table(name = "step_weight")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "weightId")
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
}
