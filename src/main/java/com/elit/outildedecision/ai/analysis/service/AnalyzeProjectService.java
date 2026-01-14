package com.elit.outildedecision.ai.analysis.service;

import com.elit.outildedecision.ai.answer.dto.AiAnswerDTO;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.ResultAiNotFoundException;
import com.elit.outildedecision.exception.ResultAlreadyExistsException;

public interface AnalyzeProjectService {
  /**
   * Analyze project.
   *
   * @param aiAnswerDTO
   * @param projectId
   * @return status of analysis
   * @throws ResultAiNotFoundException
   * @throws ProjectNotFoundException
   * @throws ResultAlreadyExistsException
   */
  String analyzeProject(AiAnswerDTO aiAnswerDTO, long projectId) throws ResultAiNotFoundException, ProjectNotFoundException, ResultAlreadyExistsException;
}
