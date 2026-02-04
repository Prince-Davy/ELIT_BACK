package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.dto.FeedbackIaMethodCreateDto;
import com.elit.outildedecision.entity.FeedbackIaMethod;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.FeedBackNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.model.ErrorMessage;
import com.elit.outildedecision.model.FeedBackIaMethodModel;
import com.elit.outildedecision.repository.FeedbackIaMethodRepository;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.service.FeedbackIaMethodService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.elit.outildedecision.service.implementation.ProjectServiceImpl.PROJECT_NOT_FOUND;

@Service
public class FeedbackIaMethodServiceImpl implements FeedbackIaMethodService {
  public static final String FEEDBACK_NOT_FOUND = "Aucun feedback existant.";
  private final FeedbackIaMethodRepository feedbackIaMethodRepository;
  private final ProjectRepository projectRepository;

  /**
   * Constructeur du service FeedbackIaMethodServiceImpl.
   * @param feedbackIaMethodRepository Repository des feedbacks IA méthode
   * @param projectRepository  Repository des projets
   */
  public FeedbackIaMethodServiceImpl(
    final FeedbackIaMethodRepository feedbackIaMethodRepository,
    final ProjectRepository projectRepository
  ) {
    this.feedbackIaMethodRepository = feedbackIaMethodRepository;
    this.projectRepository = projectRepository;
  }

  /**
   * Enregistrer un feedback sur une méthode.
   * Modifiable sans endpoint update: POST fait un upsert sur le triplet (project,user,method).
   */
  @Override
  @Transactional
  public ResponseEntity<Object> saveFeedback(final FeedbackIaMethodCreateDto dto) throws ProjectNotFoundException {
    if (dto == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Body requis");
    }
    if (dto.getProjectId() == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("projectId est requis");
    }
    if (dto.getUserId() == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("userId est requis");
    }
    if (dto.getMethodName() == null || dto.getMethodName().isBlank()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("methodName est requis");
    }
    if (dto.getIsLiked() == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("isLiked est requis");
    }

    Project project = projectRepository.findById(dto.getProjectId())
      .orElseThrow(() -> new ProjectNotFoundException(PROJECT_NOT_FOUND));

    String methodTypeName = dto.getMethodName();

    Optional<FeedbackIaMethod> existing = feedbackIaMethodRepository
      .findByProject_ProjectIdAndUser_UserId(dto.getProjectId(), dto.getUserId());

    try {
      if (existing.isPresent()) {
        FeedbackIaMethod toUpdate = existing.get();
        toUpdate.setIsLiked(dto.getIsLiked());
        toUpdate.setFeedbackAnswer(dto.getFeedbackAnswer());
        // On conserve les associations
        toUpdate.setMethod(methodTypeName);
        feedbackIaMethodRepository.save(toUpdate);
        return ResponseEntity.ok().body("Feedback mis à jour avec succès.");
      }

      FeedbackIaMethod toCreate = new FeedbackIaMethod();
      toCreate.setIsLiked(dto.getIsLiked());
      toCreate.setFeedbackAnswer(dto.getFeedbackAnswer());
      toCreate.setProject(project);

      User user = new User();
      user.setUserId(dto.getUserId());
      toCreate.setUser(user);

      toCreate.setMethod(methodTypeName);

      feedbackIaMethodRepository.save(toCreate);
      return ResponseEntity.ok().body("Feedback enregistré avec succès.");
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("An unexpected error occurred: " + e.getMessage());
    }
  }

  @Override
  public List<FeedBackIaMethodModel> getAllFeedbacks() {
    List<FeedbackIaMethod> feedbacks = feedbackIaMethodRepository.findAll();
    List<FeedBackIaMethodModel> models = new ArrayList<>();

    for (FeedbackIaMethod feedback : feedbacks) {
      models.add(toModel(feedback));
    }

    return models;
  }

  @Override
  public List<FeedBackIaMethodModel> getFeedbacksByProject(final Long projectId) {
    List<FeedbackIaMethod> feedbacks = feedbackIaMethodRepository.findByProject_ProjectId(projectId);
    List<FeedBackIaMethodModel> models = new ArrayList<>();

    for (FeedbackIaMethod feedback : feedbacks) {
      models.add(toModel(feedback));
    }

    return models;
  }

  @Override
  public FeedBackIaMethodModel getFeedbackByProjectIdAndUserIdAndMethodName(final Long projectId, final Long userId, final String methodName)
    throws FeedBackNotFoundException {

    FeedbackIaMethod feedback = feedbackIaMethodRepository
      .findByProject_ProjectIdAndUser_UserId(projectId, userId)
      .orElseThrow(() -> new FeedBackNotFoundException(FEEDBACK_NOT_FOUND));

    return toModel(feedback);
  }

  @Override
  @Transactional
  public ResponseEntity<Object> deleteFeedbackByUserAndProjectAndMethodName(final Long projectId, final Long userId, final String methodName) {
    try {
      feedbackIaMethodRepository.findByProject_ProjectIdAndUser_UserId(projectId, userId)
        .orElseThrow(() -> new FeedBackNotFoundException("Feedback non trouvé pour le projet ID " + projectId
          + ", l'utilisateur ID " + userId + " et la méthode " + methodName));

      feedbackIaMethodRepository.deleteByProject_ProjectIdAndUser_UserId(projectId, userId);
    } catch (FeedBackNotFoundException e) {
      ErrorMessage err = new ErrorMessage(HttpStatus.NOT_FOUND, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("Une erreur inattendue s'est produite : " + e.getMessage());
    }
    return ResponseEntity.ok().body("Le FeedBack a bien été supprimé");
  }

  @Override
  @Transactional
  public ResponseEntity<Object> deleteFeedbacksByProjectId(final Long projectId) {
    try {
      List<FeedbackIaMethod> feedbacks = feedbackIaMethodRepository.findByProject_ProjectId(projectId);
      if (feedbacks.isEmpty()) {
        throw new FeedBackNotFoundException("Feedback non trouvé pour le projet ID " + projectId);
      }
      feedbackIaMethodRepository.deleteAll(feedbacks);
    } catch (FeedBackNotFoundException e) {
      ErrorMessage err = new ErrorMessage(HttpStatus.NOT_FOUND, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("Une erreur inattendue s'est produite : " + e.getMessage());
    }
    return ResponseEntity.ok().body("Tous les FeedBacks du projet ont bien été supprimés");
  }

  private FeedBackIaMethodModel toModel(final FeedbackIaMethod feedback) {
    FeedBackIaMethodModel model = new FeedBackIaMethodModel();
    model.setProjectId(feedback.getProject().getProjectId());
    model.setUserId(feedback.getUser().getUserId());
    model.setIsLiked(feedback.getIsLiked());
    model.setFeedbackAnswer(feedback.getFeedbackAnswer());
    model.setMethodName(feedback.getMethod());

    return model;
  }
}
