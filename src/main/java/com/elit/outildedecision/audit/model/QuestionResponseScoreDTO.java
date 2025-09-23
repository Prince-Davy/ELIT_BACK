package com.elit.outildedecision.audit.model;

import com.elit.outildedecision.util.QuestionCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponseScoreDTO {
  /**
   * The unique identifier of the question.
   * Used to link the response to a specific question in the audit.
   */
  private Long questionId;

  /**
   * The reference code of the question.
   * Useful for external mapping or standard compliance.
   */
  private String questionRef;

  /**
   * The category to which the question belongs.
   * Helps group and analyze scores by thematic areas.
   */
  private QuestionCategory category;

  /**
   * The label of the answer provided for the question.
   * Typically used for display purposes in UI or reports.
   */
  private String answerLabel;

  /**
   * The numeric value of the answer provided.
   * Used in scoring calculations.
   */
  private Integer answerValue;

  /**
   * The calculated score for this specific question response.
   * Represents the effective score based on the answer and question weight.
   */
  private Integer score;
}

