
package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.SurveyAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * This interface represents a Spring repository for accessing and managing SurveyAnswer entities.
 * It extends the JpaRepository interface and is used for CRUD operations on SurveyAnswer objects.
 */
@Repository
public interface SurveyAnswerRepository extends JpaRepository<SurveyAnswer, Long> {
}

