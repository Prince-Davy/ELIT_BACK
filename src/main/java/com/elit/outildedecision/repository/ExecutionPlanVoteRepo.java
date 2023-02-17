package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.ExecutionPlanVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExecutionPlanVoteRepo extends JpaRepository<ExecutionPlanVote, Long> {

  @Query(
      "SELECT v FROM ExecutionPlanVote v " +
        "WHERE v.method.id = ?1 " +
        "and v.user.userId = ?2 " +
        "and v.executionPlan.executionPlanId = ?3")
  List<ExecutionPlanVote> findVoteByMethodIdUserIdAndExecutionPlanId(
      Long methodId, Long userId, Long executionPlanId);

  @Query(
      "SELECT v FROM ExecutionPlanVote v WHERE v.method.id = ?1 and v.executionPlan.executionPlanId = ?2")
  List<ExecutionPlanVote> findAllVotesByMethodIdAndExecutionPlanId(
      Long methodId, Long executionPlanId);

  @Query(
      "SELECT v FROM ExecutionPlanVote v WHERE v.user.userId = ?1 and v.executionPlan.executionPlanId = ?2")
  ExecutionPlanVote findVoteByUserIdAndExecutionPlanId(Long userId, Long executionPlanId);

  @Query("SELECT v FROM ExecutionPlanVote v WHERE v.executionPlan.executionPlanId = ?1")
  List<ExecutionPlanVote> findAllVotesByExecutionPlanId(Long executionPlanId);
}
