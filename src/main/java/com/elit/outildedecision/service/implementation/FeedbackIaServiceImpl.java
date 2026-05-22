package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.FeedbackIa;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.exception.FeedBackNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.model.FeedBackIaModel;
import com.elit.outildedecision.repository.FeedbackIaRepository;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.repository.UserRepository;
import com.elit.outildedecision.service.FeedbackIaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.elit.outildedecision.model.ErrorMessage;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.elit.outildedecision.service.implementation.ProjectServiceImpl.PROJECT_NOT_FOUND;

@Service
public class FeedbackIaServiceImpl implements FeedbackIaService {

  public static final String FEEDBACK_NOT_FOUND = "Ce Feedback n'existe pas !";
  private final FeedbackIaRepository feedbackIaRepository;
  private final ProjectRepository projectRepository;
  private final UserRepository userRepository;

  /**
   * @param feedbackIaRepository feedbackIaRepository
   * @param projectRepository projectRepository
   */
  public FeedbackIaServiceImpl(final FeedbackIaRepository feedbackIaRepository, final ProjectRepository projectRepository, final UserRepository userRepository) {
    this.feedbackIaRepository = feedbackIaRepository;
    this.projectRepository = projectRepository;
    this.userRepository = userRepository;
  }

  /**
   * Enregistrer un feedback.
   *
   * @param feedbackIa feedbackIa envoy&eacute; depuis le front
   * @return ResponseEntity<Object>
   * @throws ProjectNotFoundException Si le projet n'existe pas
   * @throws FeedBackNotFoundException Si le feedback n'existe pas
   */
  @Override
  @Transactional
  public ResponseEntity<Object> saveFeedback(final FeedbackIa feedbackIa) throws ProjectNotFoundException {
    Project project = projectRepository.findById(feedbackIa.getProject().getProjectId())
      .orElseThrow(() -> new ProjectNotFoundException(PROJECT_NOT_FOUND));

    // Attacher l'entité Project récupérée pour éviter les problèmes de persistance
    feedbackIa.setProject(project);

    // Récupérer et attacher l'entité User si fournie
    if (Objects.nonNull(feedbackIa.getUser()) && Objects.nonNull(feedbackIa.getUser().getUserId()) && Objects.nonNull(userRepository)) {
      userRepository.findById(feedbackIa.getUser().getUserId()).ifPresent(feedbackIa::setUser);
    }

    Optional<FeedbackIa> feedback = feedbackIaRepository.findByProject_ProjectIdAndUser_UserId(feedbackIa.getProject().getProjectId(), feedbackIa.getUser().getUserId());

    if (Objects.nonNull(project.getProjectId()) && feedback.isPresent()) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Vous avez déjà un feedback pour ce projet!");
    } else {
      try {
        feedbackIaRepository.save(feedbackIa);
      } catch (Exception e) {
        return ResponseEntity.internalServerError().body("An unexpected error occurred: " + e.getMessage());
      }
    }
    return ResponseEntity.ok().body("Feedback enregistré avec succès.");
  }

  /**
   * Récupérer l'ensemble des feedbacks sur l'ensemble des projets.
   *
   * @return List<FeedBackIaModel>
   */
  @Override
  public List<FeedBackIaModel> getAllFeedbacks() {
    List<FeedbackIa> feedbacks = feedbackIaRepository.findAll();
    List<FeedBackIaModel> feedbacksModelList = new ArrayList<>();

    for (FeedbackIa feedback : feedbacks) {
      FeedBackIaModel feedBacksModel = new FeedBackIaModel();
      feedBacksModel.setProjectId(feedback.getProject().getProjectId());
      feedBacksModel.setFeedbackAnswer(feedback.getFeedbackAnswer());
      feedBacksModel.setUserId(feedback.getUser().getUserId());
      feedBacksModel.setMethodology(feedback.getMethodology());
      feedBacksModel.setIsLiked(feedback.getIsLiked());
      feedbacksModelList.add(feedBacksModel);
    }
    return feedbacksModelList;
  }


  /**
   * Récupérer l'ensemble des feedbacks sur un projet spécifique.
   *
   * @param projectId projectId
   * @return List<FeedBackIaModel>
   */
  @Override
  public List<FeedBackIaModel> getFeedbacksByProject(final Long projectId) {
    List<FeedbackIa> feedbacksOfProject = feedbackIaRepository.findByProject_ProjectId(projectId);
    List<FeedBackIaModel> feedbacksModelList = new ArrayList<>();

    for (FeedbackIa feedback : feedbacksOfProject) {
      FeedBackIaModel feedBacksModel = new FeedBackIaModel();
      feedBacksModel.setProjectId(feedback.getProject().getProjectId());
      feedBacksModel.setFeedbackAnswer(feedback.getFeedbackAnswer());
      feedBacksModel.setUserId(feedback.getUser().getUserId());
      feedBacksModel.setMethodology(feedback.getMethodology());
      feedBacksModel.setIsLiked(feedback.getIsLiked());
      feedbacksModelList.add(feedBacksModel);
    }
    return feedbacksModelList;
  }


  /**
   * Récupérer un feedback lié à un user sur un projet spécifique.
   *
   * @param projectId projectId
   * @param userId userId
   * @return FeedBackIaModel
   * @throws FeedBackNotFoundException Si le feedback n'existe pas
   */
  @Override
  public FeedBackIaModel getFeedbackByProjectIdAndUserId(final Long projectId, final Long userId)
    throws FeedBackNotFoundException {
    FeedbackIa feedback = feedbackIaRepository.findByProject_ProjectIdAndUser_UserId(projectId, userId)
      .orElseThrow(() -> new FeedBackNotFoundException(FEEDBACK_NOT_FOUND));
    FeedBackIaModel feedBackModel = new FeedBackIaModel();
    feedBackModel.setProjectId(feedback.getProject().getProjectId());
    feedBackModel.setFeedbackAnswer(feedback.getFeedbackAnswer());
    feedBackModel.setUserId(feedback.getUser().getUserId());
    feedBackModel.setMethodology(feedback.getMethodology());
    feedBackModel.setIsLiked(feedback.getIsLiked());
    return feedBackModel;
  }

  /**
   * Supprimer l'ensemble des feedbacks d'un user sur un projet.
   *
   * @param projectId projectId
   * @param userId userId
   */
  @Override
  @Transactional
  public ResponseEntity<Object> deleteFeedbackByUserAndProject(final Long projectId, final Long userId) {
    try {
      FeedbackIa feedback = feedbackIaRepository.findByProject_ProjectIdAndUser_UserId(projectId, userId)
        .orElseThrow(() -> new FeedBackNotFoundException("Feedback non trouvé pour le projet ID " + projectId + " et l'utilisateur ID " + userId));

      if (Objects.isNull(feedback)) {
        throw new FeedBackNotFoundException("Feedback non trouvé pour le projet ID " + projectId + " et l'utilisateur ID " + userId);
      }
      feedbackIaRepository.deleteByProject_ProjectIdAndUser_UserId(projectId, userId);
    } catch (FeedBackNotFoundException e) {
      // If the exception is a ResponseStatusException (it is), prefer the reason
      String reason = (e instanceof ResponseStatusException) ? ((ResponseStatusException) e).getReason() : e.getMessage();
      ErrorMessage err = new ErrorMessage(HttpStatus.NOT_FOUND, reason);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("Une erreur inattendue s'est produite : " + e.getMessage());
    }
    return ResponseEntity.ok().body("Le FeedBack a bien été supprimé");
  }

  /**
   * Supprimer l'ensemble des feedbacks sur un projet.
   *
   * @param projectId projectId
   * @return ResponseEntity<Object>
   */
  @Override
  @Transactional
  public ResponseEntity<Object> deleteFeedbacksByProjectId(final Long projectId) {
    try {
      List<FeedbackIa> feedbacks = feedbackIaRepository.findByProject_ProjectId(projectId);
      if (feedbacks.isEmpty()) {
        throw new FeedBackNotFoundException("Feedback non trouvé pour le projet ID " + projectId);
      }
      feedbackIaRepository.deleteAll(feedbacks);
    } catch (FeedBackNotFoundException e) {
      String reason = (e instanceof ResponseStatusException) ? ((ResponseStatusException) e).getReason() : e.getMessage();
      ErrorMessage err = new ErrorMessage(HttpStatus.NOT_FOUND, reason);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("Une erreur inattendue s'est produite : " + e.getMessage());
    }
    return ResponseEntity.ok().body("Tous les FeedBacks du projet ont bien été supprimé");
  }
}
