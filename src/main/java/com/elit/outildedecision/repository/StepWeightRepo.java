
package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.StepWeight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * This interface represents a Spring repository for accessing and managing StepWeight entities.
 * It extends the JpaRepository interface and is used for CRUD operations on StepWeight objects.
 */
@Repository
public interface StepWeightRepo extends JpaRepository<StepWeight, Long> {

  /**
   * Returns the step weight with the specified step ID and criteria name.
   *
   * @param stepId the ID of the step to retrieve the weight for
   * @param criteriaName the name of the criteria to retrieve the weight for
   * @return the step weight with the specified step ID and criteria name
   */
  @Query("select sw from StepWeight sw where sw.criteriaName = ?2 and sw.formStep.stepId = ?1")
  StepWeight findByStepIdAndCriteriaName(Long stepId, String criteriaName);
}

