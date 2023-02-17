package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.MultiCriteriaAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MultiCriteriaAnalysisRepository
    extends JpaRepository<MultiCriteriaAnalysis, Long> {
  @Query(
      "select m from MultiCriteriaAnalysis m "
          + "where m.project.projectId = ?1 "
          + "and m.multiCriteriaAnalysisId = ("
          + "select max (mu.multiCriteriaAnalysisId) from MultiCriteriaAnalysis mu"
          + " where mu.project.projectId=?1)")
  MultiCriteriaAnalysis findLatestMultiCriteriaAnalysisByProjectId(Long projectId);
}
