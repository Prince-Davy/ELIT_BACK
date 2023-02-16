package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.SurveyQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * interface used to communicate with database and to manage the survey_question table data
 */
@Repository
public interface SurveyQuestionRepository extends JpaRepository<SurveyQuestion, Long> {
    SurveyQuestion findByQuestion(String question);
}
