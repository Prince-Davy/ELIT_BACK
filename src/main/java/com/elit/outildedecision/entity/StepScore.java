package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Id;
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
}
