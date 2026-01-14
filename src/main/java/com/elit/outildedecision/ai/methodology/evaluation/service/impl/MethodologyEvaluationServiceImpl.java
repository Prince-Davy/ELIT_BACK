package com.elit.outildedecision.ai.methodology.evaluation.service.impl;

import com.elit.outildedecision.ai.answer.dto.AiAnswerDTO;
import com.elit.outildedecision.ai.answer.entity.AiAnswer;
import com.elit.outildedecision.ai.answer.service.AiAnswerService;
import com.elit.outildedecision.ai.client.predictrf.dto.PredictRfResponse;
import com.elit.outildedecision.ai.client.predictrf.mapper.MethodologyEvaluationExternalMapper;
import com.elit.outildedecision.ai.methodology.evaluation.api.dto.ExplanatoryFactorDto;
import com.elit.outildedecision.ai.methodology.evaluation.api.dto.MethodologyEvaluationDto;
import com.elit.outildedecision.ai.methodology.evaluation.api.mapper.MethodologyEvaluationMapper;
import com.elit.outildedecision.ai.methodology.evaluation.entity.MethodologyEvaluation;
import com.elit.outildedecision.ai.methodology.evaluation.exception.UnsupportedModelTypeException;
import com.elit.outildedecision.ai.methodology.evaluation.repository.MethodologyEvaluationRepository;
import com.elit.outildedecision.ai.methodology.evaluation.service.MethodologyEvaluationFetcherService;
import com.elit.outildedecision.ai.methodology.evaluation.service.MethodologyEvaluationService;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.ResultAi;
import com.elit.outildedecision.exception.ResultAiNotFoundException;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.repository.ResultIaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

  /**
   * Create the evaluations of different methodologies with AI.
   *
   * @param projectId   the ID of the project
   * @param aiAnswerDTO answers to the form for AI analysis.
   */
  @Override
  public List<MethodologyEvaluation> createEvaluation(final AiAnswerDTO aiAnswerDTO, final long projectId) throws ResultAiNotFoundException, UnsupportedModelTypeException {
    log.info("Evaluate methodologies for project ID {}", projectId);

    Project project = projectRepository.findById(projectId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found with ID: " + projectId));

    PredictRfResponse response = methodologyEvaluationFetcherService.fetchEvaluations(aiAnswerDTO, project);
    log.debug("Methodologies evaluated by AI : {}", response);

    log.info("Save evaluated methodologies in database");
    List<MethodologyEvaluation> methodologyEvaluations = response.getPredictions().stream()
      .map(dto -> {
          var methodologyEvaluation = methodologyEvaluationExternalMapper.toEntity(dto);
          methodologyEvaluation.setProject(project);
          return methodologyEvaluation;
        }
      )
      .collect(Collectors.toList());

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
  public List<MethodologyEvaluationDto> getMethodologyEvaluations(final Long projectId, final int maxFactors)
    throws UnsupportedModelTypeException, ResponseStatusException {
    log.info("Service get Methodology evalutations for project ID: {}", projectId);

    Project project = projectRepository.findById(projectId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found with ID: " + projectId));

    ResultAi resultAi = resultIaRepository.findByProject(project)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Result AI not found for project id :" + project.getProjectId()));

    if ("Hybride".equals(resultAi.getPredictedMethod())) {
      throw new UnsupportedModelTypeException("Hybride projects do not have this functionality enabled yet");
    }

    log.info("Retrieve evaluations");
    List<MethodologyEvaluation> evaluations = methodologyEvaluationRepository.findByProject(project);
    log.info("Retrieved evaluations : {}", evaluations);

    log.info("Retrieve ai answers");
    List<AiAnswer> aiAnswers = aiAnswerService.getAnswersByProject(projectId);

    return evaluations.stream()
      .map(evaluation -> {
        MethodologyEvaluationDto allFactors = methodologyEvaluationMapper.toDto(evaluation, aiAnswers);

        List<ExplanatoryFactorDto> topFactors = allFactors.getExplanatoryFactorDto().stream()
          .sorted(Comparator.comparing(ExplanatoryFactorDto::getQuestionScore).reversed())
          .limit(maxFactors)
          .collect(Collectors.toList());

        return MethodologyEvaluationDto.builder()
          .methodology(evaluation.getMethodology())
          .score(evaluation.getScore())
          .explanatoryFactorDto(topFactors)
          .build();
      })
      .limit(3)
      .collect(Collectors.toList());
  }
}
