package com.elit.outildedecision.ai.feedback.dto;

import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.kernel.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Inbound payload to create a feedback on the AI methodology recommendation of a project.
 *
 * <p>Mirrors the legacy entity-shaped body (the front sends the nested {@code project}/{@code user}
 * objects carrying their ids); kept identical to preserve the API contract.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackRequest {

  private Long id;

  private Boolean isLiked;

  private String methodology;

  private String feedbackAnswer;

  private Project project;

  private User user;
}
