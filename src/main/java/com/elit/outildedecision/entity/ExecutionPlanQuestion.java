package com.elit.outildedecision.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "execution_plan_question")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExecutionPlanQuestion implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long executionPlanQuestionId;

  private static final long serialVersionUID = 1L;

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
