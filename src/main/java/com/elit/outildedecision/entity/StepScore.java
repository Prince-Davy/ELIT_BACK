package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "step_score")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StepScore {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long Id;

  private String criteriaName;
  private float value;

  @ManyToOne()
  @JoinColumn(name = "step_id", referencedColumnName = "stepId")
  @JsonBackReference("step_score")
  private FormStep formStep;
}
