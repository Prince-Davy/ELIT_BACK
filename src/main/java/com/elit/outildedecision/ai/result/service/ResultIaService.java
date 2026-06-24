package com.elit.outildedecision.ai.result.service;

import com.elit.outildedecision.ai.answer.dto.AnswerRequest;
import com.elit.outildedecision.ai.answer.exception.AiAnswerNotFoundException;
import com.elit.outildedecision.ai.result.dto.RecommendationItemResponse;
import com.elit.outildedecision.ai.result.dto.ResultResponse;
import com.elit.outildedecision.ai.result.exception.ResultAiNotFoundException;
import com.elit.outildedecision.ai.result.exception.ResultAlreadyExistsException;
import com.elit.outildedecision.shared.exception.ResponseNotFoundException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;

import java.util.List;


/** Result ia service interface. */
public interface ResultIaService {

  /**
   * Send data to Ai and get result to save it on BDD.
   *
   * @param projectId   project ID
   * @param answerRequest answers IA
   * @return String result
   */
  String generateAndSaveResult(long projectId, AnswerRequest answerRequest) throws ProjectNotFoundException, ResultAlreadyExistsException, ResultAiNotFoundException;


  /**
   * Get result analyse IA for project id.
   *
   * @param projectId project id
   * @return ResultResponse
   * @throws ProjectNotFoundException if exception
   */
  ResultResponse getResultIa(long projectId) throws ProjectNotFoundException, ResponseNotFoundException;

  /**
   * Delete the AI analysis result of a project if present (used during project deletion cleanup).
   *
   * @param projectId project id
   */
  void deleteResultAnalysisAIByProjectId(long projectId);

  /**
   * Lookup the recommendation mapping for the project ID, assuming the user has completed AI anlysis survey and
   * PMM responded successfully.
   *
   * @param projectId the project id
   * @return the mapping
   */
  List<RecommendationItemResponse> getMapping(String projectId)
    throws AiAnswerNotFoundException, ResultAiNotFoundException;
}
