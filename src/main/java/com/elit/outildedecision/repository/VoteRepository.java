package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    @Query("select o from Vote o where o.user.userId = ?1")
    Vote findByUserId(Long userId);

    @Query("SELECT v FROM Vote v WHERE v.predictibilityAnalysis.predictibilityAnalysisId =?1")
    List<Vote> findAllByPredictibilityAnalysisId(Long predictibilityAnalysisId);

    @Query("SELECT v FROM Vote v WHERE v.predictibilityAnalysis.predictibilityAnalysisId=?1 and v.user.userId = ?2")
    Vote findByPredictibilityAnalysisAndUserId(Long predictibilityAnalysis, Long userId);

}
