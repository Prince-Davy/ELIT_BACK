package com.elit.outildedecision.collaboration.executionplan.entity;

import com.elit.outildedecision.shared.methodology.entity.Method;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
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

/** Execution plan vote class. */
@Entity
@Table(name = "execution_plan_vote")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class ExecutionPlanVote {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = false)
  @JsonBackReference("execution_plan_vote_user")
  private User user;

  @ManyToOne(optional = false)
  @JoinColumn(
      name = "execution_plan_id",
      referencedColumnName = "executionPlanId",
      nullable = false)
  @JsonBackReference("execution_plan_vote_executionPlan")
  private ExecutionPlan executionPlan;

  @ManyToOne(optional = false)
  @JoinColumn(name = "method_id", referencedColumnName = "id", nullable = false)
  @JsonBackReference("execution_plan_vote_method")
  private Method method;
}
