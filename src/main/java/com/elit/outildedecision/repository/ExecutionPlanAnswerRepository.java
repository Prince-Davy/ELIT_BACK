package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.ExecutionPlanAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ExecutionPlanAnswerRepository extends JpaRepository<ExecutionPlanAnswer, Long> {
  /**
   * Find execution plan answer by execution plan id and execution plan question id.
   *
   * @param executionPlanId execution plan id
   * @param executionPlanQuestionId execution plan question id
   * @return execution plan answer matching the criteria
   */
  @Query(
      "SELECT a FROM ExecutionPlanAnswer a "
        + "WHERE a.executionPlan.executionPlanId=?1 "
        + "and a.executionPlanQuestion.executionPlanQuestionId = ?2")
  ExecutionPlanAnswer findByExecutionPlanIdAndExecutionPlanQuestionId(
      Long executionPlanId, Long executionPlanQuestionId);
}
