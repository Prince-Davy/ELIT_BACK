package com.elit.outildedecision.ai.methodology.evaluation.repository;

import com.elit.outildedecision.ai.methodology.evaluation.entity.MethodologyEvaluation;
import com.elit.outildedecision.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MethodologyEvaluationRepository extends JpaRepository<MethodologyEvaluation, Long> {
  /**
   * findByProject.
   *
   * @param project
   * @return evaluations.
   */
  List<MethodologyEvaluation> findByProject(Project project);
}
