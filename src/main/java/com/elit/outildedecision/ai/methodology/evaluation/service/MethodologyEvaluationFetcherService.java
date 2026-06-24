package com.elit.outildedecision.ai.methodology.evaluation.service;

import com.elit.outildedecision.ai.answer.dto.AnswerRequest;
import com.elit.outildedecision.ai.client.predictrf.dto.PredictRfResponse;
import com.elit.outildedecision.ai.methodology.evaluation.exception.UnsupportedModelTypeException;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.ai.result.exception.ResultAiNotFoundException;

/** Methodology evaluation fetcher service interface. */
public interface MethodologyEvaluationFetcherService {
  /**
   * fetchEvaluations.
   *
   * @param answerRequest the answer dto
   * @param project the project id
   * @return response
   * @throws ResultAiNotFoundException exception ResultAiNotFoundException
   * @throws UnsupportedModelTypeException exception UnsupportedModelTypeException
   */
  PredictRfResponse fetchEvaluations(AnswerRequest answerRequest, Project project) throws ResultAiNotFoundException, UnsupportedModelTypeException;
}
