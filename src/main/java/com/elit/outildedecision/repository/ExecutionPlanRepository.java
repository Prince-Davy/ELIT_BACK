package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.ExecutionPlan;
import com.elit.outildedecision.entity.PredictibilityAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ExecutionPlanRepository extends JpaRepository<ExecutionPlan, Long> {

  @Query(
      "select e from ExecutionPlan e where e.predictibilityAnalysis.predictibilityAnalysisId = ?1 and e.executionIteration= (select max(ex.executionIteration) from ExecutionPlan ex)")
  ExecutionPlan findByLastIterationAndPredictibilityId(Long predictibilityAnalysis);

  @Query("select e from ExecutionPlan e where e.executionPlanId = ?1")
  ExecutionPlan findByAnalyseId(Long analyseId);
}
