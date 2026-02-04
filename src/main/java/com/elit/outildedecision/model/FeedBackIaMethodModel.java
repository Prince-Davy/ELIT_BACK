package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de lecture pour les feedbacks étape 3 (méthode).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedBackIaMethodModel {

  private Boolean isLiked;

  private String feedbackAnswer;

  private Long projectId;

  private Long userId;

  /**
   * Nom canonique de la méthode (ex: "scrum", "kanban", "waterfall", "vmodel", "scaled_agility").
   *
   * <p>On ne référence pas directement {@code MethodType} ici pour éviter tout couplage/erreur de conversion
   * sur la couche REST.
   */
  private String methodName;
}
