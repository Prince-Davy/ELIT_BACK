package com.elit.outildedecision.ai.feedback.controller;

import com.elit.outildedecision.ai.feedback.dto.FeedbackRequest;
import com.elit.outildedecision.ai.feedback.dto.FeedbackResponse;
import com.elit.outildedecision.ai.feedback.exception.FeedBackNotFoundException;
import com.elit.outildedecision.ai.feedback.service.FeedbackIaService;
import com.elit.outildedecision.shared.common.dto.SimpleMessageDto;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import io.swagger.v3.oas.annotations.tags.Tag;
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

/**
 * Controller for the AI methodology feedback feature. Errors are delegated to the global handler.
 */
@Slf4j
@RestController
@RequestMapping("/feedbacks")
@Tag(name = "AI - Feedback", description = "Feedback IA sur les recommandations")
public class FeedbackIaController {

  private final FeedbackIaService feedbackIaService;

  /**
   * Constructor.
   *
   * @param feedbackIaService the feedback service
   */
  public FeedbackIaController(final FeedbackIaService feedbackIaService) {
    this.feedbackIaService = feedbackIaService;
  }

  /**
   * Creates and persists a feedback for the AI recommendation of a project.
   *
   * @param request          the user feedback
   * @param ignoredProjectId project id, used only by {@code @PreAuthorize}
   * @return a success message
   * @throws ProjectNotFoundException if the project does not exist
   */
  @PostMapping("/projectIa/{ignoredProjectId}")
  @PreAuthorize("isProjectOwner(#ignoredProjectId) or isContributer(#ignoredProjectId)")
  public SimpleMessageDto saveFeedback(@RequestBody final FeedbackRequest request,
                                       @PathVariable final Long ignoredProjectId) throws ProjectNotFoundException {
    log.info("Received feedback save request for projectId={}", ignoredProjectId);
    String message = feedbackIaService.saveFeedback(request);
    return new SimpleMessageDto(message, HttpStatus.OK.value(), LocalDateTime.now());
  }

  /**
   * Retrieves a feedback by project and user.
   *
   * @param projectId the project id
   * @param userId    the user id
   * @return the feedback
   * @throws FeedBackNotFoundException if no feedback is found
   */
  @GetMapping("/projectIa/{projectId}/{userId}")
  @PreAuthorize("isContributer(#projectId) or isObserver(#projectId)")
  public FeedbackResponse getFeedbackByProjectIdAndUserId(@PathVariable final Long projectId,
                                                          @PathVariable final Long userId)
    throws FeedBackNotFoundException {
    return feedbackIaService.getFeedbackByProjectIdAndUserId(projectId, userId);
  }

  /**
   * Deletes a feedback by project and user.
   *
   * @param projectId the project id
   * @param userId    the user id
   * @return a success message
   */
  @DeleteMapping("/projectIa/{projectId}/{userId}")
  @PreAuthorize("isProjectOwner(#projectId)")
  public SimpleMessageDto deleteFeedbackByUserAndProject(@PathVariable final Long projectId,
                                                         @PathVariable final Long userId) {
    log.info("Received feedback delete request for projectId={} userId={}", projectId, userId);
    feedbackIaService.deleteFeedbackByUserAndProject(projectId, userId);
    return new SimpleMessageDto("Le FeedBack a bien été supprimé", HttpStatus.OK.value(), LocalDateTime.now());
  }
}
