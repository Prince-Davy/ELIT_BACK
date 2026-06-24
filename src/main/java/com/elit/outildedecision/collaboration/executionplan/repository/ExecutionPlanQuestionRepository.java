package com.elit.outildedecision.collaboration.executionplan.repository;

import com.elit.outildedecision.collaboration.executionplan.entity.ExecutionPlanQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Execution plan question repository interface. */
@Repository
public interface ExecutionPlanQuestionRepository
    extends JpaRepository<ExecutionPlanQuestion, Long> { }
