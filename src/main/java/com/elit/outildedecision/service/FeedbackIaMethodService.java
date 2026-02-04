package com.elit.outildedecision.service;

import com.elit.outildedecision.dto.FeedbackIaMethodCreateDto;
import com.elit.outildedecision.exception.FeedBackNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.model.FeedBackIaMethodModel;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Service métier pour la gestion des feedbacks IA sur une méthode.
 */
public interface FeedbackIaMethodService {

  /**
   * Enregistre (ou met à jour) un feedback via un upsert.
   *
   * @param dto données d'entrée.
   * @return réponse HTTP.
   * @throws ProjectNotFoundException si le projet n'existe pas.
   */
  ResponseEntity<Object> saveFeedback(FeedbackIaMethodCreateDto dto) throws ProjectNotFoundException;

  /**
   * Récupère tous les feedbacks.
   *
   * @return liste des feedbacks.
   */
  List<FeedBackIaMethodModel> getAllFeedbacks();

  /**
   * Récupère les feedbacks d'un projet.
   *
   * @param projectId identifiant du projet.
   * @return liste des feedbacks du projet.
   */
  List<FeedBackIaMethodModel> getFeedbacksByProject(Long projectId);

  /**
   * Récupère un feedback par projet, utilisateur et nom de méthode.
   *
   * @param projectId identifiant du projet.
   * @param userId identifiant de l'utilisateur.
   * @param methodName nom de la méthode.
   * @return le feedback.
   * @throws FeedBackNotFoundException si aucun feedback n'est trouvé.
   */
  FeedBackIaMethodModel getFeedbackByProjectIdAndUserIdAndMethodName(Long projectId, Long userId, String methodName)
    throws FeedBackNotFoundException;

  /**
   * Supprime un feedback par projet, utilisateur et nom de méthode.
   *
   * @param projectId identifiant du projet.
   * @param userId identifiant de l'utilisateur.
   * @param methodName nom de la méthode.
   * @return réponse HTTP.
   */
  ResponseEntity<Object> deleteFeedbackByUserAndProjectAndMethodName(Long projectId, Long userId, String methodName);

  /**
   * Supprime tous les feedbacks d'un projet.
   *
   * @param projectId identifiant du projet.
   * @return réponse HTTP.
   */
  ResponseEntity<Object> deleteFeedbacksByProjectId(Long projectId);
}
