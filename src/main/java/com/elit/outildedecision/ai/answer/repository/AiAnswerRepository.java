package com.elit.outildedecision.ai.answer.repository;

import com.elit.outildedecision.ai.answer.entity.AiAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Ai answer repository interface. */
@Repository
public interface AiAnswerRepository extends JpaRepository<AiAnswer, Long> {


  /**
   * Retrieves all AI answers associated with a specific project ID.
   *
   * @param projectId the ID of the project
   * @return a list of AiAnswer entities linked to the given project
   */
  List<AiAnswer> findByProject_ProjectId(Long projectId);

  /**
   * Deletes all AI answers associated with a specific project ID.
   *
   * @param projectId the ID of the project
   */
  void deleteByProject_ProjectId(Long projectId);
}
