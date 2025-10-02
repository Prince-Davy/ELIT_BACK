package com.elit.outildedecision.ai.methodology.evaluation.service.impl;

import com.elit.outildedecision.ai.answer.dto.AiAnswerDTO;
import com.elit.outildedecision.ai.client.predictrf.PredictRfApiClient;
import com.elit.outildedecision.ai.client.predictrf.dto.PredictRfRequest;
import com.elit.outildedecision.ai.client.predictrf.dto.PredictRfResponse;
import com.elit.outildedecision.ai.methodology.evaluation.exception.UnsupportedModelTypeException;
import com.elit.outildedecision.ai.methodology.evaluation.service.MethodologyEvaluationFetcherService;
import com.elit.outildedecision.ai.methodology.evaluation.service.ModelType;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.ResultAi;
import com.elit.outildedecision.exception.ResultAiNotFoundException;
import com.elit.outildedecision.repository.ResultIaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MethodologyEvaluationFetcherServiceImpl implements MethodologyEvaluationFetcherService {

  private final PredictRfApiClient predictRfApiClient;
  private final ResultIaRepository resultIaRepository;

  /**
   * Fetch Evaluations from AI API.
   *
   * @param aiAnswerDTO aiAnswerDTO
   * @param project     project
   * @return response
   * @throws ResultAiNotFoundException     ResultAiNotFoundException
   * @throws UnsupportedModelTypeException UnsupportedModelTypeException
   */
  public PredictRfResponse fetchEvaluations(final AiAnswerDTO aiAnswerDTO, final Project project) throws ResultAiNotFoundException, UnsupportedModelTypeException {
    ResultAi resultAi = resultIaRepository.findByProject(project)
      .orElseThrow(() -> new ResultAiNotFoundException("Result AI not found for project id :" + project.getProjectId()));

    ModelType modelType = ModelType.fromPredictedMethod(resultAi.getPredictedMethod());

    PredictRfRequest request = PredictRfRequest.builder()
      .data(aiAnswerDTO.getData())
      .modelType(modelType)
      .build();

    return predictRfApiClient.predict(request);
  }
}
