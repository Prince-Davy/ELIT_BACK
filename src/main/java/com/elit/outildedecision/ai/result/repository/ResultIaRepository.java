package com.elit.outildedecision.ai.result.repository;

import com.elit.outildedecision.ai.result.entity.ResultAi;
import com.elit.outildedecision.shared.kernel.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Result ia repository interface. */
@Repository
public interface ResultIaRepository extends JpaRepository<ResultAi, Long> {

  /**
   * Find resultAI by project.
   *
   * @param project project
   * @return Optional<ResultAi>
   */
  Optional<ResultAi> findByProject(Project project);

  /**
   * Find resultAI by project id.
   * @param projectId the project id
   * @return the ResultAi if found, Optional.EMPTY otherwise
   */
  Optional<ResultAi> findByProject_ProjectId(Long projectId);
}
