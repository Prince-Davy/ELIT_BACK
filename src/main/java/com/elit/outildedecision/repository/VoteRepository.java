
package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * A repository interface for managing user votes on predictability analysis.
 */
@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

  /**
   * Retrieves a list of all votes associated with a particular predictibility analysis.
   *
   * @param predictibilityAnalysisId The ID of the predictibility analysis to retrieve votes for
   * @return A list of votes associated with the given predictibility analysis
   */
  @Query("SELECT v FROM Vote v "
    + "WHERE v.predictibilityAnalysis.predictibilityAnalysisId =?1")
  List<Vote> findAllByPredictibilityAnalysisId(Long predictibilityAnalysisId);

  /**
   * Retrieves a specific vote associated with a predictibility analysis and a user ID.
   *
   * @param predictibilityAnalysis The ID of the predictibility analysis the vote is associated with
   * @param userId The ID of the user who cast the vote
   * @return The vote associated with the given predictibility analysis and user, or null if not found
   */
  @Query(
    "SELECT v FROM Vote v "
      + "WHERE v.predictibilityAnalysis.predictibilityAnalysisId=?1 "
      + "and v.user.userId = ?2")
  Vote findByPredictibilityAnalysisAndUserId(Long predictibilityAnalysis, Long userId);
}

