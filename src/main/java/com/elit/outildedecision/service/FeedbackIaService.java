package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.FeedbackIa;
import com.elit.outildedecision.exception.FeedBackNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.model.FeedBackIaModel;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface FeedbackIaService {

  /**
   * Permet de sauvegarder un feedback.
   *
   * @param feedbackIa feedbackIa
   * @return ResponseEntity<Object>
   */
  ResponseEntity<Object> saveFeedback(FeedbackIa feedbackIa) throws ProjectNotFoundException, FeedBackNotFoundException;

  /**
   * Permet de récupérer tous les feedbacks.
   *
   * @return List<FeedBackIaModel>
   */
  List<FeedBackIaModel> getAllFeedbacks();

  /**
   * Permet de récupérer un feedback lié à un user pour un projet spécifique.
   *
   * @param projectId projectId
   * @param userId userId
   * @return List<FeedBackIaModel>
   */
  FeedBackIaModel getFeedbackByProjectIdAndUserId(Long projectId, Long userId) throws FeedBackNotFoundException;

  /**
   * Permet de récupérer tous les feedbacks d'un projet.
   *
   * @param projectId projectId
   * @return List<FeedBackIaModel>
   */
  List<FeedBackIaModel> getFeedbacksByProject(Long projectId);

  /**
   * Permet de supprimer un feedback d'un utilisateur pour un projet spécifique.
   *
   * @param projectId projectId
   * @param userId userId
   * @return ResponseEntity<Object>
   */
  ResponseEntity<Object> deleteFeedbackByUserAndProject(Long projectId, Long userId);

  /**
   * Permet de supprimer tous les feedbacks d'un projet.
   *
   * @param projectId projectId
   * @return ResponseEntity<Object>
   */
  ResponseEntity<Object> deleteFeedbacksByProjectId(Long projectId);

}
