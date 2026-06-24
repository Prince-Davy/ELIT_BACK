package com.elit.outildedecision.ai.feedback.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Inbound payload to create/update (upsert) a feedback on a recommended method (etape 3).
 *
 * <p>{@code methodName} is the canonical method value (e.g. "scrum", "kanban", "waterfall",
 * "vmodel", "scaled_agility").
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackMethodRequest {

  private Boolean isLiked;

  @Size(max = 255, message = "Le feedback ne peut pas dépasser 255 caractères")
  private String feedbackAnswer;

  private Long projectId;

  private Long userId;

  private String methodName;
}
