package com.elit.outildedecision.ai.methodology.evaluation.service;

import com.elit.outildedecision.ai.answer.dto.AiAnswerDTO;
import com.elit.outildedecision.ai.client.predictrf.dto.PredictRfResponse;
import com.elit.outildedecision.ai.methodology.evaluation.exception.UnsupportedModelTypeException;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.exception.ResultAiNotFoundException;

public interface MethodologyEvaluationFetcherService {
  /**
   * fetchEvaluations.
   *
   * @param aiAnswerDTO the answer dto
   * @param project the project id
   * @return response
   * @throws ResultAiNotFoundException exception ResultAiNotFoundException
   * @throws UnsupportedModelTypeException exception UnsupportedModelTypeException
   */
  PredictRfResponse fetchEvaluations(AiAnswerDTO aiAnswerDTO, Project project) throws ResultAiNotFoundException, UnsupportedModelTypeException;
}
