package com.elit.outildedecision.features.audit.question;

import com.elit.outildedecision.enumeration.QuestionCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * Model for question creation and update requests.
 * Following Single Responsibility Principle - handles only question request
 * data.
 * Designed for methodology-agnostic question management.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionRequestModel {

  // Ajouter un champs questionRef pour la référence de la question de type String
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
   * Following Dependency Inversion Principle - depends on AnswerValue
   * abstraction.
   */
  @NotNull(message = "La valeur de réponse est obligatoire")
  private Integer auditAnswerValue;

}
