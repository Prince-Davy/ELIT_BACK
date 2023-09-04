
package com.elit.outildedecision.service;

import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.ExecutionPlanNotFoundException;
import com.elit.outildedecision.exception.VoteAlreadyExistException;
import com.elit.outildedecision.exception.ExecutionPlanException;
import com.elit.outildedecision.exception.UserNotAuthorizedException;
import com.elit.outildedecision.model.ExecutionPlanAnswerModel;
import com.elit.outildedecision.model.ExecutionPlanScoreResponseModel;
import com.elit.outildedecision.model.ExecutionVoteRequestModel;
import org.springframework.http.ResponseEntity;
import java.util.List;
/**
 * This interface defines the contract for the Execution Plan Service, which provides methods to create,
 * retrieve, update, and delete execution plans.
 */
public interface ExecutionPlanService {

  /**
   * Creates a new execution plan for the specified project.
   *
   * @param projectId the ID of the project for which to create an execution plan
   * @return a ResponseEntity containing the ID of the newly created execution plan
   */
  ResponseEntity<Long> createExecutionPlan(Long projectId);

  /**
   * Retrieves the questions and answers for the specified execution plan.
   *
   * @param executionPlanId the ID of the execution plan for which to retrieve the questions and answers
   * @return a ResponseEntity containing the questions and answers for the specified execution plan
   */
  ResponseEntity<Object> getQuestionsAndAnswers(Long executionPlanId);

  /**
   * Retrieves the questions and answers for the specified execution plan.
   *
   * @param projectId the project id
   * @param executionPlanId the ID of the execution plan for which to retrieve the questions and answers
   * @throws ProjectNotFoundException when the project is not found in the database
   * @throws ExecutionPlanNotFoundException when the execution plan is not found in the database
   * @return a ResponseEntity containing the questions and answers for the specified execution plan
   */
  ResponseEntity<Object> getQuestionsAndAnswers(Long projectId, Long executionPlanId) throws ProjectNotFoundException, ExecutionPlanNotFoundException;

  /**
   * Updates the answers for the specified execution plan.
   *
   * @param executionPlanAnswerModel a list of ExecutionPlanAnswerModel objects containing the updated answers
   * @param executionPlanId the ID of the execution plan for which to update the answers
   * @return a ResponseEntity indicating the success or failure of the update operation
   */
  ResponseEntity<Object> updateAnswers(
    List<ExecutionPlanAnswerModel> executionPlanAnswerModel, Long executionPlanId);

  /**
   * Updates the answers for the specified execution plan.
   *
   * @param projectId the project id
   * @param executionPlanId the ID of the execution plan for which to update the answers
   * @param executionPlanAnswerModel a list of ExecutionPlanAnswerModel objects containing the updated answers
   * @throws ProjectNotFoundException when the project is not found in the database
   * @throws ExecutionPlanNotFoundException when the execution plan is not found in the database
   * @return a ResponseEntity indicating the success or failure of the update operation
   */
  ResponseEntity<Object> updateAnswers(
    Long projectId, Long executionPlanId, List<ExecutionPlanAnswerModel> executionPlanAnswerModel) throws ProjectNotFoundException, ExecutionPlanNotFoundException;

  /**
   * Retrieves the methodologies for the specified execution plan.
   *
   * @param executionPlanId the ID of the execution plan for which to retrieve the methodologies
   * @return a ResponseEntity containing the methodologies for the specified execution plan
   */
  ResponseEntity<Object> getMethodologies(Long executionPlanId);

  /**
   * Retrieves the methodologies for the specified execution plan.
   *
   * @param projectId the project id
   * @param executionPlanId the ID of the execution plan for which to retrieve the methodologies
   * @throws ProjectNotFoundException when the project is not found in the database
   * @return a ResponseEntity containing the methodologies for the specified execution plan
   */
  ResponseEntity<Object> getMethodologies(Long projectId, Long executionPlanId) throws ProjectNotFoundException, ExecutionPlanNotFoundException;

  /**
   * Posts a vote for a methodology in the specified execution plan.
   *
   * @param executionVoteRequestModel an ExecutionVoteRequestModel object containing the vote to be posted
   * @return a ResponseEntity indicating the success or failure of the vote operation
   * @throws UserNotAuthorizedException if the user is not authorized to vote in the specified execution plan
   */
  ResponseEntity<String> postMethodologiesVote(ExecutionVoteRequestModel executionVoteRequestModel)
          throws VoteAlreadyExistException;

  /**
   * Posts a vote for a methodology in the specified execution plan.
   *
   * @param projectId the project id
   * @param executionVoteRequestModel an ExecutionVoteRequestModel object containing the vote to be posted
   * @throws VoteAlreadyExistException when a vote already exist by the user
   * @throws ProjectNotFoundException when the project is not found in the database
   * @throws ExecutionPlanNotFoundException if the execution plan is not found
   * @return a ResponseEntity indicating the success or failure of the vote operation
   *
   */
  ResponseEntity<String> postMethodologiesVote(Long projectId, ExecutionVoteRequestModel executionVoteRequestModel)
    throws VoteAlreadyExistException, ProjectNotFoundException, ExecutionPlanNotFoundException;

  /**
   * Retrieves the score for the specified execution plan.
   *
   * @param executionPlanId the ID of the execution plan for which to retrieve the score
   * @return an ExecutionPlanScoreResponseModel object containing the score for the specified execution plan
   * @throws UserNotAuthorizedException if the user doesn't have access to the project associated with the specified execution plan
   * @throws ExecutionPlanNotFoundException if the specified execution plan does not exist
   */
  ExecutionPlanScoreResponseModel getExecutionPlanScore(Long executionPlanId) throws UserNotAuthorizedException, ExecutionPlanNotFoundException;

  /**
   * Retrieves the score for the specified execution plan.
   *
   * @param projectId the project id
   * @param executionPlanId the ID of the execution plan for which to retrieve the score
   * @return an ExecutionPlanScoreResponseModel object containing the score for the specified execution plan
   * @throws ExecutionPlanNotFoundException if the specified execution plan does not exist
   */
  ExecutionPlanScoreResponseModel getExecutionPlanScore(Long projectId, Long executionPlanId) throws  ExecutionPlanNotFoundException, ProjectNotFoundException;

  /**
   * Sets the "isFinished" flag for the specified execution plan to true.
   *
   * @param executionPlanId the ID of the execution plan for which to set the flag
   * @return a ResponseEntity indicating the success or failure of the operation
   * @throws UserNotAuthorizedException if the user is not authorized to update the specified execution plan
   * @throws ExecutionPlanNotFoundException if the specified execution plan does not exist
   * @throws ExecutionPlanException if the specified execution plan is already finished
   */
  ResponseEntity<Boolean> putIsFinishedExecutionPlan(Long executionPlanId)
    throws UserNotAuthorizedException, ExecutionPlanNotFoundException, ExecutionPlanException;

  /**
   * Sets the "isFinished" flag for the specified execution plan to true.
   *
   * @param projectId the project id
   * @param executionPlanId the ID of the execution plan for which to set the flag
   * @return a ResponseEntity indicating the success or failure of the operation
   * @throws ExecutionPlanNotFoundException if the specified execution plan does not exist
   * @throws ExecutionPlanException if the specified execution plan is already finished
   * @throws ProjectNotFoundException when the project is not found in the database
   */
  ResponseEntity<Boolean> putIsFinishedExecutionPlan(Long projectId, Long executionPlanId)
    throws ExecutionPlanNotFoundException, ExecutionPlanException, ProjectNotFoundException;
}
