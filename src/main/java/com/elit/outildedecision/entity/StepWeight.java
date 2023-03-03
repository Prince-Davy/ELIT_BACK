package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "step_weight")
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
