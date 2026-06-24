package com.elit.outildedecision.collaboration.multicriteria.repository;

import com.elit.outildedecision.collaboration.multicriteria.entity.MultiCriteriaAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/** Multi criteria analysis repository interface. */
@Repository
public interface MultiCriteriaAnalysisRepository
    extends JpaRepository<MultiCriteriaAnalysis, Long> {
  /**
   * Find the latest MultiCriteriaAnalysis Iteration by project id.
   *
   * @param projectId the project id
   * @return MultiCriteriaAnalysis Object of the latest iteration
   */
  @Query(
      "select m from MultiCriteriaAnalysis m "
          + "where m.project.projectId = ?1 "
          + "and m.multiCriteriaAnalysisId = ("
          + "select max (mu.multiCriteriaAnalysisId) from MultiCriteriaAnalysis mu"
          + " where mu.project.projectId=?1)")
  MultiCriteriaAnalysis findLatestMultiCriteriaAnalysisByProjectId(Long projectId);
}
