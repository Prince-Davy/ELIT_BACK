package com.elit.outildedecision.ai.feedback.repository;

import com.elit.outildedecision.ai.feedback.entity.FeedbackIa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Feedback ia repository interface. */
@Repository
public interface FeedbackIaRepository extends JpaRepository<FeedbackIa, Long> {

  /**
   * Permet de recuperer l'ensemble des feedbacks lie a un projet.
   *
   * @param projectId projectId
   * @return List of feedbacks
   */
  List<FeedbackIa> findByProject_ProjectId(Long projectId);

  /**
   * Permet de recuperer un feedback lie a un projet et un utilisateur specifique.
   *
   * @param projectId projectId
   * @param userId    userId
   * @return optional feedback
   */
  Optional<FeedbackIa> findByProject_ProjectIdAndUser_UserId(Long projectId, Long userId);

  /**
   * Indique si un feedback existe pour un projet et un utilisateur specifique.
   *
   * @param projectId projectId
   * @param userId    userId
   * @return true si le feedback existe
   */
  boolean existsByProject_ProjectIdAndUser_UserId(Long projectId, Long userId);

  /**
   * Permet de supprimer un feedback lie a un utilisateur pour un projet specifique.
   *
   * @param projectId project id
   * @param userId user id
   */
  void deleteByProject_ProjectIdAndUser_UserId(Long projectId, Long userId);
}
