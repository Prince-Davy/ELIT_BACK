package com.elit.outildedecision.audit.model;

import com.elit.outildedecision.audit.entity.LevelScore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing the result of an audit evaluation for a specific question category.
 * Contains both quantitative and qualitative feedback information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditCategoryResultDTO {

  /**
   * The category of questions this result pertains to.
   */
  private String category;

  /**
   * The percentage score achieved for this category.
   */
  private Double scorePercentageCategory;

  /**
   * The evaluation level derived from the score (e.g., BON, MOYEN, MAUVAIS).
   */
  private LevelScore level;

  /**
   * The ID of the associated comment providing qualitative feedback.
   */
  private Long categoryCommentId;

  /**
   * The textual content of the comment explaining the evaluation.
   */
  private String text;
}
