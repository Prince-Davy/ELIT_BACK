package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.StepWeight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StepWeightRepo extends JpaRepository<StepWeight, Long> {
  @Query("select sw from StepWeight sw " +
    "where sw.criteriaName=?2 " +
    "and sw.formStep.stepId=?1")
  StepWeight findByStepIdAndCriteriaName(Long stepId, String criteriaName);
}
