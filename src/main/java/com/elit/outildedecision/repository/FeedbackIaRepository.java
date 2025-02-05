package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.FeedbackIa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackIaRepository extends JpaRepository<FeedbackIa, Long> {


  /**
   * Permet de récupérer l'ensemble des feedbacks lié à un projet.
   *
   * @param projectId projectId
   * @return List<FeedbackIa>
   */
  List<FeedbackIa> findByProject_ProjectId(Long projectId);

  /**
   * Permet de récupérer un feedback lié à un projet et un utilisateur spécifique.
   *
   * @param projectId projectId
   * @param userId    userId
   * @return List<FeedbackIa>
   */
  Optional<FeedbackIa> findByProject_ProjectIdAndUser_UserId(Long projectId, Long userId);

  /**
   * Permet de supprimer un feedback lié à un utilisateur pour un projet spécifique.
   *
   * @param projectId project id
   * @param userId user id
   */
  void deleteByProject_ProjectIdAndUser_UserId(Long projectId, Long userId);
}
