package com.elit.outildedecision.ai.methodology.evaluation.service;


import com.elit.outildedecision.ai.answer.dto.AiAnswerDTO;
import com.elit.outildedecision.ai.methodology.evaluation.api.dto.MethodologyEvaluationDto;
import com.elit.outildedecision.ai.methodology.evaluation.entity.MethodologyEvaluation;
import com.elit.outildedecision.ai.methodology.evaluation.exception.UnsupportedModelTypeException;
import com.elit.outildedecision.exception.ResultAiNotFoundException;

import java.util.List;

public interface MethodologyEvaluationService {
  /**
   * createEvaluation.
   *
   * @param aiAnswerDTO
   * @param projectId
   * @return evaluations
   * @throws ResultAiNotFoundException
   * @throws UnsupportedModelTypeException
   */
  List<MethodologyEvaluation> createEvaluation(AiAnswerDTO aiAnswerDTO, long projectId) throws ResultAiNotFoundException, UnsupportedModelTypeException;

  /**
   * getMethodologyEvaluations.
   *
   * @param projectId
   * @param maxFactors
   * @return evaluations.
   * @throws UnsupportedModelTypeException
   */
  List<MethodologyEvaluationDto> getMethodologyEvaluations(Long projectId, int maxFactors) throws UnsupportedModelTypeException;
}
