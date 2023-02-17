package com.elit.outildedecision.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExecutionPlanQuestion {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long executionPlanQuestionId;

  private String title;
  private String question;

  @OneToMany(mappedBy = "executionPlanQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ExecutionPlanAnswer> executionPlanAnswers;

  /**
   * @return String
   */
  @Override
  public String toString() {
    return "ExecutionPlanQuestion{" + "executionPlanQuestionId=" + executionPlanQuestionId + '}';
  }
}
