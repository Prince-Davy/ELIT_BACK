package com.elit.outildedecision.ai.feedback.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Outbound representation of a feedback on the AI methodology recommendation of a project.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackResponse {

  private Boolean isLiked;

  private String methodology;

  private String feedbackAnswer;

  private Long projectId;

  private Long userId;
}
