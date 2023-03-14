package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.Brainstorming;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BrainstormingRepository extends JpaRepository<Brainstorming, Long> {
  /**
   * Find brainstorming by user, question and predictibility analysis id.
   *
   * @param userID user id
   * @param questionID question id
   * @param predictibilityAnalysisId predictibility analysis id
   * @return brainstorming matching the criteria
   */
  @Query(
      "SELECT b FROM Brainstorming b"
        + " WHERE b.user.userId = ?1 "
        + "and b.formQuestion.questionId = ?2 "
        + "and b.predictibilityAnalysis.predictibilityAnalysisId = ?3")
  Brainstorming findByUserQuestionPredictibilityId(
      Long userID, Long questionID, Long predictibilityAnalysisId);
}
