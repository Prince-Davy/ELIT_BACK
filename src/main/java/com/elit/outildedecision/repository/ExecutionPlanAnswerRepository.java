package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.ExecutionPlanAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ExecutionPlanAnswerRepository extends JpaRepository<ExecutionPlanAnswer, Long> {
    @Query("SELECT a FROM ExecutionPlanAnswer a WHERE a.executionPlan.executionPlanId=?1 and a.executionPlanQuestion.id = ?2")
    ExecutionPlanAnswer findByExecutionPlanIdAndExecutionPlanQuestionId(Long executionPlanId, Long executionPlanQuestionId);
}
