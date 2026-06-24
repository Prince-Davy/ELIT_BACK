package com.elit.outildedecision.audit.dto;

import com.elit.outildedecision.audit.entity.LevelScore;
import com.elit.outildedecision.shared.util.MethodType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response payload exposing a global comment associated with an evaluation level.
 * Used to provide qualitative feedback in audit scoring results.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {

  /**
   * Unique identifier of the comment.
   */
  private Long id;

  /**
   * Method type of the audit.
   */
  private MethodType method;

  /**
   * The evaluation level this comment corresponds to (e.g., BON, MOYEN, MAUVAIS).
   */
  private LevelScore level;

  /**
   * The textual content of the comment.
   */
  private String text;

  /**
   * The comment title.
   */
  private String title;
}
