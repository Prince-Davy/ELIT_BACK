package com.elit.outildedecision.dto;

import jakarta.validation.constraints.Size;
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

  @Size(max = 255, message = "Le feedback ne peut pas dépasser 255 caractères")
  private String feedbackAnswer;

  private Long projectId;

  private Long userId;

  private String methodName;
}
