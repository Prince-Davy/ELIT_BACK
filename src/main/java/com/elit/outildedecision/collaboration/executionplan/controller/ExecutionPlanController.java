package com.elit.outildedecision.collaboration.executionplan.controller;

import com.elit.outildedecision.collaboration.executionplan.dto.ExecutionPlanAnswerRequest;
import com.elit.outildedecision.collaboration.executionplan.dto.ExecutionPlanFormResponse;
import com.elit.outildedecision.collaboration.executionplan.dto.ExecutionPlanScoreResponse;
import com.elit.outildedecision.collaboration.executionplan.dto.ExecutionVoteRequest;
import com.elit.outildedecision.collaboration.executionplan.dto.ExecutionVoteResponse;
import com.elit.outildedecision.collaboration.executionplan.exception.ExecutionPlanException;
import com.elit.outildedecision.collaboration.executionplan.exception.VoteAlreadyExistException;
import com.elit.outildedecision.collaboration.executionplan.service.ExecutionPlanService;
import com.elit.outildedecision.shared.exception.ExecutionPlanNotFoundException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * REST controller for the execution plan feature.
 */
@RestController
@RequestMapping("/")
@Tag(name = "Collaboration - Execution Plan", description = "Gestion du plan d'exécution")
public class ExecutionPlanController {

  /**
   * the execution plan service.
   */
  private final ExecutionPlanService executionPlanService;

  /**
   * Constructor injecting the execution plan service.
   *
   * @param executionPlanService the execution plan service
   */
  public ExecutionPlanController(final ExecutionPlanService executionPlanService) {
    this.executionPlanService = executionPlanService;
  }

  /**
   * Create a new execution plan for a project.
   *
   * @param projectId the project id
   * @return the id of the created or in-progress execution plan
   */
  @PostMapping("/executionPlan/create/{projectId}")
  @PreAuthorize("isProjectOwner(#projectId) or isContributer(#projectId) or isObserver(#projectId)")
  public ResponseEntity<Long> createExecutionPlan(@PathVariable final Long projectId) {
    return ResponseEntity.ok(executionPlanService.createExecutionPlan(projectId));
  }

  /**
   * Get questions and answers of an execution plan.
   *
   * @param projectId the project id
   * @param executionPlanId the execution plan id
   * @return the execution plan form entries
   * @throws ProjectNotFoundException when the project does not match the execution plan
   * @throws ExecutionPlanNotFoundException when the execution plan is not found
   */
  @GetMapping("/{projectId}/executionPlan/formConstraints/{executionPlanId}")
  @PreAuthorize("hasAnyAuthority('ADMIN') or isContributer(#projectId)")
  public ResponseEntity<List<ExecutionPlanFormResponse>> getQuestionsAndAnswers(
      @PathVariable final Long projectId, @PathVariable final Long executionPlanId)
      throws ProjectNotFoundException, ExecutionPlanNotFoundException {
    return ResponseEntity.ok(executionPlanService.getQuestionsAndAnswers(projectId, executionPlanId));
  }

  /**
   * Update answers of an execution plan.
   *
   * @param projectId the project id
   * @param executionPlanId the execution plan id
   * @param answers the answers to update
   * @return the confirmation messages
   * @throws ProjectNotFoundException when the project does not match the execution plan
   * @throws ExecutionPlanNotFoundException when the execution plan is not found
   */
  @PutMapping(value = "/{projectId}/executionPlan/formConstraints/putAnswers/{executionPlanId}")
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<List<String>> updateAnswers(
      @PathVariable final Long projectId,
      @PathVariable final Long executionPlanId,
      @RequestBody final List<ExecutionPlanAnswerRequest> answers)
      throws ProjectNotFoundException, ExecutionPlanNotFoundException {
    return ResponseEntity.ok(executionPlanService.updateAnswers(projectId, executionPlanId, answers));
  }

  /**
   * Get methodologies proposed for an execution plan.
   *
   * @param projectId the project id
   * @param executionPlanId the execution plan id
   * @return the proposed methodologies with their votes
   * @throws ProjectNotFoundException when the project does not match the execution plan
   * @throws ExecutionPlanNotFoundException when the execution plan is not found
   */
  @GetMapping("/{projectId}/executionPlan/methodologies/{executionPlanId}")
  @PreAuthorize("isContributer(#projectId)")
  public ResponseEntity<List<ExecutionVoteResponse>> getMethodologies(
      @PathVariable final Long projectId, @PathVariable final Long executionPlanId)
      throws ProjectNotFoundException, ExecutionPlanNotFoundException {
    return ResponseEntity.ok(executionPlanService.getMethodologies(projectId, executionPlanId));
  }

  /**
   * Vote for a methodology.
   *
   * @param projectId the project id
   * @param executionVoteRequest the vote request
   * @return the confirmation message
   * @throws VoteAlreadyExistException when a vote already exists for the user
   * @throws ProjectNotFoundException when the project does not match the execution plan
   * @throws ExecutionPlanNotFoundException when the execution plan is not found
   */
  @PostMapping("/{projectId}/executionPlan/methodologies/vote")
  @PreAuthorize("isContributer(#projectId)")
  public ResponseEntity<String> postMethodologiesVote(
      @PathVariable final Long projectId,
      @RequestBody final ExecutionVoteRequest executionVoteRequest)
      throws VoteAlreadyExistException, ProjectNotFoundException, ExecutionPlanNotFoundException {
    return ResponseEntity.ok(executionPlanService.postMethodologiesVote(projectId, executionVoteRequest));
  }

  /**
   * Get the execution plan score.
   *
   * @param projectId the project id
   * @param executionPlanId the execution plan id
   * @return the execution plan score
   * @throws ExecutionPlanNotFoundException when the execution plan is not found
   * @throws ProjectNotFoundException when the project does not match the execution plan
   */
  @GetMapping("/{projectId}/executionPlan/{executionPlanId}/score")
  @PreAuthorize("hasAnyAuthority('ADMIN') or isContributer(#projectId) or isObserver(#projectId)")
  public ExecutionPlanScoreResponse getExecutionPlanScore(
      @PathVariable final Long projectId, @PathVariable final Long executionPlanId)
      throws ExecutionPlanNotFoundException, ProjectNotFoundException {
    return executionPlanService.getExecutionPlanScore(projectId, executionPlanId);
  }

  /**
   * Mark an execution plan as finished.
   *
   * @param projectId the project id
   * @param executionPlanId the execution plan id
   * @return {@code true} when the execution plan has been marked as finished
   * @throws ExecutionPlanNotFoundException when the execution plan is not found
   * @throws ExecutionPlanException when the execution plan is already finished
   * @throws ProjectNotFoundException when the project does not match the execution plan
   */
  @PostMapping("/{projectId}/executionPlan/isFinished/{executionPlanId}")
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<Boolean> putIsFinishedExecutionPlan(
      @PathVariable final Long projectId, @PathVariable final Long executionPlanId)
      throws ExecutionPlanNotFoundException, ExecutionPlanException, ProjectNotFoundException {
    return ResponseEntity.ok(executionPlanService.putIsFinishedExecutionPlan(projectId, executionPlanId));
  }
}
