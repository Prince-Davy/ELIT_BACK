package com.elit.outildedecision.ai.feedback.controller;

import com.elit.outildedecision.ai.feedback.dto.FeedbackMethodRequest;
import com.elit.outildedecision.ai.feedback.dto.FeedbackMethodResponse;
import com.elit.outildedecision.ai.feedback.exception.FeedBackNotFoundException;
import com.elit.outildedecision.ai.feedback.service.FeedbackIaMethodService;
import com.elit.outildedecision.shared.common.dto.SimpleMessageDto;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for the AI method feedback feature (etape 3). Errors are delegated to the global handler.
 */
@Slf4j
@RestController
@RequestMapping("/feedbacks-method")
@Tag(name = "AI - Feedback (Methodology)", description = "Feedback IA sur les méthodologies")
public class FeedbackIaMethodController {

  private final FeedbackIaMethodService feedbackIaMethodService;

  /**
   * Constructor.
   *
   * @param feedbackIaMethodService the method feedback service
   */
  public FeedbackIaMethodController(final FeedbackIaMethodService feedbackIaMethodService) {
    this.feedbackIaMethodService = feedbackIaMethodService;
  }

  /**
   * Creates / updates (upsert) a feedback on a recommended method.
   *
   * @param request          the inbound payload
   * @param ignoredProjectId project id, used only by {@code @PreAuthorize}
   * @return a success message
   * @throws ProjectNotFoundException if the project does not exist
   */
  @PostMapping("/projectIa/method/{ignoredProjectId}")
  @PreAuthorize("isProjectOwner(#ignoredProjectId) or isContributer(#ignoredProjectId)")
  public SimpleMessageDto saveFeedback(@Valid @RequestBody final FeedbackMethodRequest request,
                                       @PathVariable final Long ignoredProjectId) throws ProjectNotFoundException {
    log.info("Received method feedback save request for projectId={}", ignoredProjectId);
    String message = feedbackIaMethodService.saveFeedback(request);
    return new SimpleMessageDto(message, HttpStatus.OK.value(), LocalDateTime.now());
  }

  /**
   * Retrieves the method feedbacks of a project.
   *
   * @param projectId the project id
   * @return the project method feedbacks
   */
  @GetMapping("/projectIa/method/{projectId}")
  @PreAuthorize("isContributer(#projectId) or isObserver(#projectId)")
  public List<FeedbackMethodResponse> getFeedbacksByProjectId(@PathVariable final Long projectId) {
    return feedbackIaMethodService.getFeedbacksByProject(projectId);
  }

  /**
   * Retrieves a method feedback by project, user and method name.
   *
   * @param projectId  the project id
   * @param userId     the user id
   * @param methodName the method name
   * @return the feedback
   * @throws FeedBackNotFoundException if no feedback is found
   */
  @GetMapping("/projectIa/method/{projectId}/{userId}/{methodName}")
  @PreAuthorize("isContributer(#projectId) or isObserver(#projectId)")
  public FeedbackMethodResponse getFeedbackByProjectIdAndUserIdAndMethodName(@PathVariable final Long projectId,
                                                                            @PathVariable final Long userId,
                                                                            @PathVariable final String methodName)
    throws FeedBackNotFoundException {
    return feedbackIaMethodService.getFeedbackByProjectIdAndUserIdAndMethodName(projectId, userId, methodName);
  }

  /**
   * Deletes a method feedback by project, user and method name.
   *
   * @param projectId  the project id
   * @param userId     the user id
   * @param methodName the method name
   * @return a success message
   */
  @DeleteMapping("/projectIa/method/{projectId}/{userId}/{methodName}")
  @PreAuthorize("isProjectOwner(#projectId)")
  public SimpleMessageDto deleteFeedbackByUserAndProjectAndMethodName(@PathVariable final Long projectId,
                                                                      @PathVariable final Long userId,
                                                                      @PathVariable final String methodName) {
    log.info("Received method feedback delete request for projectId={} userId={} method={}", projectId, userId, methodName);
    feedbackIaMethodService.deleteFeedbackByUserAndProjectAndMethodName(projectId, userId, methodName);
    return new SimpleMessageDto("Le FeedBack a bien été supprimé", HttpStatus.OK.value(), LocalDateTime.now());
  }
}
