package com.elit.outildedecision.audit.dto;

import com.elit.outildedecision.audit.entity.ScoringType;
import com.elit.outildedecision.shared.util.QuestionCategory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response payload presenting a single audit question with its scoring metrics.
 * Supports methodology-agnostic question display and analytics.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionResponse {

  private Long id;

  /**
   * Unique reference identifier for the question.
   */
  private String questionRef;

  /**
   * Category of the question for classification and filtering.
   * Hidden from JSON response - only categoryDisplayName is exposed.
   */
  @JsonIgnore
  private QuestionCategory category;

  /**
   * Display name of the category for user interface.
   */
  private String categoryDisplayName;

  /**
   * Weight/importance of the question in scoring.
   */
  private Integer weight;

  /**
   * Reference to the associated audit ID.
   * Useful for navigation and relationship tracking.
   */
  @JsonIgnore
  private Long auditId;

  /**
   * Label of the answer.
   */
  private String auditAnswerLabel;

  /**
   * Value of the answer type for user interface.
   */
  private Integer auditAnswerValue;

  /**
   * Weighted score (currentScore * weight) for audit calculations.
   * Useful for understanding question impact on total audit score.
   */
  private Integer weightedScore;

  /**
   * Maximum possible score for this question (always 2 * weight).
   * Useful for percentage calculations and progress tracking.
   */
  private Integer maxPossibleScore;

  /**
   * Score percentage for this question (0-100%).
   * Helpful for visual indicators and progress reporting.
   */
  private Double scorePercentage;

  /**
   * Indicates whether this question has been answered.
   * Useful for completion tracking and validation.
   */
  private Boolean isAnswered;

  /**
   * Indicate whether Yes or No is the more valuable answer in a multiple-choice question.
   */
  private ScoringType scoringType;

}
