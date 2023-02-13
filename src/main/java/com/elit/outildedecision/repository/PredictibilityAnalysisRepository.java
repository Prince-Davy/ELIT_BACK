package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.PredictibilityAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PredictibilityAnalysisRepository extends JpaRepository<PredictibilityAnalysis, Long> {

    @Query("select p from PredictibilityAnalysis p where p.multiCriteriaAnalysis.multiCriteriaAnalysisId = ?1 and p.predictibilityAnalysisIteration = (select max(pr.predictibilityAnalysisIteration) from PredictibilityAnalysis pr where pr.multiCriteriaAnalysis.multiCriteriaAnalysisId = ?1)")
    PredictibilityAnalysis findByLastIterationAndMultiCriteriaId(Long multiCriteriaAnalysisId);

    @Query("select m from PredictibilityAnalysis m where m.predictibilityAnalysisId = ?1")
    PredictibilityAnalysis findByAnalyseId(Long analyseId);
}
