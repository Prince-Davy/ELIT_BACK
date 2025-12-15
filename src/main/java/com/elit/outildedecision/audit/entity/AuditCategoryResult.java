package com.elit.outildedecision.audit.entity;

import com.elit.outildedecision.audit.entity.comment.CategoryComment;
import com.elit.outildedecision.util.QuestionCategory;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Column;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Entity representing the result of an audit evaluation for a specific question category.
 * This class is mapped to the "audit_category_result" table in the database.
 * A unique constraint is applied on the combination of auditScore and category.
 * Lombok annotations used:
 * - @Data: Generates getters, setters, equals, hashCode, and toString methods.
 * - @Builder: Enables the builder pattern for object creation.
 * - @AllArgsConstructor: Generates a constructor with all fields.
 * - @NoArgsConstructor: Generates a no-argument constructor.
 * JPA annotations used:
 * - @Entity: Marks this class as a JPA entity.
 * - @Table: Maps the entity to the "audit_category_result" table and defines a unique constraint.
 * - @Id and @GeneratedValue: Specifies the primary key and its generation strategy.
 * - @ManyToOne and @JoinColumn: Defines relationships with other entities.
 * - @Enumerated: Specifies that enums are stored as strings.
 * - @Column: Maps fields to table columns with constraints.
 * Jackson annotations used:
 * - @JsonBackReference: Prevents circular references during serialization.
 * - @JsonIgnore: Excludes the field from JSON serialization.
 * Validation annotations used:
 * - @NotNull: Ensures the field is not null.
 * - @Min / @Max: Validates that the score is between 0 and 100.
 */
@Entity
@Table(
  name = "audit_category_result",
  uniqueConstraints = @UniqueConstraint(columnNames = {"audit_score_id", "category"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditCategoryResult {

  /**
   * Unique identifier for the audit category result.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long auditCategoryResultId;

  /**
   * The audit score to which this category result belongs.
   * This field is required and lazily loaded.
   */
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "audit_score_id", nullable = false)
  @JsonBackReference("auditScore_categoryResults")
  private AuditScore auditScore;

  /**
   * The category of the question being evaluated.
   * Stored as a string in the database.
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "category", nullable = false)
  private QuestionCategory category;

  /**
   * The score percentage for the given category.
   * Must be between 0 and 100.
   */
  @NotNull
  @Min(0)
  @Max(100)
  @Column(name = "score_Percentage_Category", nullable = false)
  private Double scorePercentageCategory;

  /**
   * The level of the score for the category.
   * Stored as a string in the database.
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "level", nullable = false)
  private LevelScore level;

  /**
   * The comment associated with the category evaluation.
   * This field is required and excluded from JSON serialization.
   */
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "category_comment_id")
  @JsonIgnore
  private CategoryComment categoryComment;
}
