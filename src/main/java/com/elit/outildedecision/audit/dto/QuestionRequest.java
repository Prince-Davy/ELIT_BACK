package com.elit.outildedecision.audit.dto;

import com.elit.outildedecision.audit.entity.ScoringType;
import com.elit.outildedecision.shared.util.QuestionCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Request payload for creating or updating a single audit question.
 * Designed for methodology-agnostic question management.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionRequest {

  /**
   * Reference identifier for the question.
   * Can be used to link to external standards or frameworks.
   * Optional field to allow flexibility in question management.
   */
  @NotBlank(message = "La référence de la question est obligatoire")
  private String questionRef;

  /**
   * Category of the question following Open/Closed Principle.
   * Allows extension with new categories without modification.
   */
  @NotNull(message = "La catégorie de la question est obligatoire")
  private QuestionCategory category;

  /**
   * Weight/importance of the question in audit scoring.
   * Must be positive to ensure meaningful weighting system.
   */
  @NotNull(message = "Le poids de la question est obligatoire")
  @Positive(message = "Le poids de la question doit être positif")
  private Integer weight;

  /**
   * Label to the associated audit ID.
   * Useful for navigation and relationship tracking.
   */
  @NotNull(message = "Le Label de réponse est obligatoire")
  private String auditAnswerLabel;

  /**
   * Value of the answer type for user interface.
   * Required to ensure all questions have a valid answer value.
   */
  @NotNull(message = "La valeur de réponse est obligatoire")
  private Integer auditAnswerValue;

  /**
   * Type of scoring applied to this question.
   */
  @NotNull(message = "Le type de réponse est obligatoire")
  private ScoringType scoringType;

}
