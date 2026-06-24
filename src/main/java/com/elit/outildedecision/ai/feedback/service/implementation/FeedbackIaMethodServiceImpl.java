package com.elit.outildedecision.ai.feedback.service.implementation;

import com.elit.outildedecision.ai.feedback.dto.FeedbackMethodRequest;
import com.elit.outildedecision.ai.feedback.dto.FeedbackMethodResponse;
import com.elit.outildedecision.ai.feedback.entity.FeedbackIaMethod;
import com.elit.outildedecision.ai.feedback.exception.FeedBackNotFoundException;
import com.elit.outildedecision.ai.feedback.mapper.FeedbackMapper;
import com.elit.outildedecision.ai.feedback.repository.FeedbackIaMethodRepository;
import com.elit.outildedecision.ai.feedback.service.FeedbackIaMethodService;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.exception.BadRequestException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.project.repository.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.elit.outildedecision.shared.project.service.implementation.ProjectServiceImpl.PROJECT_NOT_FOUND;

/** Feedback ia method service impl class. */
@Service
@Slf4j
public class FeedbackIaMethodServiceImpl implements FeedbackIaMethodService {

  public static final String FEEDBACK_NOT_FOUND = "Aucun feedback existant.";

  private final FeedbackIaMethodRepository feedbackIaMethodRepository;
  private final ProjectRepository projectRepository;
  private final FeedbackMapper feedbackMapper;

  /**
   * Constructor.
   *
   * @param feedbackIaMethodRepository the method feedback repository
   * @param projectRepository          the project repository
   * @param feedbackMapper             the feedback mapper
   */
  public FeedbackIaMethodServiceImpl(final FeedbackIaMethodRepository feedbackIaMethodRepository,
                                     final ProjectRepository projectRepository, final FeedbackMapper feedbackMapper) {
    this.feedbackIaMethodRepository = feedbackIaMethodRepository;
    this.projectRepository = projectRepository;
    this.feedbackMapper = feedbackMapper;
  }

  /**
   * Saves (or updates via upsert) a feedback on a recommended method.
   *
   * @param request the inbound payload
   * @return the success message
   * @throws ProjectNotFoundException if the project does not exist
   */
  @Override
  @Transactional
  public String saveFeedback(final FeedbackMethodRequest request) throws ProjectNotFoundException {
    if (request == null) {
      throw new BadRequestException("Body requis");
    }
    if (request.getProjectId() == null) {
      throw new BadRequestException("projectId est requis");
    }
    if (request.getUserId() == null) {
      throw new BadRequestException("userId est requis");
    }
    if (request.getMethodName() == null || request.getMethodName().isBlank()) {
      throw new BadRequestException("methodName est requis");
    }
    if (request.getIsLiked() == null) {
      throw new BadRequestException("isLiked est requis");
    }

    Project project = projectRepository.findById(request.getProjectId())
      .orElseThrow(() -> new ProjectNotFoundException(PROJECT_NOT_FOUND));

    Optional<FeedbackIaMethod> existing = feedbackIaMethodRepository
      .findByProject_ProjectIdAndUser_UserId(request.getProjectId(), request.getUserId());

    try {
      if (existing.isPresent()) {
        FeedbackIaMethod toUpdate = existing.get();
        toUpdate.setIsLiked(request.getIsLiked());
        toUpdate.setFeedbackAnswer(request.getFeedbackAnswer());
        toUpdate.setMethod(request.getMethodName());
        feedbackIaMethodRepository.save(toUpdate);
        log.info("Method feedback updated for projectId={} userId={} method={}",
          request.getProjectId(), request.getUserId(), request.getMethodName());
        return "Feedback mis à jour avec succès.";
      }

      FeedbackIaMethod toCreate = new FeedbackIaMethod();
      toCreate.setIsLiked(request.getIsLiked());
      toCreate.setFeedbackAnswer(request.getFeedbackAnswer());
      toCreate.setProject(project);
      User user = new User();
      user.setUserId(request.getUserId());
      toCreate.setUser(user);
      toCreate.setMethod(request.getMethodName());

      feedbackIaMethodRepository.save(toCreate);
      log.info("Method feedback created for projectId={} userId={} method={}",
        request.getProjectId(), request.getUserId(), request.getMethodName());
      return "Feedback enregistré avec succès.";
    } catch (RuntimeException e) {
      log.error("Failed to save method feedback for projectId={} userId={} method={}",
        request.getProjectId(), request.getUserId(), request.getMethodName(), e);
      throw e;
    }
  }

  /**
   * Retrieves the method feedbacks of a project.
   *
   * @param projectId the project id
   * @return the project method feedbacks
   */
  @Override
  @Transactional(readOnly = true)
  public List<FeedbackMethodResponse> getFeedbacksByProject(final Long projectId) {
    List<FeedbackIaMethod> feedbacks = feedbackIaMethodRepository.findByProject_ProjectId(projectId);
    return feedbackMapper.toMethodResponseList(feedbacks);
  }

  /**
   * Retrieves a method feedback for a project and user.
   *
   * <p>The lookup key is {@code (projectId, userId)} only: a user holds a single method feedback per
   * project (see {@link #saveFeedback} upsert). {@code methodName} is kept for the frozen URL
   * contract but does not narrow the query.
   *
   * @param projectId  the project id
   * @param userId     the user id
   * @param methodName part of the URL contract, not used as a lookup key
   * @return the feedback
   * @throws FeedBackNotFoundException if no feedback is found
   */
  @Override
  @Transactional(readOnly = true)
  public FeedbackMethodResponse getFeedbackByProjectIdAndUserIdAndMethodName(final Long projectId, final Long userId,
                                                                            final String methodName)
    throws FeedBackNotFoundException {
    FeedbackIaMethod feedback = feedbackIaMethodRepository.findByProject_ProjectIdAndUser_UserId(projectId, userId)
      .orElseThrow(() -> new FeedBackNotFoundException(FEEDBACK_NOT_FOUND));
    return feedbackMapper.toMethodResponse(feedback);
  }

  /**
   * Deletes a method feedback for a project and user.
   *
   * <p>The deletion key is {@code (projectId, userId)} only; {@code methodName} is kept for the
   * frozen URL contract but does not narrow the deletion (one feedback per project/user).
   *
   * @param projectId  the project id
   * @param userId     the user id
   * @param methodName part of the URL contract, not used as a deletion key
   */
  @Override
  @Transactional
  public void deleteFeedbackByUserAndProjectAndMethodName(final Long projectId, final Long userId,
                                                          final String methodName) {
    if (!feedbackIaMethodRepository.existsByProject_ProjectIdAndUser_UserId(projectId, userId)) {
      throw new FeedBackNotFoundException("Feedback non trouvé pour le projet ID " + projectId
        + ", l'utilisateur ID " + userId + " et la méthode " + methodName);
    }

    feedbackIaMethodRepository.deleteByProject_ProjectIdAndUser_UserId(projectId, userId);
    log.info("Method feedback deleted for projectId={} userId={} method={}", projectId, userId, methodName);
  }
}
