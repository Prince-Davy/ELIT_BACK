package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.FormStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormStepRepo extends JpaRepository<FormStep, Long> {
    @Query("select s from FormStep s where s.multiCriteriaAnalysis.multiCriteriaAnalysisId = ?1 and s.stepName= ?2")
    FormStep findByFormIdAndStepName(Long formId, String stepName);

    @Query("select s from FormStep s where s.multiCriteriaAnalysis.multiCriteriaAnalysisId =?1")
    List<FormStep> findByFormId(Long formId);
}
