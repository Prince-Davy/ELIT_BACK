package com.elit.outildedecision.ai.feedback.service.implementation;

import com.elit.outildedecision.ai.feedback.dto.FeedbackRequest;
import com.elit.outildedecision.ai.feedback.dto.FeedbackResponse;
import com.elit.outildedecision.ai.feedback.entity.FeedbackIa;
import com.elit.outildedecision.ai.feedback.exception.FeedBackNotFoundException;
import com.elit.outildedecision.ai.feedback.mapper.FeedbackMapper;
import com.elit.outildedecision.ai.feedback.repository.FeedbackIaRepository;
import com.elit.outildedecision.ai.feedback.service.FeedbackIaService;
import com.elit.outildedecision.shared.exception.ConflictException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.project.repository.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.elit.outildedecision.shared.project.service.implementation.ProjectServiceImpl.PROJECT_NOT_FOUND;

/** Feedback ia service impl class. */
@Service
@Slf4j
public class FeedbackIaServiceImpl implements FeedbackIaService {

  public static final String FEEDBACK_NOT_FOUND = "Ce Feedback n'existe pas !";

  private final FeedbackIaRepository feedbackIaRepository;
  private final ProjectRepository projectRepository;
  private final FeedbackMapper feedbackMapper;

  /**
   * Constructor.
   *
   * @param feedbackIaRepository the feedback repository
   * @param projectRepository    the project repository
   * @param feedbackMapper       the feedback mapper
   */
  public FeedbackIaServiceImpl(final FeedbackIaRepository feedbackIaRepository, final ProjectRepository projectRepository,
                               final FeedbackMapper feedbackMapper) {
    this.feedbackIaRepository = feedbackIaRepository;
    this.projectRepository = projectRepository;
    this.feedbackMapper = feedbackMapper;
  }

  /**
   * Persists a feedback on the AI methodology recommendation of a project.
   *
   * @param request the inbound feedback payload
   * @return the success message
   * @throws ProjectNotFoundException if the project does not exist
   */
  @Override
  @Transactional
  public String saveFeedback(final FeedbackRequest request) throws ProjectNotFoundException {
    Long projectId = request.getProject().getProjectId();
    Long userId = request.getUser().getUserId();

    projectRepository.findById(projectId)
      .orElseThrow(() -> new ProjectNotFoundException(PROJECT_NOT_FOUND));

    if (feedbackIaRepository.findByProject_ProjectIdAndUser_UserId(projectId, userId).isPresent()) {
      throw new ConflictException("Vous avez déjà un feedback pour ce projet!");
    }

    try {
      feedbackIaRepository.save(feedbackMapper.toEntity(request));
    } catch (RuntimeException e) {
      log.error("Failed to save feedback for projectId={} userId={}", projectId, userId, e);
      throw e;
    }

    log.info("Feedback saved for projectId={} userId={}", projectId, userId);
    return "Feedback enregistré avec succès.";
  }

  /**
   * Retrieves the feedback left by a user on a project.
   *
   * @param projectId the project id
   * @param userId    the user id
   * @return the feedback
   * @throws FeedBackNotFoundException if no feedback exists for this project/user
   */
  @Override
  @Transactional(readOnly = true)
  public FeedbackResponse getFeedbackByProjectIdAndUserId(final Long projectId, final Long userId)
    throws FeedBackNotFoundException {
    FeedbackIa feedback = feedbackIaRepository.findByProject_ProjectIdAndUser_UserId(projectId, userId)
      .orElseThrow(() -> new FeedBackNotFoundException(FEEDBACK_NOT_FOUND));
    return feedbackMapper.toResponse(feedback);
  }

  /**
   * Deletes the feedback left by a user on a project.
   *
   * @param projectId the project id
   * @param userId    the user id
   */
  @Override
  @Transactional
  public void deleteFeedbackByUserAndProject(final Long projectId, final Long userId) {
    if (!feedbackIaRepository.existsByProject_ProjectIdAndUser_UserId(projectId, userId)) {
      throw new FeedBackNotFoundException(
        "Feedback non trouvé pour le projet ID " + projectId + " et l'utilisateur ID " + userId);
    }

    feedbackIaRepository.deleteByProject_ProjectIdAndUser_UserId(projectId, userId);
    log.info("Feedback deleted for projectId={} userId={}", projectId, userId);
  }

  /**
   * Deletes all the feedbacks of a project (used when a project is removed).
   *
   * @param projectId the project id
   */
  @Override
  @Transactional
  public void deleteFeedbacksByProjectId(final Long projectId) {
    List<FeedbackIa> feedbacks = feedbackIaRepository.findByProject_ProjectId(projectId);
    if (!feedbacks.isEmpty()) {
      feedbackIaRepository.deleteAll(feedbacks);
      log.info("Deleted {} feedback(s) for projectId={}", feedbacks.size(), projectId);
    }
  }
}
