package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.ExecutionPlanQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ExecutionPlanQuestionRepository extends JpaRepository<ExecutionPlanQuestion, Long> {
}
