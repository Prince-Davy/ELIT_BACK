
package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.ExecutionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ExecutionPlanRepository extends JpaRepository<ExecutionPlan, Long> {
  /**
   * Find an executionPlan Object of the latest iteration of predictability analysis by predictability id.
   *
   * @param predictibilityAnalysis the predictibility analysis
   * @return the execution plan matching the predictibility id
   */
  @Query(
      "select e from ExecutionPlan e where e.predictibilityAnalysis.predictibilityAnalysisId = ?1 and e.executionIteration= (select max(ex.executionIteration) from ExecutionPlan ex)")
  ExecutionPlan findByLastIterationAndPredictibilityId(Long predictibilityAnalysis);
}
