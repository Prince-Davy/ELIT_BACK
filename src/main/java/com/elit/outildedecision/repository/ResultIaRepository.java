package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.ResultAi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
