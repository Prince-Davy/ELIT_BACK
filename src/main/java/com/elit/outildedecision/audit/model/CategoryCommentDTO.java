package com.elit.outildedecision.audit.model;

import com.elit.outildedecision.audit.entity.LevelScore;
import com.elit.outildedecision.util.MethodType;
import com.elit.outildedecision.util.QuestionCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing a comment associated with a specific question category and evaluation level.
 * Used to provide qualitative feedback in audit scoring results.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCommentDTO {

  /**
   * Unique identifier of the comment.
   */
  private Long categoryCommentId;

  /**
   * method type of audit .
   */
  private MethodType method;

  /**
   * The category of questions this comment applies to.
   */
  private QuestionCategory category;

  /**
   * The evaluation level this comment corresponds to (e.g., BON, MOYEN, MAUVAIS).
   */
  private LevelScore level;

  /**
   * The textual content of the comment.
   */
  private String text;
}
