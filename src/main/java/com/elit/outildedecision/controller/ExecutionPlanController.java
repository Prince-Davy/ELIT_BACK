
package com.elit.outildedecision.controller;

import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.ExecutionPlanNotFoundException;
import com.elit.outildedecision.exception.VoteAlreadyExistException;
import com.elit.outildedecision.exception.ExecutionPlanException;
import com.elit.outildedecision.model.ExecutionPlanAnswerModel;
import com.elit.outildedecision.model.ExecutionPlanScoreResponseModel;
import com.elit.outildedecision.model.ExecutionVoteRequestModel;
import com.elit.outildedecision.service.ExecutionPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
  @PreAuthorize("isProjectOwner(#projectId) or isContributer(#projectId) or isObserver(#projectId)")
  ResponseEntity<Long> createExecutionPlan(@PathVariable final Long projectId) {
    return executionPlanService.createExecutionPlan(projectId);
  }

  /**
   * Get questions and answers for of an execution plan.
   * @param projectId the project id
   * @param executionPlanId the execution plan id
   * @throws ProjectNotFoundException when the project is not found in the database
   * @throws ExecutionPlanNotFoundException when the execution plan is not found in the database
   * @return ResponseEntity<Long>
   */
  @GetMapping("/{projectId}/executionPlan/formConstraints/{executionPlanId}")
  @PreAuthorize("hasAnyAuthority('ADMIN') or isContributer(#projectId)")
  public ResponseEntity<Object> getQuestionsAndAnswers(@PathVariable final Long projectId, @PathVariable final Long executionPlanId)
    throws ProjectNotFoundException, ExecutionPlanNotFoundException {
    return executionPlanService.getQuestionsAndAnswers(projectId, executionPlanId);
  }

  /**
   * Update answers of an execution plan.
   * @param projectId the project id
   * @param executionPlanId the execution plan id
   * @param executionPlanAnswerModel the execution plan answer model
   * @throws ProjectNotFoundException when the project is not found in the database
   * @throws ExecutionPlanNotFoundException when the execution plan is not found in the database
   * @return ResponseEntity<Long>
   */
  @PutMapping(value = "/{projectId}/executionPlan/formConstraints/putAnswers/{executionPlanId}")
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<Object> updateAnswers(
    @PathVariable final Long projectId,
    @PathVariable final Long executionPlanId,
    @RequestBody final List<ExecutionPlanAnswerModel> executionPlanAnswerModel) throws ProjectNotFoundException, ExecutionPlanNotFoundException {
    return executionPlanService.updateAnswers(projectId, executionPlanId, executionPlanAnswerModel);
  }

  /**
   * Get methodologies for an execution plan.
   * @param projectId the project id
   * @param executionPlanId the execution plan id
   * @throws ProjectNotFoundException when a vote already exist by the user
   * @return ResponseEntity<Long>
   */
  @GetMapping("/{projectId}/executionPlan/methodologies/{executionPlanId}")
  @PreAuthorize("isContributer(#projectId)")
  public ResponseEntity<Object> getMethodologies(@PathVariable final Long projectId, @PathVariable final Long executionPlanId) throws ProjectNotFoundException, ExecutionPlanNotFoundException {
    return executionPlanService.getMethodologies(projectId, executionPlanId);
  }

  /**
   * Vote for a methodology.
   * @param projectId the project id
   * @param executionVoteRequestModel the execution vote request model
   * @throws VoteAlreadyExistException when a vote already exist by the user
   * @throws ProjectNotFoundException when the project is not found in the database
   * @throws ExecutionPlanNotFoundException if the execution plan is not found
   * @return ResponseEntity<Long>
   */
  @PostMapping("/{projectId}/executionPlan/methodologies/vote")
  @PreAuthorize("isContributer(#projectId)")
  public ResponseEntity<String> postMethodologiesVote(
    @PathVariable final Long projectId,
    @RequestBody final ExecutionVoteRequestModel executionVoteRequestModel)
    throws VoteAlreadyExistException, ProjectNotFoundException, ExecutionPlanNotFoundException {
    return executionPlanService.postMethodologiesVote(projectId, executionVoteRequestModel);
  }

  /**
   * Get execution plan score.
   * @param projectId the project id
   * @param executionPlanId the execution plan id
   * @return ExecutionPlanScoreResponseModel
   * @throws ExecutionPlanNotFoundException if the execution plan is not found
   * @throws ProjectNotFoundException when the project is not found in the database
   */
  @GetMapping("/{projectId}/executionPlan/{executionPlanId}/score")
  @PreAuthorize("hasAnyAuthority('ADMIN') or isContributer(#projectId) or isObserver(#projectId)")
  public ExecutionPlanScoreResponseModel getExecutionPlanScore(@PathVariable final Long projectId, @PathVariable final Long executionPlanId)
    throws ExecutionPlanNotFoundException, ProjectNotFoundException {
    return executionPlanService.getExecutionPlanScore(projectId, executionPlanId);
  }

  /**
   * Get execution plan score.
   * @param projectId the project id
   * @param executionPlanId the execution plan id
   * @throws ExecutionPlanNotFoundException when an execution plan is not found
   * @throws ExecutionPlanException when an execution plan is already finished
   * @throws ProjectNotFoundException when the project is not found in the database
   * @return ExecutionPlanScoreResponseModel
   */
  @PostMapping("/{projectId}/executionPlan/isFinished/{executionPlanId}")
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<Boolean> putIsFinishedExecutionPlan(@PathVariable final Long projectId, @PathVariable final Long executionPlanId)
    throws ExecutionPlanNotFoundException, ExecutionPlanException, ProjectNotFoundException {
    return executionPlanService.putIsFinishedExecutionPlan(projectId, executionPlanId);
  }
}
