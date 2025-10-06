package com.elit.outildedecision.audit.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditScoreDTO {
  /**
   * Unique identifier of the audit score.
   * Used to reference the score record in the system.
   */
  private Long auditScoreId;

  /**
   * Identifier of the audit associated with this score.
   * Links the score to a specific audit instance.
   */
  private Long auditId;

  /**
   * Identifier of the user who submitted or calculated the score.
   * Useful for tracking ownership or responsibility.
   */
  private Long userId;

  /**
   * The overall score percentage calculated for the audit.
   * Represents the global performance across all questions.
   */
  private Double globalScorePercentage;

  private CommentDTO globalComment;
  /**
   * A map of score percentages grouped by question category.
   * Allows analysis of performance per category.
   */
  @Builder.Default
  private List<AuditCategoryResultDTO> auditCategoryResults = new ArrayList<>();

  /**
   * List of individual question response scores.
   * Each item represents the score for a specific question answered during the audit.
   */
  private List<QuestionResponseScoreDTO> questionResponses;

  /**
   * The date and time when the score was submitted or calculated.
   * Useful for audit history and tracking changes over time.
   */
  private Timestamp submissionDate;

}

