package com.elit.outildedecision.ai.answer.service;

import com.elit.outildedecision.ai.answer.entity.AiAnswer;
import com.elit.outildedecision.ai.answer.exception.AiAnswerNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;

import java.util.List;
import java.util.Map;

public interface AiAnswerService {

  /**
   * Saves a set of AI-generated answers for a given project.
   *
   * @param projectId the ID of the project to associate with the answers
   * @param data a map containing question-answer pairs
   * @throws ProjectNotFoundException if the project does not exist
   * @throws AiAnswerNotFoundException if the data is invalid or incomplete
   */
  void saveAnswers(Long projectId, Map<String, String> data) throws ProjectNotFoundException, AiAnswerNotFoundException;

  /**
   * Retrieves all AI-generated answers associated with a specific project.
   *
   * @param projectId the ID of the project
   * @return a list of AiAnswer entities
   * @throws AiAnswerNotFoundException if no answers are found for the project
   */
  List<AiAnswer> getAnswersByProject(Long projectId) throws AiAnswerNotFoundException;
}
