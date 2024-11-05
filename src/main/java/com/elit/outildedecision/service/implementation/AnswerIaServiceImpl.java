package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.AnswerIa;
import com.elit.outildedecision.repository.AnswerIaRepository;
import com.elit.outildedecision.service.AnswerIaService;
import org.springframework.stereotype.Service;
import javax.persistence.EntityManager;

import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AnswerIaServiceImpl implements AnswerIaService {

  @PersistenceContext
  private EntityManager entityManager;
  private final AnswerIaRepository answerIaRepository;

  /**
   * @param answerIaRepository answerIaRepository
   */
  public AnswerIaServiceImpl(final AnswerIaRepository answerIaRepository) {
    this.answerIaRepository = answerIaRepository;
  }

  /**
   * @param projectId projectId
   * @return List<AnswerIa>
   */
  @Override
  public List<AnswerIa> getAllAnswers(final long projectId) {
    return answerIaRepository.findByProject_ProjectId(projectId);
  }

  /**
   * @param answers answers
   */
  @Override
  @Transactional
  public void saveAll(final List<AnswerIa> answers) {
    List<AnswerIa> updatedAnswers = new ArrayList<>();

    for (AnswerIa answer : answers) {
      // Rechercher la réponse existante par question et projectId
      Optional<AnswerIa> existingAnswer = answerIaRepository.findByQuestionAndProject_ProjectId(
        answer.getQuestion(),
        answer.getProject().getProjectId()
      );

      if (existingAnswer.isPresent()) {
        AnswerIa answerInDb = existingAnswer.get();

        // Vérifier si la réponse a changé
        if (!answerInDb.getAnswer().equals(answer.getAnswer())) {
          // Supprimer l'enregistrement actuel pour éviter le doublon
          answerIaRepository.delete(answerInDb);

          // Ajouter la réponse mise à jour à la liste des réponses à sauvegarder
          updatedAnswers.add(answer);
        }
      } else {
        // Si la réponse n'existe pas en base, ajouter directement la nouvelle réponse
        updatedAnswers.add(answer);
      }
    }
    // Sauvegarder les réponses qui ont été modifiées ou qui sont nouvelles
    if (!updatedAnswers.isEmpty()) {
      answerIaRepository.saveAll(updatedAnswers);
    }
  }

  /**
   * @param projectId id
   */
  @Override
  @Transactional
  public void deleteAnswersByProjectId(final long projectId) {
    answerIaRepository.deleteByProject_ProjectId(projectId);
  }

}
