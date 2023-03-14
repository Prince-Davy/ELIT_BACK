
package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.PredictibilityResults;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PredictibilityResultsRepository
    extends JpaRepository<PredictibilityResults, Long> {
  /**
   * Find all predictability results by predictability analysis id.
   * @param predictibilityAnalysisId the predictability analysis id
   * @return the list of predictability results
   */
  @Query(
      "select r from PredictibilityResults r"
        + " where r.predictibilityAnalysis.predictibilityAnalysisId = ?1")
  List<PredictibilityResults> findAllPredictibilityResultsByPreAnalysisId(
      Long predictibilityAnalysisId);
}
