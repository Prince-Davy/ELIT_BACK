package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "execution_plan_vote")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionPlanVote {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, cascade = CascadeType.ALL)
  @JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = false)
  @JsonBackReference("execution_plan_vote_user")
  private User user;

  @ManyToOne(optional = false, cascade = CascadeType.ALL)
  @JoinColumn(
      name = "execution_plan_id",
      referencedColumnName = "executionPlanId",
      nullable = false)
  @JsonBackReference("execution_plan_vote_executionPlan")
  private ExecutionPlan executionPlan;

  @ManyToOne(optional = false, cascade = CascadeType.ALL)
  @JoinColumn(name = "method_id", referencedColumnName = "id", nullable = false)
  @JsonBackReference("execution_plan_vote_method")
  private Method method;
}
