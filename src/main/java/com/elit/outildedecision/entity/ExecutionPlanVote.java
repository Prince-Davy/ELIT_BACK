package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "execution_plan_vote")
@Data
@AllArgsConstructor
@NoArgsConstructor
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

  /**
   * get user.
   * @return user
   */
  public User getUser() {
    return user;
  }

  /**
   * set user.
   * @param user user
   */
  public void setUser(final User user) {
    this.user = user;
  }
}
