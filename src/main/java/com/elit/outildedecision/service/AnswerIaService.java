package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.AnswerIa;

import java.util.List;

public interface AnswerIaService {

  /**
   * @param projectId projectId
   * @return List<AnswerIa>
   */
  List<AnswerIa> getAllAnswers(long projectId);

  /**
   * @param answers answers
   */
  void saveAll(List<AnswerIa> answers);

  /**
   * @param projectId projectId
   */
  void deleteAnswersByProjectId(long projectId);

}
