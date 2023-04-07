
package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.FormStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FormStepRepo extends JpaRepository<FormStep, Long> {
  /**
   * Find a FormStep Object by form id and step name.
   *
   * @param formId the form id
   * @param stepName the step name
   * @return the form step matching the criteria
   */
  @Query(
      "select s from FormStep s "
        + "where s.multiCriteriaAnalysis.multiCriteriaAnalysisId = ?1 "
        + "and s.stepName= ?2")
  FormStep findByFormIdAndStepName(Long formId, String stepName);
}
