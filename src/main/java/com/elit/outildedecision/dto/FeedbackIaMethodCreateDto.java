package com.elit.outildedecision.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO d'entrée pour créer/modifier un feedback étape 3 (feedback sur une méthode).
 *
 * On utilise methodName comme String (valeur canonique attendue: "scrum", "kanban", "waterfall", "vmodel", "scaled_agility").
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackIaMethodCreateDto {

  private Boolean isLiked;

  private String feedbackAnswer;

  private Long projectId;

  private Long userId;

  private String methodName;
}
