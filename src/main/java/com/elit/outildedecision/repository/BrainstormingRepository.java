package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.Brainstorming;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrainstormingRepository extends JpaRepository<Brainstorming, Long> {

    @Query("SELECT b FROM Brainstorming b WHERE b.user.userId = ?1 and b.formQuestion.questionId = ?2 and b.predictibilityAnalysis.predictibilityAnalysisId = ?3")
    Brainstorming findByUserQuestionPredictibilityId(Long userID, Long questionID, Long predictibilityAnalysisId);

}
