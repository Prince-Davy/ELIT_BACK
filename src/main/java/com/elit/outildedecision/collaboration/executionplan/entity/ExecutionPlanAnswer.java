package com.elit.outildedecision.collaboration.executionplan.entity;

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


/** Execution plan answer class. */
@Entity
@Table(name = "execution_plan_answer")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "executionPlanAnswerId")
public class ExecutionPlanAnswer implements Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long executionPlanAnswerId;

  private String answer;

  @ManyToOne(optional = false)
  @JoinColumn(name = "execution_plan_id", referencedColumnName = "executionPlanId")
  @JsonBackReference("executionPlan_answer")
  private ExecutionPlan executionPlan;

  @ManyToOne(optional = false)
  @JoinColumn(name = "execution_plan_question_id", nullable = false)
  @JsonBackReference("execution_plan_answer")
  private ExecutionPlanQuestion executionPlanQuestion;

  /**
   * @return String
   */
  @Override
  public String toString() {
    return "executionPlanAnswerId="
        + executionPlanAnswerId
        + "executionPlanQuestion="
        + executionPlanQuestion;
  }
}
