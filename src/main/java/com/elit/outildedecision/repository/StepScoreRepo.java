package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.Criteria;
import com.elit.outildedecision.entity.StepScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StepScoreRepo extends JpaRepository<StepScore, Long> {
    @Query("select c from Criteria c where c.formQuestion.formStep.stepId = ?1 and c.criteriaName=?2")
    List<Criteria> getScoreByMulticriteriaAnalys(Long multicriteriaAnalysID);
}
