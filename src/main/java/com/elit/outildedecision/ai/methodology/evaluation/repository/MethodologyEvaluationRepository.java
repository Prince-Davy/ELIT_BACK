package com.elit.outildedecision.ai.methodology.evaluation.repository;

import com.elit.outildedecision.ai.methodology.evaluation.entity.MethodologyEvaluation;
import com.elit.outildedecision.shared.kernel.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Methodology evaluation repository interface. */
public interface MethodologyEvaluationRepository extends JpaRepository<MethodologyEvaluation, Long> {
  /**
   * findByProject.
   *
   * @param project
   * @return evaluations.
   */
  List<MethodologyEvaluation> findByProject(Project project);
}
