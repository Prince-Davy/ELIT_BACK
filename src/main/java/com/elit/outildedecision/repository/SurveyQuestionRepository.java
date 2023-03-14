
package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.SurveyQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This interface represents a Spring repository for accessing and managing SurveyQuestion entities.
 * It extends the JpaRepository interface and is used for CRUD operations on SurveyQuestion objects.
 */
@Repository
public interface SurveyQuestionRepository extends JpaRepository<SurveyQuestion, Long> {
}
