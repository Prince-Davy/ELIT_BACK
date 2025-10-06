package com.elit.outildedecision.audit.entity;

import com.elit.outildedecision.audit.model.ScoringType;
import com.elit.outildedecision.util.QuestionCategory;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;

/**
 * Question entity representing individual questions in an audit questionnaire.
 * Following Single Responsibility Principle - manages question data only.
 * Designed to be methodology-agnostic for maximum flexibility.
 */
@Entity
@Table(name = "question")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Question implements Serializable {

  /**
   * Unique identifier for the question.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long questionId;

  // ajouter un champs questionRef pour la référence de la question de type String

  /**
   * Reference identifier for the question.
   * Can be used to link to external standards or frameworks.
   * Optional field to allow flexibility in question management.
   */
  @Column(name = "question_ref", nullable = false)
  @NotBlank(message = "Question reference is required")
  @ToString.Include
  private String questionRef;

  /**
   * Category of the question
   * New categories can be added without modifying existing code.
   */
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  @NotNull(message = "Question category is required")
  @ToString.Include
  private QuestionCategory category;

  /**
   * Weight/importance of the question in the audit scoring.
   * Must be positive to ensure meaningful scoring.
   */
  @Column(nullable = false)
  @Positive(message = "Question weight must be positive")
  @ToString.Include
  private Integer weight;

  /**
   * Reference to the audit this question belongs to.
   */
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "audit_id", referencedColumnName = "auditId")
  @JsonBackReference("audit_questions")
  @NotNull(message = "Audit reference is required")
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private Audit audit;

  /**
   * Type of answer scoring for this question.
   * Following Open/Closed Principle - extensible for new scoring types.
   */
  @Column(name = "audit_answer_label", nullable = false)
  @NotNull(message = "Audit answer label is required")
  @ToString.Include
  private String auditAnswerLabel;

  /**
   * Value of the answer type for user interface.
   * Required to ensure all questions have a valid answer value.
   * Following Dependency Inversion Principle - depends on AnswerValue abstraction.
   */
  @Column(name = "audit_answer_value", nullable = false)
  @NotNull(message = "Audit answer value is required")
  @ToString.Include
  private Integer auditAnswerValue;

  /**
   * Current calculated score based on answer and answer type.
   */
  @Column(name = "current_score")
  @ToString.Include
  private Integer currentScore;

  /**
   * the type of the question .
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "scoring_type", nullable = false)
  @ToString.Include
  private ScoringType scoringType;

}
