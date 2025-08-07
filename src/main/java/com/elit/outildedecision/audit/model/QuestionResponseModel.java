package com.elit.outildedecision.audit.model;

import com.elit.outildedecision.util.QuestionCategory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model for question response data.
 * Following Single Responsibility Principle - handles only question response
 * presentation.
 * Supports methodology-agnostic question display and analytics.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionResponseModel {

  /**
   * Unique identifier for the question.
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
   * Current calculated score based on answer and answer type.
   * Automatically computed for display and analytics.
   */
  private Integer currentScore;

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
   * Helper method to set category and automatically populate display name.
   * Following DRY principle - avoid repetitive display name setting.
   *
   * @param category the question category
   */
  public void setCategory(final QuestionCategory category) {
    this.category = category;
    this.categoryDisplayName = category != null ? category.getDisplayName() : null;
  }

  /**
   * Helper method to calculate and set all score-related fields.
   * Following DRY principle - centralized score calculation logic.
   */
  public void calculateScoreMetrics() {
    if (currentScore != null && weight != null) {
      this.weightedScore = currentScore * weight;
      this.maxPossibleScore = 2 * weight;

      if (maxPossibleScore > 0) {
        this.scorePercentage = weightedScore / maxPossibleScore * 100.0;
      } else {
        this.scorePercentage = 0.0;
      }
    } else {
      this.weightedScore = 0;
      this.maxPossibleScore = weight != null ? 2 * weight : 0;
      this.scorePercentage = 0.0;
    }
  }
}
