package com.elit.outildedecision.ai.methodology.evaluation.service.impl;

import com.elit.outildedecision.ai.answer.dto.AnswerRequest;
import com.elit.outildedecision.ai.answer.entity.AiAnswer;
import com.elit.outildedecision.ai.answer.service.AiAnswerService;
import com.elit.outildedecision.ai.client.predictrf.dto.PredictRfResponse;
import com.elit.outildedecision.ai.client.predictrf.mapper.MethodologyEvaluationExternalMapper;
import com.elit.outildedecision.ai.methodology.evaluation.api.dto.MethodologyEvaluationDto;
import com.elit.outildedecision.ai.methodology.evaluation.api.mapper.MethodologyEvaluationMapper;
import com.elit.outildedecision.ai.methodology.evaluation.calculate.MethodologyEvaluationSelector;
import com.elit.outildedecision.ai.methodology.evaluation.entity.MethodologyEvaluation;
import com.elit.outildedecision.ai.methodology.evaluation.exception.UnsupportedModelTypeException;
import com.elit.outildedecision.ai.methodology.evaluation.repository.MethodologyEvaluationRepository;
import com.elit.outildedecision.ai.methodology.evaluation.service.MethodologyEvaluationFetcherService;
import com.elit.outildedecision.ai.methodology.evaluation.service.MethodologyEvaluationService;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.ai.result.entity.ResultAi;
import com.elit.outildedecision.ai.result.exception.ResultAiNotFoundException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.project.repository.ProjectRepository;
import com.elit.outildedecision.ai.result.repository.ResultIaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.elit.outildedecision.shared.project.service.implementation.ProjectServiceImpl.PROJECT_NOT_FOUND;

/** Methodology evaluation service impl class. */
@Slf4j
@Service
@RequiredArgsConstructor
public class MethodologyEvaluationServiceImpl implements MethodologyEvaluationService {

  private final ProjectRepository projectRepository;
  private final MethodologyEvaluationRepository methodologyEvaluationRepository;
  private final MethodologyEvaluationFetcherService methodologyEvaluationFetcherService;
  private final MethodologyEvaluationExternalMapper methodologyEvaluationExternalMapper;
  private final AiAnswerService aiAnswerService;
  private final MethodologyEvaluationMapper methodologyEvaluationMapper;
  private final ResultIaRepository resultIaRepository;
  private final MethodologyEvaluationSelector methodologyEvaluationSelector;

  /**
   * Create the evaluations of different methodologies with AI.
   *
   * @param projectId   the ID of the project
   * @param answerRequest answers to the form for AI analysis.
   */
  // noRollbackFor: an unsupported model (e.g. Hybride) is a graceful no-op handled by the caller, not
  // a data error. Without this, throwing here would mark the shared transaction rollback-only and the
  // caller's commit would fail with UnexpectedRollbackException (500) despite catching the exception.
  @Override
  @Transactional(noRollbackFor = UnsupportedModelTypeException.class)
  public List<MethodologyEvaluation> createEvaluation(final AnswerRequest answerRequest, final long projectId) throws ResultAiNotFoundException, UnsupportedModelTypeException {
    log.info("Evaluate methodologies for project ID {}", projectId);

    Project project = projectRepository.findById(projectId)
      .orElseThrow(() -> new ProjectNotFoundException(PROJECT_NOT_FOUND));

    PredictRfResponse response = methodologyEvaluationFetcherService.fetchEvaluations(answerRequest, project);
    log.debug("Methodologies evaluated by AI : {}", response);

    log.info("Save evaluated methodologies in database");
    List<MethodologyEvaluation> methodologyEvaluations = response.getPredictions().stream()
      .map(dto -> {
          var methodologyEvaluation = methodologyEvaluationExternalMapper.toEntity(dto);
          methodologyEvaluation.setProject(project);
          return methodologyEvaluation;
        }
      )
      .toList();

    methodologyEvaluations = methodologyEvaluationRepository.saveAll(methodologyEvaluations);
    log.info("Number of methodology evaluations saved : {}", methodologyEvaluations.size());
    return methodologyEvaluations;
  }

  /**
   * Get 3 methodology evaluations with their explanatory factors.
   * Evaluations are sorted by decreasing score (done in AI back) and for each evaluation factors are also sorted by decreasing score.
   *
   * @param projectId  the ID of the project
   * @param maxFactors max number of factors in an evaluation
   */
  @Override
  @Transactional(readOnly = true)
  public List<MethodologyEvaluationDto> getMethodologyEvaluations(final Long projectId, final int maxFactors)
    throws UnsupportedModelTypeException {
    log.info("Service get Methodology evalutations for project ID: {}", projectId);

    Project project = projectRepository.findById(projectId)
      .orElseThrow(() -> new ProjectNotFoundException(PROJECT_NOT_FOUND));

    ResultAi resultAi = resultIaRepository.findByProject(project)
      .orElseThrow(() -> new ResultAiNotFoundException("Result AI not found for project id :" + project.getProjectId()));

    if ("Hybride".equals(resultAi.getPredictedMethod())) {
      throw new UnsupportedModelTypeException("Hybride projects do not have this functionality enabled yet");
    }

    log.info("Retrieve evaluations");
    List<MethodologyEvaluation> evaluations = methodologyEvaluationRepository.findByProject(project);
    log.info("Retrieved evaluations : {}", evaluations);

    log.info("Retrieve ai answers");
    List<AiAnswer> aiAnswers = aiAnswerService.getAnswersByProject(projectId);

    List<MethodologyEvaluationDto> evaluationDtos = evaluations.stream()
      .map(evaluation -> methodologyEvaluationMapper.toDto(evaluation, aiAnswers))
      .toList();

    return methodologyEvaluationSelector.selectTopEvaluations(evaluationDtos, maxFactors);
  }
}
