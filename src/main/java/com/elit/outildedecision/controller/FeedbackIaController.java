package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.FeedbackIa;
import com.elit.outildedecision.exception.FeedBackNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.model.FeedBackIaModel;
import com.elit.outildedecision.service.FeedbackIaService;
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
 * Controller pour la gestion FeedbackIA.
 */
@RestController
@RequestMapping("/feedbacks")
public class FeedbackIaController {

  private final FeedbackIaService feedbackIaService;

  /**
   * Constructeur pour FeedbackIaController.
   *
   * @param feedbackIaService feedbackIaService
   */
  public FeedbackIaController(final FeedbackIaService feedbackIaService) {
    this.feedbackIaService = feedbackIaService;
  }

  /**
   * Creer et Sauvegarder un feedback dans la BDD pour un resultat IA d'un projet.
   *
   * @param feedbackIa Le feedback de l'utilisateur
   * @param ignoredProjectId id du projet
   * @return ResponseEntity<Object>
   */
  @PostMapping("/projectIa/{ignoredProjectId}")
  @PreAuthorize("isProjectOwner(#ignoredProjectId) or isContributer(#ignoredProjectId)")
  public ResponseEntity<Object> saveFeedback(@RequestBody final FeedbackIa feedbackIa, @PathVariable final Long ignoredProjectId) throws ProjectNotFoundException, FeedBackNotFoundException {
    // ignoredProjectId est utilisé dans l'annotation @PreAuthorize pour vérifier les autorisations
    return feedbackIaService.saveFeedback(feedbackIa);
  }

  /**
   * Récupère les tous les feedbacks.
   *
   * @return List<FeedbackIa>
   */
  @GetMapping("/projectIa/allFeedbacks")
  @PreAuthorize("hasAnyAuthority('USER')")
  public ResponseEntity<List<FeedBackIaModel>> getAllFeedbacks() {
    List<FeedBackIaModel> feedbacks = feedbackIaService.getAllFeedbacks();
    return ResponseEntity.ok(feedbacks);
  }

  /**
   * Récupère les feedbacks associés à un projet spécifique.
   *
   * @param projectId projectId
   * @return List<FeedbackIa>
   */
  @GetMapping("/projectIa/{projectId}")
  @PreAuthorize("hasAnyAuthority('USER')")
  public ResponseEntity<List<FeedBackIaModel>> getFeedbacksByProjectId(@PathVariable final Long projectId) {
    List<FeedBackIaModel> feedbacks = feedbackIaService.getFeedbacksByProject(projectId);
    return ResponseEntity.ok(feedbacks);
  }

  /**
   * Recuperer un feedback par user et projet.
   *
   * @param projectId projectId
   * @param userId userId
   * @return esponseEntity<Object>
   */
  @GetMapping("/projectIa/{projectId}/{userId}")
  @PreAuthorize("hasAnyAuthority('USER')")
  public ResponseEntity<FeedBackIaModel> getFeedbackByProjectIdAndUserId(@PathVariable final Long projectId, @PathVariable final Long userId) throws FeedBackNotFoundException {
    FeedBackIaModel feedback = feedbackIaService.getFeedbackByProjectIdAndUserId(projectId, userId);

    return ResponseEntity.ok(feedback);
  }

  /**
   * Supprime un feedback spécifique par projet et par user.
   *
   * @param projectId projectId
   * @param userId userId
   * @return ResponseEntity<Object>
   */
  @DeleteMapping("projectIa/{projectId}/{userId}")
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<Object> deleteFeedbackByUserAndProject(@PathVariable final Long projectId, @PathVariable final Long userId) {
    return feedbackIaService.deleteFeedbackByUserAndProject(projectId, userId);
  }

  /**
   * Supprime tous les feedbacks associés à un projet spécifique.
   *
   * @param projectId projectId
   * @return ResponseEntityNoContent
   */
  @DeleteMapping("/projectIa/{projectId}")
  @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<Object> deleteFeedbacksByProjectId(@PathVariable final Long projectId) {
    return feedbackIaService.deleteFeedbacksByProjectId(projectId);
  }
}
