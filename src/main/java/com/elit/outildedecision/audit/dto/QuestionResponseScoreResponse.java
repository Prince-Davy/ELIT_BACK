package com.elit.outildedecision.audit.dto;

import com.elit.outildedecision.shared.util.QuestionCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response payload exposing the computed score of a single question response.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionResponseScoreResponse {

  /**
   * The unique identifier of the question.
   */
  private Long questionId;

  /**
   * The reference code of the question.
   */
  private String questionRef;

  /**
   * The category to which the question belongs.
   */
  private QuestionCategory category;

  /**
   * The label of the answer provided for the question.
   */
  private String answerLabel;

  /**
   * The numeric value of the answer provided.
   */
  private Integer answerValue;

  /**
   * The calculated score for this specific question response.
   */
  private Double score;
}
