package com.elit.outildedecision.ai.analysis.service;

import com.elit.outildedecision.ai.answer.dto.AnswerRequest;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.ai.result.exception.ResultAiNotFoundException;
import com.elit.outildedecision.ai.result.exception.ResultAlreadyExistsException;

/** Analyze project service interface. */
public interface AnalyzeProjectService {
  /**
   * Analyze project.
   *
   * @param answerRequest the AI questionnaire answers
   * @param projectId the project id
   * @return status of analysis
   * @throws ResultAiNotFoundException if the AI result cannot be found
   * @throws ProjectNotFoundException if the project does not exist
   * @throws ResultAlreadyExistsException if an AI result already exists for the project
   */
  String analyzeProject(AnswerRequest answerRequest, long projectId) throws ResultAiNotFoundException, ProjectNotFoundException, ResultAlreadyExistsException;
}
