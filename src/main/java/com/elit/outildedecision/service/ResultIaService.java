package com.elit.outildedecision.service;

import com.elit.outildedecision.ai.answer.dto.AiAnswerDTO;
import com.elit.outildedecision.ai.answer.exception.AiAnswerNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.ResponseNotFoundException;
import com.elit.outildedecision.exception.ResultAiNotFoundException;
import com.elit.outildedecision.exception.ResultAlreadyExistsException;
import com.elit.outildedecision.model.RecommandationItemModel;
import com.elit.outildedecision.model.ResultAiModel;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface ResultIaService {

  /**
   * Send data to Ai and get result to save it on BDD.
   *
   * @param projectId   project ID
   * @param aiAnswerDTO answers IA
   * @return String result
   */
  String generateAndSaveResult(long projectId, AiAnswerDTO aiAnswerDTO) throws ProjectNotFoundException, ResultAlreadyExistsException, ResultAiNotFoundException;


  /**
   * Get result analyse IA for project id.
   *
   * @param projectId project id
   * @return Optional<ResultAi>
   * @throws ProjectNotFoundException if exception
   */
  ResultAiModel getResultIa(long projectId) throws ProjectNotFoundException, ResponseNotFoundException;

  /**
   * Delete result of analysis AI by id.
   *
   * @param resultIaId id of result AI
   * @return ResponseEntity<String>
   * @throws ResponseNotFoundException if exception
   */
  ResponseEntity<String> deleteResultAnalysisAI(long resultIaId) throws ResponseNotFoundException;

  /**
   * Delete result of analysis AI by project id.
   * @param projectId project id
   * @return ResponseEntity<String>
   */
  ResponseEntity<String> deleteResultAnalysisAIByProjectId(long projectId);

  /**
   * Lookup the recommandation mapping for the project ID, assuming the user has completed AI anlysis survey and
   * PMM responded successfully.
   *
   * @param projectId the project id
   * @return the mapping
   */
  ResponseEntity<List<RecommandationItemModel>> getMapping(String projectId)
    throws AiAnswerNotFoundException, ResultAiNotFoundException;
}
