package com.elit.outildedecision.audit.dto;

import com.elit.outildedecision.audit.entity.LevelScore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response payload describing the result of an audit evaluation for a question category.
 * Contains both quantitative and qualitative feedback information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditCategoryResultResponse {

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
