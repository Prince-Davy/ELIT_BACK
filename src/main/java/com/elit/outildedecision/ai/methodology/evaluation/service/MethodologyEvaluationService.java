package com.elit.outildedecision.ai.methodology.evaluation.service;


import com.elit.outildedecision.ai.answer.dto.AnswerRequest;
import com.elit.outildedecision.ai.methodology.evaluation.api.dto.MethodologyEvaluationDto;
import com.elit.outildedecision.ai.methodology.evaluation.entity.MethodologyEvaluation;
import com.elit.outildedecision.ai.methodology.evaluation.exception.UnsupportedModelTypeException;
import com.elit.outildedecision.ai.result.exception.ResultAiNotFoundException;

import java.util.List;

/** Methodology evaluation service interface. */
public interface MethodologyEvaluationService {
  /**
   * createEvaluation.
   *
   * @param answerRequest the answer dto
   * @param projectId the project id
   * @return evaluations
   * @throws ResultAiNotFoundException exception  ResultAiNotFoundException
   * @throws UnsupportedModelTypeException exception UnsupportedModelTypeException
   */
  List<MethodologyEvaluation> createEvaluation(AnswerRequest answerRequest, long projectId) throws ResultAiNotFoundException, UnsupportedModelTypeException;

  /**
   * getMethodologyEvaluations.
   *
   * @param projectId the project id
   * @param maxFactors the max factors
   * @return evaluations.
   * @throws UnsupportedModelTypeException exception UnsupportedModelTypeException
   */
  List<MethodologyEvaluationDto> getMethodologyEvaluations(Long projectId, int maxFactors) throws UnsupportedModelTypeException;
}
