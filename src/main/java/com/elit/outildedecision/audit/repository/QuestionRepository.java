package com.elit.outildedecision.audit.repository;

import com.elit.outildedecision.audit.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Question repository following Single Responsibility Principle.
 * Responsible only for question data access operations.
 * Designed with Interface Segregation Principle - specific methods for
 * different needs.
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
}
