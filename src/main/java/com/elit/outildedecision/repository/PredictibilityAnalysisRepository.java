
package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.PredictibilityAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PredictibilityAnalysisRepository
    extends JpaRepository<PredictibilityAnalysis, Long> {
  /**
   * Find the predictability analysis of the last iteration of the multi criteria analysis
   * by the multi criteria analysis id.
   *
   * @param multiCriteriaAnalysisId the multi criteria analysis id
   * @return the predictability analysis
   */
  @Query(
      "select p from PredictibilityAnalysis p "
        + "where p.multiCriteriaAnalysis.multiCriteriaAnalysisId = ?1 "
        + "and p.predictibilityAnalysisIteration = ("
        + "select max(pr.predictibilityAnalysisIteration) from PredictibilityAnalysis pr "
        + "where pr.multiCriteriaAnalysis.multiCriteriaAnalysisId = ?1)")
  PredictibilityAnalysis findByLastIterationAndMultiCriteriaId(Long multiCriteriaAnalysisId);
}
