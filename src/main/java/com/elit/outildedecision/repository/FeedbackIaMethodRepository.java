package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.FeedbackIaMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour les entités FeedbackIaMethod.
 */
@Repository
public interface FeedbackIaMethodRepository extends JpaRepository<FeedbackIaMethod, Long> {

  /**
   * Récupère les feedbacks d'un projet.
   *
   * @param projectId identifiant du projet.
   * @return liste des feedbacks du projet.
   */
  List<FeedbackIaMethod> findByProject_ProjectId(Long projectId);

  /**
   * Récupère le feedback (s'il existe) pour un projet et un utilisateur.
   *
   * @param projectId identifiant du projet.
   * @param userId identifiant de l'utilisateur.
   * @return feedback optionnel.
   */
  Optional<FeedbackIaMethod> findByProject_ProjectIdAndUser_UserId(Long projectId, Long userId);

  /**
   * Supprime le feedback pour un projet et un utilisateur.
   *
   * @param projectId identifiant du projet.
   * @param userId identifiant de l'utilisateur.
   */
  void deleteByProject_ProjectIdAndUser_UserId(Long projectId, Long userId);
}
