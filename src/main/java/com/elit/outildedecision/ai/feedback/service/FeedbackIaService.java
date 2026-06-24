package com.elit.outildedecision.ai.feedback.service;

import com.elit.outildedecision.ai.feedback.dto.FeedbackRequest;
import com.elit.outildedecision.ai.feedback.dto.FeedbackResponse;
import com.elit.outildedecision.ai.feedback.exception.FeedBackNotFoundException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;

/**
 * Business service for the AI methodology feedback feature.
 */
public interface FeedbackIaService {

  /**
   * Saves a feedback on the AI methodology recommendation of a project.
   *
   * @param request the inbound feedback payload
   * @return the success message
   * @throws ProjectNotFoundException if the project does not exist
   */
  String saveFeedback(FeedbackRequest request) throws ProjectNotFoundException;

  /**
   * Retrieves the feedback left by a user on a project.
   *
   * @param projectId the project id
   * @param userId    the user id
   * @return the feedback
   * @throws FeedBackNotFoundException if no feedback exists for this project/user
   */
  FeedbackResponse getFeedbackByProjectIdAndUserId(Long projectId, Long userId) throws FeedBackNotFoundException;

  /**
   * Deletes the feedback left by a user on a project.
   *
   * @param projectId the project id
   * @param userId    the user id
   */
  void deleteFeedbackByUserAndProject(Long projectId, Long userId);

  /**
   * Deletes all the feedbacks of a project (used when a project is removed).
   *
   * @param projectId the project id
   */
  void deleteFeedbacksByProjectId(Long projectId);
}
