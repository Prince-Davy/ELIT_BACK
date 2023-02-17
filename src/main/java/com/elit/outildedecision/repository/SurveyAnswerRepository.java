package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.SurveyAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** interface used to communicate with database and to manage the survey_answer table data */
@Repository
public interface SurveyAnswerRepository extends JpaRepository<SurveyAnswer, Long> {}
