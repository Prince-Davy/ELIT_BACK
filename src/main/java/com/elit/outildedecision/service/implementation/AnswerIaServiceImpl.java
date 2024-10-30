package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.AnswerIa;
import com.elit.outildedecision.repository.AnswerIaRepository;
import com.elit.outildedecision.service.AnswerIaService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class AnswerIaServiceImpl implements AnswerIaService {

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
  public void saveAll(final List<AnswerIa> answers) {
    answerIaRepository.saveAll(answers);
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
