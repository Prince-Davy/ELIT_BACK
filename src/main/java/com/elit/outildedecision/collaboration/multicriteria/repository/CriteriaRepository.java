package com.elit.outildedecision.collaboration.multicriteria.repository;

import com.elit.outildedecision.collaboration.multicriteria.entity.Criteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Criteria repository interface. */
@Repository
public interface CriteriaRepository extends JpaRepository<Criteria, Long> {
  /**
   * Find criterias by step id and criteria name.
   *
   * @param stepId step id
   * @param criteriaName criteria name
   * @return criteria matching the criteria
   */
  @Query("select c from Criteria c where c.formQuestion.formStep.stepId = ?1 and c.criteriaName=?2")
  List<Criteria> getCriteriaByStepIdAndCriteriaName(Long stepId, String criteriaName);
}
