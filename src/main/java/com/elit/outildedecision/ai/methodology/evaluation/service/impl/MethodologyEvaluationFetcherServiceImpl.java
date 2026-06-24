package com.elit.outildedecision.ai.methodology.evaluation.service.impl;

import com.elit.outildedecision.ai.answer.dto.AnswerRequest;
import com.elit.outildedecision.ai.client.predictrf.PredictRfApiClient;
import com.elit.outildedecision.ai.client.predictrf.dto.PredictRfRequest;
import com.elit.outildedecision.ai.client.predictrf.dto.PredictRfResponse;
import com.elit.outildedecision.ai.methodology.evaluation.exception.UnsupportedModelTypeException;
import com.elit.outildedecision.ai.methodology.evaluation.service.MethodologyEvaluationFetcherService;
import com.elit.outildedecision.ai.methodology.evaluation.service.ModelType;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.ai.result.entity.ResultAi;
import com.elit.outildedecision.ai.result.exception.ResultAiNotFoundException;
import com.elit.outildedecision.ai.result.repository.ResultIaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Methodology evaluation fetcher service impl class. */
@Slf4j
@Service
@RequiredArgsConstructor
public class MethodologyEvaluationFetcherServiceImpl implements MethodologyEvaluationFetcherService {

  private final PredictRfApiClient predictRfApiClient;
  private final ResultIaRepository resultIaRepository;

  /**
   * Fetch Evaluations from AI API.
   *
   * @param answerRequest answerRequest
   * @param project     project
   * @return response
   * @throws ResultAiNotFoundException     ResultAiNotFoundException
   * @throws UnsupportedModelTypeException UnsupportedModelTypeException
   */
  public PredictRfResponse fetchEvaluations(final AnswerRequest answerRequest, final Project project) throws ResultAiNotFoundException, UnsupportedModelTypeException {
    ResultAi resultAi = resultIaRepository.findByProject(project)
      .orElseThrow(() -> new ResultAiNotFoundException("Result AI not found for project id :" + project.getProjectId()));

    ModelType modelType = ModelType.fromPredictedMethod(resultAi.getPredictedMethod());

    PredictRfRequest request = PredictRfRequest.builder()
      .data(answerRequest.getData())
      .modelType(modelType)
      .build();

    return predictRfApiClient.predict(request);
  }
}
