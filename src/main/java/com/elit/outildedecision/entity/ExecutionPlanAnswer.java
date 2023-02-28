package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "execution_plan_answer")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExecutionPlanAnswer {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long executionPlanAnswerId;

  private String answer;

  @ManyToOne(optional = false)
  @JoinColumn(name = "execution_plan_id", referencedColumnName = "executionPlanId")
  @JsonBackReference("executionPlan_answer")
  private ExecutionPlan executionPlan;

  @ManyToOne(optional = false, cascade = CascadeType.ALL)
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
