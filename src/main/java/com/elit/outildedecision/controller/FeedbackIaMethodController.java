package com.elit.outildedecision.controller;

import com.elit.outildedecision.dto.FeedbackIaMethodCreateDto;
import com.elit.outildedecision.exception.FeedBackNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.model.FeedBackIaMethodModel;
import com.elit.outildedecision.service.FeedbackIaMethodService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller pour la gestion Feedback IA étape 3 (feedback sur une méthode).
 */
@RestController
@RequestMapping("/feedbacks-method")
public class FeedbackIaMethodController {

  private final FeedbackIaMethodService feedbackIaMethodService;

  /**
   * Construit le contrôleur.
   *
   * @param feedbackIaMethodService service métier.
   */
  public FeedbackIaMethodController(final FeedbackIaMethodService feedbackIaMethodService) {
    this.feedbackIaMethodService = feedbackIaMethodService;
  }

  /**
   * Créer / modifier (upsert) un feedback sur une méthode.
   *
   * @param dto DTO de création/modification.
   * @param ignoredProjectId identifiant projet utilisé uniquement pour l'autorisation.
   * @return réponse HTTP.
   * @throws ProjectNotFoundException si le projet n'existe pas.
   */
  @PostMapping("/projectIa/method/{ignoredProjectId}")
  @PreAuthorize("isProjectOwner(#ignoredProjectId) or isContributer(#ignoredProjectId)")
  public ResponseEntity<Object> saveFeedback(
    @Valid @RequestBody final FeedbackIaMethodCreateDto dto,
    @PathVariable final Long ignoredProjectId
  ) throws ProjectNotFoundException {
    // ignoredProjectId utilisé uniquement dans @PreAuthorize
    return feedbackIaMethodService.saveFeedback(dto);
  }

  /**
   * Récupère l'ensemble des feedbacks.
   *
   * @return la liste des feedbacks.
   */
  @GetMapping("/projectIa/method/allFeedbacks")
  @PreAuthorize("hasAnyAuthority('USER')")
  public ResponseEntity<List<FeedBackIaMethodModel>> getAllFeedbacks() {
    return ResponseEntity.ok(feedbackIaMethodService.getAllFeedbacks());
  }

  /**
   * Récupère les feedbacks d'un projet.
   *
   * @param projectId identifiant du projet.
   * @return la liste des feedbacks du projet.
   */
  @GetMapping("/projectIa/method/{projectId}")
  @PreAuthorize("hasAnyAuthority('USER')")
  public ResponseEntity<List<FeedBackIaMethodModel>> getFeedbacksByProjectId(@PathVariable final Long projectId) {
    return ResponseEntity.ok(feedbackIaMethodService.getFeedbacksByProject(projectId));
  }

  /**
   * Récupère un feedback par projet, utilisateur et nom de méthode.
   *
   * @param projectId identifiant du projet.
   * @param userId identifiant de l'utilisateur.
   * @param methodName nom de la méthode.
   * @return le feedback.
   * @throws FeedBackNotFoundException si aucun feedback n'est trouvé.
   */
  @GetMapping("/projectIa/method/{projectId}/{userId}/{methodName}")
  @PreAuthorize("hasAnyAuthority('USER')")
  public ResponseEntity<FeedBackIaMethodModel> getFeedbackByProjectIdAndUserIdAndMethodName(
    @PathVariable final Long projectId,
    @PathVariable final Long userId,
    @PathVariable final String methodName
  ) throws FeedBackNotFoundException {
    return ResponseEntity.ok(feedbackIaMethodService.getFeedbackByProjectIdAndUserIdAndMethodName(projectId, userId, methodName));
  }

  /**
   * Supprime un feedback par projet, utilisateur et nom de méthode.
   *
   * @param projectId identifiant du projet.
   * @param userId identifiant de l'utilisateur.
   * @param methodName nom de la méthode.
   * @return réponse HTTP.
   */
  @DeleteMapping("/projectIa/method/{projectId}/{userId}/{methodName}")
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<Object> deleteFeedbackByUserAndProjectAndMethodName(
    @PathVariable final Long projectId,
    @PathVariable final Long userId,
    @PathVariable final String methodName
  ) {
    return feedbackIaMethodService.deleteFeedbackByUserAndProjectAndMethodName(projectId, userId, methodName);
  }

  /**
   * Supprime tous les feedbacks d'un projet.
   *
   * @param projectId identifiant du projet.
   * @return réponse HTTP.
   */
  @DeleteMapping("/projectIa/method/{projectId}")
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<Object> deleteFeedbacksByProjectId(@PathVariable final Long projectId) {
    return feedbackIaMethodService.deleteFeedbacksByProjectId(projectId);
  }
}
