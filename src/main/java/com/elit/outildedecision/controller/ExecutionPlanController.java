
package com.elit.outildedecision.controller;

import com.elit.outildedecision.exception.ExecutionPlanException;
import com.elit.outildedecision.exception.ExecutionPlanNotFoundException;
import com.elit.outildedecision.exception.UserNotAuthorizedException;
import com.elit.outildedecision.exception.VoteAlreadyExistException;
import com.elit.outildedecision.model.ExecutionPlanAnswerModel;
import com.elit.outildedecision.model.ExecutionPlanScoreResponseModel;
import com.elit.outildedecision.model.ExecutionVoteRequestModel;
import com.elit.outildedecision.service.ExecutionPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * the controller for execution plan.
 */
@RestController
@RequestMapping("/")
public class ExecutionPlanController {
  /**
   * the execution plan service.
   */
  private final ExecutionPlanService executionPlanService;

  /**
   * the constructor for execution plan controller.
   * dependency injection for execution plan service.
   * @param executionPlanService the execution plan service
   */
  public ExecutionPlanController(final ExecutionPlanService executionPlanService) {
    this.executionPlanService = executionPlanService;
  }
  /**
   * Create a new execution plan for a project.
   * @param projectId the project id
   * @return ResponseEntity<Long>
   */
  @PostMapping("/executionPlan/create/{projectId}")
  ResponseEntity<Long> createExecutionPlan(@PathVariable final Long projectId) {
    return executionPlanService.createExecutionPlan(projectId);
  }
  /**
   * Get questions and answers for of an execution plan.
   * @param executionPlanId the execution plan id
   * @return ResponseEntity<Long>
   */
  @GetMapping("/executionPlan/formConstraints/{executionPlanId}")
  // @PreAuthorize("isContributer(#projectId)")
  public ResponseEntity<Object> getQuestionsAndAnswers(@PathVariable final Long executionPlanId) {
    return executionPlanService.getQuestionsAndAnswers(executionPlanId);
  }
  /**
   * Update answers of an execution plan.
   * @param executionPlanAnswerModel the execution plan answer model
   * @param executionPlanId the execution plan id
   * @return ResponseEntity<Long>
   */
  @PutMapping(value = "/executionPlan/formConstraints/putAnswers/{executionPlanId}")
  // @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<Object> updateAnswers(
      @RequestBody final List<ExecutionPlanAnswerModel> executionPlanAnswerModel,
      @PathVariable final Long executionPlanId) {
    return executionPlanService.updateAnswers(executionPlanAnswerModel, executionPlanId);
  }
  /**
   * Get methodologies for an execution plan.
   * @param executionPlanId the execution plan id
   * @return ResponseEntity<Long>
   */
  @GetMapping("/executionPlan/methodologies/{executionPlanId}")
  public ResponseEntity<Object> getMethodologies(@PathVariable final Long executionPlanId) {
    return executionPlanService.getMethodologies(executionPlanId);
  }
  /**
   * Vote for a methodology.
   * @param executionVoteRequestModel the execution vote request model
   * @throws VoteAlreadyExistException when a vote already exist by the user
   * @return ResponseEntity<Long>
   */
  @PostMapping("/executionPlan/methodologies/vote")
  public ResponseEntity<String> postMethodologiesVote(
      @RequestBody final ExecutionVoteRequestModel executionVoteRequestModel)
    throws VoteAlreadyExistException {
    return executionPlanService.postMethodologiesVote(executionVoteRequestModel);
  }
  /**
   * Get execution plan score.
   * @param executionPlanId the execution plan id
   * @return ExecutionPlanScoreResponseModel
   * @throws UserNotAuthorizedException if the user is not authorized to vote in the specified execution plan
   * @throws ExecutionPlanNotFoundException if the execution plan is not found
   */
  @GetMapping("/executionPlan/{executionPlanId}/score")
  public ExecutionPlanScoreResponseModel getExecutionPlanScore(@PathVariable final Long executionPlanId) throws UserNotAuthorizedException, ExecutionPlanNotFoundException {
    return executionPlanService.getExecutionPlanScore(executionPlanId);
  }
  /**
   * Get execution plan score.
   * @param executionPlanId the execution plan id
   * @throws UserNotAuthorizedException when a user is not authorized
   * @throws ExecutionPlanNotFoundException when an execution plan is not found
   * @throws ExecutionPlanException when an execution plan is already finished
   * @return ExecutionPlanScoreResponseModel
   */
  @PostMapping("/executionPlan/isFinished/{executionPlanId}")
  public ResponseEntity<Boolean> putIsFinishedExecutionPlan(@PathVariable final Long executionPlanId)
    throws UserNotAuthorizedException, ExecutionPlanNotFoundException, ExecutionPlanException {
    return executionPlanService.putIsFinishedExecutionPlan(executionPlanId);
  }
}
