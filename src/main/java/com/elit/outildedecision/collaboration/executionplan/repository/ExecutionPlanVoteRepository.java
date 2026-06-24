package com.elit.outildedecision.collaboration.executionplan.repository;

import com.elit.outildedecision.collaboration.executionplan.entity.ExecutionPlanVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Execution plan vote repository interface. */
@Repository
public interface ExecutionPlanVoteRepository extends JpaRepository<ExecutionPlanVote, Long> {
  /**
   * Find an executionPlanVote Object by method id, user id and execution plan id.
   *
   * @param methodId the method id
   * @param userId the user id
   * @param executionPlanId the execution plan id
   * @return the execution plan vote matching the criteria
   */
  @Query(
      "SELECT v FROM ExecutionPlanVote v "
        + "WHERE v.method.id = ?1 "
        + "and v.user.userId = ?2 "
        + "and v.executionPlan.executionPlanId = ?3")
  List<ExecutionPlanVote> findVoteByMethodIdUserIdAndExecutionPlanId(
      Long methodId, Long userId, Long executionPlanId);
  /**
   * Find an executionPlanVote Object by method id and execution plan id.
   *
   * @param methodId the method id
   * @param executionPlanId the execution plan id
   * @return the execution plan vote matching the criteria
   */
  @Query(
      "SELECT v FROM ExecutionPlanVote v WHERE v.method.id = ?1 and v.executionPlan.executionPlanId = ?2")
  List<ExecutionPlanVote> findAllVotesByMethodIdAndExecutionPlanId(
      Long methodId, Long executionPlanId);
  /**
   * Find an executionPlanVote Object by user id and execution plan id.
   *
   * @param userId the user id
   * @param executionPlanId the execution plan id
   * @return the execution plan vote matching the criteria
   */
  @Query(
      "SELECT v FROM ExecutionPlanVote v WHERE v.user.userId = ?1 and v.executionPlan.executionPlanId = ?2")
  ExecutionPlanVote findVoteByUserIdAndExecutionPlanId(Long userId, Long executionPlanId);
  /**
   * Find an executionPlanVote Object by execution plan id.
   *
   * @param executionPlanId the execution plan id
   * @return the execution plan vote matching the criteria
   */
  @Query("SELECT v FROM ExecutionPlanVote v WHERE v.executionPlan.executionPlanId = ?1")
  List<ExecutionPlanVote> findAllVotesByExecutionPlanId(Long executionPlanId);
}
