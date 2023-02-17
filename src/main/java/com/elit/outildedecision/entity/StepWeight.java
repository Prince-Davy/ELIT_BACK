package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StepWeight {
  @ManyToOne()
  @JoinColumn(name = "stepId", referencedColumnName = "stepId")
  @JsonBackReference("step_weight")
  FormStep formStep;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long weightId;

  private String criteriaName;
  private String criteriaDescription;
  private float weightValue;
}
