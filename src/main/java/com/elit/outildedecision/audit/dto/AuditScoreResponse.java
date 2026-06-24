package com.elit.outildedecision.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Response payload exposing the computed score of an audit, broken down by category
 * and by question, together with the global comment.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditScoreResponse {

  /**
   * Unique identifier of the audit score.
   */
  private Long auditScoreId;

  /**
   * Identifier of the audit associated with this score.
   */
  private Long auditId;

  /**
   * Identifier of the user who submitted or calculated the score.
   */
  private Long userId;

  /**
   * The overall score percentage calculated for the audit.
   */
  private Double globalScorePercentage;

  /**
   * The global comment matching the audit method and the global level.
   */
  private CommentResponse globalComment;

  /**
   * Score percentages grouped by question category.
   */
  @Builder.Default
  private List<AuditCategoryResultResponse> auditCategoryResults = new ArrayList<>();

  /**
   * List of individual question response scores.
   */
  private List<QuestionResponseScoreResponse> questionResponses;

  /**
   * The date and time when the score was submitted or calculated.
   */
  private Timestamp submissionDate;

}
