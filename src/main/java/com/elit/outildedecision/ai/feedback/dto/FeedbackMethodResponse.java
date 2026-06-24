package com.elit.outildedecision.ai.feedback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Outbound representation of a feedback on a recommended method (etape 3).
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackMethodResponse {

  private Boolean isLiked;

  private String feedbackAnswer;

  private Long projectId;

  private Long userId;

  private String methodName;
}
