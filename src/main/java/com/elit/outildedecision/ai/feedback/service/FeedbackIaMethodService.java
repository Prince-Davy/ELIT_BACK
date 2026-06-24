package com.elit.outildedecision.ai.feedback.service;

import com.elit.outildedecision.ai.feedback.dto.FeedbackMethodRequest;
import com.elit.outildedecision.ai.feedback.dto.FeedbackMethodResponse;
import com.elit.outildedecision.ai.feedback.exception.FeedBackNotFoundException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;

import java.util.List;

/**
 * Business service for the AI method feedback feature (etape 3).
 */
public interface FeedbackIaMethodService {

  /**
   * Saves (or updates via upsert) a feedback on a recommended method.
   *
   * @param request the inbound payload
   * @return the success message
   * @throws ProjectNotFoundException if the project does not exist
   */
  String saveFeedback(FeedbackMethodRequest request) throws ProjectNotFoundException;

  /**
   * Retrieves the method feedbacks of a project.
   *
   * @param projectId the project id
   * @return the project method feedbacks
   */
  List<FeedbackMethodResponse> getFeedbacksByProject(Long projectId);

  /**
   * Retrieves a method feedback for a project and user. The lookup key is {@code (projectId, userId)}
   * only; {@code methodName} is part of the frozen URL contract but does not narrow the query.
   *
   * @param projectId  the project id
   * @param userId     the user id
   * @param methodName part of the URL contract, not used as a lookup key
   * @return the feedback
   * @throws FeedBackNotFoundException if no feedback is found
   */
  FeedbackMethodResponse getFeedbackByProjectIdAndUserIdAndMethodName(Long projectId, Long userId, String methodName)
    throws FeedBackNotFoundException;

  /**
   * Deletes a method feedback for a project and user. The deletion key is {@code (projectId, userId)}
   * only; {@code methodName} is part of the frozen URL contract but does not narrow the deletion.
   *
   * @param projectId  the project id
   * @param userId     the user id
   * @param methodName part of the URL contract, not used as a deletion key
   */
  void deleteFeedbackByUserAndProjectAndMethodName(Long projectId, Long userId, String methodName);
}
