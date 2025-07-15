package com.elit.outildedecision.features.audit.question;

import com.elit.outildedecision.enumeration.QuestionCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Question repository following Single Responsibility Principle.
 * Responsible only for question data access operations.
 * Designed with Interface Segregation Principle - specific methods for
 * different needs.
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    /**
     * Find questions by audit ID following Liskov Substitution Principle.
     * Can substitute different audit identification methods.
     *
     * @param auditId the audit identifier
     * @return list of questions ordered by category and weight
     */
    @Query("SELECT q FROM Question q WHERE q.audit.auditId = :auditId ORDER BY q.category, q.weight DESC")
    List<Question> findByAuditAuditIdOrderByCategoryAndWeight(@Param("auditId") Long auditId);

    /**
     * Find questions by audit ID with pagination following Open/Closed Principle.
     * Extensible for different pagination strategies.
     *
     * @param auditId  the audit identifier
     * @param pageable pagination information
     * @return page of questions ordered by category and weight
     */
    @Query("SELECT q FROM Question q WHERE q.audit.auditId = :auditId ORDER BY q.category, q.weight DESC")
    Page<Question> findByAuditAuditIdOrderByCategoryAndWeight(@Param("auditId") Long auditId,
            @Param("pageable") Pageable pageable);

    /**
     * Find questions by category following Dependency Inversion Principle.
     * Depends on category abstraction, not concrete implementation.
     *
     * @param category the question category
     * @return list of questions in the specified category
     */
    @Query("SELECT q FROM Question q WHERE q.category = :category ORDER BY q.weight DESC")
    List<Question> findByCategoryOrderByWeightDesc(@Param("category") QuestionCategory category);

    /**
     * Find questions by audit and category for methodology-specific filtering.
     * Supports the agnostic design principle.
     *
     * @param auditId  the audit identifier
     * @param category the question category
     * @return list of questions matching both criteria
     */
    @Query("SELECT q FROM Question q WHERE q.audit.auditId = :auditId AND q.category = :category ORDER BY q.weight DESC")
    List<Question> findByAuditAuditIdAndCategoryOrderByWeightDesc(@Param("auditId") Long auditId,
            @Param("category") QuestionCategory category);

    /**
     * Count questions by audit following Single Responsibility Principle.
     * Dedicated method for counting operations.
     *
     * @param auditId the audit identifier
     * @return count of questions for the audit
     */
    @Query("SELECT COUNT(q) FROM Question q WHERE q.audit.auditId = :auditId")
    Long countQuestionsByAuditId(@Param("auditId") Long auditId);

    /**
     * Count questions by category for analytics purposes.
     * Supports reporting and statistics functionality.
     *
     * @param category the question category
     * @return count of questions in the category
     */
    @Query("SELECT COUNT(q) FROM Question q WHERE q.category = :category")
    Long countQuestionsByCategory(@Param("category") QuestionCategory category);

    /**
     * Calculate total weight for an audit's questions.
     * Useful for scoring and analytics.
     *
     * @param auditId the audit identifier
     * @return total weight of all questions in the audit
     */
    @Query("SELECT COALESCE(SUM(q.weight), 0) FROM Question q WHERE q.audit.auditId = :auditId")
    Integer calculateTotalWeightByAuditId(@Param("auditId") Long auditId);

    /**
     * Calculate total actual score for an audit.
     * Sums all weighted scores (currentScore * weight) for audit evaluation.
     *
     * @param auditId the audit identifier
     * @return total weighted score for the audit
     */
    @Query("SELECT COALESCE(SUM(q.currentScore * q.weight), 0) FROM Question q WHERE q.audit.auditId = :auditId")
    Integer calculateTotalScoreByAuditId(@Param("auditId") Long auditId);

    /**
     * Calculate maximum possible score for an audit.
     * Useful for percentage calculations and progress tracking.
     *
     * @param auditId the audit identifier
     * @return maximum possible weighted score for the audit
     */
    @Query("SELECT COALESCE(SUM(2 * q.weight), 0) FROM Question q WHERE q.audit.auditId = :auditId")
    Integer calculateMaxPossibleScoreByAuditId(@Param("auditId") Long auditId);


    /**
     * Find low-scoring questions in an audit.
     * Helps identify areas needing improvement.
     *
     * @param auditId  the audit identifier
     * @param maxScore maximum score threshold
     * @return list of questions with scores at or below threshold
     */
    @Query("SELECT q FROM Question q WHERE q.audit.auditId = :auditId AND q.currentScore <= :maxScore ORDER BY q.currentScore ASC, q.weight DESC")
    List<Question> findLowScoreQuestionsByAuditId(@Param("auditId") Long auditId,
            @Param("maxScore") Integer maxScore);

    /**
     * Find high-scoring questions in an audit.
     * Helps identify strong areas and best practices.
     *
     * @param auditId  the audit identifier
     * @param minScore minimum score threshold
     * @return list of questions with scores at or above threshold
     */
    @Query("SELECT q FROM Question q WHERE q.audit.auditId = :auditId AND q.currentScore >= :minScore ORDER BY q.currentScore DESC, q.weight DESC")
    List<Question> findHighScoreQuestionsByAuditId(@Param("auditId") Long auditId,
            @Param("minScore") Integer minScore);

    /**
     * Find questions with weight greater than specified value.
     * Supports filtering by importance/weight criteria.
     *
     * @param auditId   the audit identifier
     * @param minWeight minimum weight threshold
     * @return list of high-weight questions
     */
    @Query("SELECT q FROM Question q WHERE q.audit.auditId = :auditId AND q.weight >= :minWeight ORDER BY q.weight DESC")
    List<Question> findHighWeightQuestionsByAuditId(@Param("auditId") Long auditId,
            @Param("minWeight") Integer minWeight);

    /**
     * Delete all questions for a specific audit.
     * Cascade operations for audit cleanup.
     *
     * @param auditId the audit identifier
     */
    @Query("DELETE FROM Question q WHERE q.audit.auditId = :auditId")
    void deleteByAuditAuditId(@Param("auditId") Long auditId);
}
