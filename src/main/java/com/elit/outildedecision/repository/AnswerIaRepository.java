package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.AnswerIa;
import com.elit.outildedecision.entity.AnswerIaKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerIaRepository extends JpaRepository<AnswerIa, AnswerIaKey> {

  /**
   * Permet de récupérer l'ensemble des réponses lié à un projet.
   *
   * @param projectId projectId
   * @return List<AnswerIa>
   */
  List<AnswerIa> findByProject_ProjectId(Long projectId);

  /**
   * Permet la suppression des réponses enregistré d'un projet.
   *
   * @param projectId id
   */
  void deleteByProject_ProjectId(Long projectId);

  /**
   * trouver une reponse bien précise.
   *
   * @param question la question concerner
   * @param projectId id du projet lier
   * @return AnswerIa
   */
  Optional<AnswerIa> findByQuestionAndProject_ProjectId(String question, Long projectId);

}
