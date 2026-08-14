package com.elit.outildedecision.collaboration.executionplan.service;

import com.elit.outildedecision.collaboration.executionplan.dto.ExecutionPlanAnswerRequest;
import com.elit.outildedecision.collaboration.executionplan.dto.ExecutionPlanFormResponse;
import com.elit.outildedecision.collaboration.executionplan.dto.ExecutionPlanScoreResponse;
import com.elit.outildedecision.collaboration.executionplan.dto.ExecutionVoteRequest;
import com.elit.outildedecision.collaboration.executionplan.dto.ExecutionVoteResponse;
import com.elit.outildedecision.collaboration.executionplan.dto.ExecutionVoteUserResponse;
import com.elit.outildedecision.collaboration.executionplan.exception.ExecutionPlanException;
import com.elit.outildedecision.collaboration.executionplan.exception.VoteAlreadyExistException;
import com.elit.outildedecision.shared.exception.ExecutionPlanNotFoundException;
import com.elit.outildedecision.shared.exception.UserNotAuthorizedException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;

import java.util.List;

/**
 * Contract for the execution plan service: create plans, read their form
 * constraints and proposed
 * methodologies, register methodology votes, compute scores and mark plans as
 * finished.
 */
public interface ExecutionPlanService {

  /**
   * Creates (or returns the in-progress) execution plan for the specified
   * project.
   *
   * @param projectId the ID of the project for which to create an execution plan
   * @return the ID of the created or in-progress execution plan
   */
  Long createExecutionPlan(Long projectId);

  /**
   * Retrieves the questions and answers of the specified execution plan.
   *
   * @param projectId       the project id
   * @param executionPlanId the ID of the execution plan
   * @return the execution plan form entries
   * @throws ProjectNotFoundException       when the project does not match the
   *                                        execution plan
   * @throws ExecutionPlanNotFoundException when the execution plan is not found
   */
  List<ExecutionPlanFormResponse> getQuestionsAndAnswers(Long projectId, Long executionPlanId)
      throws ProjectNotFoundException, ExecutionPlanNotFoundException;

  /**
   * Updates the answers of the specified execution plan.
   *
   * @param projectId       the project id
   * @param executionPlanId the ID of the execution plan
   * @param answers         the updated answers
   * @return the confirmation messages of the updated answers
   * @throws ProjectNotFoundException       when the project does not match the
   *                                        execution plan
   * @throws ExecutionPlanNotFoundException when the execution plan is not found
   */
  List<String> updateAnswers(Long projectId, Long executionPlanId, List<ExecutionPlanAnswerRequest> answers)
      throws ProjectNotFoundException, ExecutionPlanNotFoundException;

  /**
   * Retrieves the methodologies proposed for the specified execution plan.
   *
   * @param projectId       the project id
   * @param executionPlanId the ID of the execution plan
   * @return the proposed methodologies with their votes
   * @throws ProjectNotFoundException       when the project does not match the
   *                                        execution plan
   * @throws ExecutionPlanNotFoundException when the execution plan is not found
   */
  List<ExecutionVoteResponse> getMethodologies(Long projectId, Long executionPlanId)
      throws ProjectNotFoundException, ExecutionPlanNotFoundException;

  /**
   * Retrieves the votes for the specified execution plan.
   *
   * @param executionPlanId the ID of the execution plan
   * @return the votes for the execution plan
   * @throws ProjectNotFoundException       when the project does not match the
   *                                        execution plan
   * @throws ExecutionPlanNotFoundException when the execution plan is not found
   */
  List<ExecutionVoteUserResponse> getVotes(Long executionPlanId)
      throws ProjectNotFoundException, ExecutionPlanNotFoundException;

  /**
   * Registers a methodology vote in the specified execution plan.
   *
   * @param projectId the project id
   * @param request   the vote to register
   * @return the confirmation message
   * @throws VoteAlreadyExistException      when the user already voted on this
   *                                        execution plan
   * @throws ProjectNotFoundException       when the project does not match the
   *                                        execution plan
   * @throws ExecutionPlanNotFoundException when the execution plan is not found
   */
  String postMethodologiesVote(Long projectId, ExecutionVoteRequest request)
      throws VoteAlreadyExistException, ProjectNotFoundException, ExecutionPlanNotFoundException;

  /**
   * Retrieves the score of the specified execution plan.
   *
   * @param projectId       the project id
   * @param executionPlanId the ID of the execution plan
   * @return the execution plan score
   * @throws ExecutionPlanNotFoundException when the execution plan is not found
   * @throws ProjectNotFoundException       when the project does not match the
   *                                        execution plan
   */
  ExecutionPlanScoreResponse getExecutionPlanScore(Long projectId, Long executionPlanId)
      throws ExecutionPlanNotFoundException, ProjectNotFoundException;

  /**
   * Retrieves the score of the specified execution plan, checking that the
   * current user belongs to
   * the related project. Used by the project data aggregation flow.
   *
   * @param executionPlanId the ID of the execution plan
   * @return the execution plan score
   * @throws UserNotAuthorizedException     when the current user is not part of
   *                                        the project
   * @throws ExecutionPlanNotFoundException when the execution plan is not found
   */
  ExecutionPlanScoreResponse getExecutionPlanScore(Long executionPlanId)
      throws UserNotAuthorizedException, ExecutionPlanNotFoundException;

  /**
   * Marks the specified execution plan as finished and completes its project.
   *
   * @param projectId       the project id
   * @param executionPlanId the ID of the execution plan
   * @return {@code true} when the execution plan has been marked as finished
   * @throws ExecutionPlanNotFoundException when the execution plan is not found
   * @throws ExecutionPlanException         when the execution plan is already
   *                                        finished
   * @throws ProjectNotFoundException       when the project does not match the
   *                                        execution plan
   */
  boolean putIsFinishedExecutionPlan(Long projectId, Long executionPlanId)
      throws ExecutionPlanNotFoundException, ExecutionPlanException, ProjectNotFoundException;
}
