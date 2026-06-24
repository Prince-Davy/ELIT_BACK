package com.elit.outildedecision.ai.result.service.implementation;

import com.elit.outildedecision.ai.answer.dto.AnswerRequest;
import com.elit.outildedecision.ai.answer.entity.AiAnswer;
import com.elit.outildedecision.ai.answer.exception.AiAnswerNotFoundException;
import com.elit.outildedecision.ai.answer.repository.AiAnswerRepository;
import com.elit.outildedecision.ai.result.dto.AiPredictionResponse;
import com.elit.outildedecision.ai.result.dto.RecommendationItemResponse;
import com.elit.outildedecision.ai.result.dto.ResultResponse;
import com.elit.outildedecision.ai.result.entity.ResultAi;
import com.elit.outildedecision.ai.result.exception.ResultAiNotFoundException;
import com.elit.outildedecision.ai.result.exception.ResultAlreadyExistsException;
import com.elit.outildedecision.ai.result.mapper.ResultMapper;
import com.elit.outildedecision.ai.result.repository.ResultIaRepository;
import com.elit.outildedecision.ai.result.service.RecommendationService;
import com.elit.outildedecision.ai.result.service.ResultIaService;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.common.service.AiPredictionApiClientService;
import com.elit.outildedecision.shared.exception.BadRequestException;
import com.elit.outildedecision.shared.exception.ResponseNotFoundException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.project.repository.ProjectRepository;
import com.elit.outildedecision.shared.util.ProjectState;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static com.elit.outildedecision.shared.project.service.implementation.ProjectServiceImpl.PROJECT_NOT_FOUND;

/** Result ia service impl class. */
@Service
@Slf4j
public class ResultIaServiceImpl implements ResultIaService {

  private static final Duration AI_REQUEST_TIMEOUT = Duration.ofSeconds(30);

  private final ResultIaRepository resultIaRepository;
  private final ProjectRepository projectRepository;
  private final RecommendationService recommendationService;
  private final AiAnswerRepository aiAnswerRepository;
  private final AiPredictionApiClientService aiPredictionApiClientService;
  private final ResultMapper resultMapper;
  private final ObjectMapper objectMapper;

  @Value("${ia.prediction.service.endpoint}")
  private String iaPredictionServiceEndpoint;

  /**
   * ResultIa service implements.
   * @param aiPredictionApiClientService aiPredictionApiClientService
   * @param resultIaRepository resultIaRepository
   * @param projectRepository  projectRepository
   * @param recommendationService reccommandationService
   * @param aiAnswerRepository answerIaRepository
   * @param resultMapper resultMapper
   * @param objectMapper parses the AI prediction response payload
   */
  public ResultIaServiceImpl(final AiPredictionApiClientService aiPredictionApiClientService, final ResultIaRepository resultIaRepository, final ProjectRepository projectRepository,
                             final RecommendationService recommendationService, final AiAnswerRepository aiAnswerRepository, final ResultMapper resultMapper,
                             final ObjectMapper objectMapper) {
    this.aiPredictionApiClientService = aiPredictionApiClientService;
    this.resultIaRepository = resultIaRepository;
    this.projectRepository = projectRepository;
    this.recommendationService = recommendationService;
    this.aiAnswerRepository = aiAnswerRepository;
    this.resultMapper = resultMapper;
    this.objectMapper = objectMapper;
  }

  /**
   * Send data to AI and get prediction result to save it in the database.
   *
   * @param projectId   ID of the project
   * @param answerRequest AI answers to be sent
   * @return the AI service response
   * @throws ProjectNotFoundException     if the project does not exist
   * @throws ResultAlreadyExistsException if a result already exists for the project
   * @throws ResultAiNotFoundException    if the AI service returns no response
   */
  @Override
  @Transactional
  public String generateAndSaveResult(final long projectId, final AnswerRequest answerRequest)
    throws ProjectNotFoundException, ResultAlreadyExistsException, ResultAiNotFoundException {

    log.info("Starting AI analysis for project ID: {}", projectId);

    Project project = projectRepository.findById(projectId)
      .orElseThrow(() -> new ProjectNotFoundException(PROJECT_NOT_FOUND));

    if (resultIaRepository.findByProject(project).isPresent()) {
      log.warn("AI result already exists for project ID: {}", projectId);
      throw new ResultAlreadyExistsException("AI analysis result already exists for this project.");
    }

    Map<String, Object> payload = Map.of("data", answerRequest.getData());
    log.debug("Sending payload to AI service for project ID: {}", projectId);

    String response;
    try {
      response = aiPredictionApiClientService
        .sendPostRequest(iaPredictionServiceEndpoint, payload)
        .block(AI_REQUEST_TIMEOUT);
    } catch (RuntimeException e) {
      log.error("Error calling external AI service for project ID: {}", projectId, e);
      throw new ResultAiNotFoundException("External AI service is unreachable at the moment. Please try again later.");
    }

    if (response == null) {
      log.error("No response received from AI service for project ID: {}", projectId);
      throw new ResultAiNotFoundException("Unable to contact AI service or received empty response.");
    }

    log.debug("Received response from AI service for project ID: {}", projectId);

    final AiPredictionResponse prediction;
    try {
      prediction = objectMapper.readValue(response, AiPredictionResponse.class);
    } catch (JsonProcessingException e) {
      log.error("Cannot parse AI service response for project ID: {}", projectId, e);
      throw new ResultAiNotFoundException("AI service returned an unexpected response format.");
    }

    ResultAi resultAi = new ResultAi();
    resultAi.setProject(project);
    resultAi.setPredictedMethod(prediction.getPredictedMethod());
    resultAi.setPredictedProba(prediction.getPredictedProba());
    resultIaRepository.save(resultAi);

    project.setProjectState(ProjectState.COMPLETED);
    projectRepository.save(project);

    log.info("AI result successfully saved for project ID: {}", projectId);

    return response;
  }


  /**
   * Get result analysis AI from project.
   *
   * @param projectId project id
   * @return ResultResponse
   * @throws ProjectNotFoundException  if project not found
   * @throws ResponseNotFoundException if project doesn't have an analysis IA result
   */
  @Override
  @Transactional(readOnly = true)
  public ResultResponse getResultIa(final long projectId) throws ProjectNotFoundException, ResponseNotFoundException {
    Project project = projectRepository.findById(projectId)
      .orElseThrow(() -> new ProjectNotFoundException(PROJECT_NOT_FOUND));
    ResultAi result = resultIaRepository.findByProject(project)
      .orElseThrow(() -> new ResponseNotFoundException("Analyse AI result not found for project ID: " + projectId));

    return resultMapper.toResponse(result);
  }

  /**
   * Delete the AI analysis result of a project if present (used during project deletion cleanup).
   *
   * @param projectId project id
   */
  @Override
  @Transactional
  public void deleteResultAnalysisAIByProjectId(final long projectId) {
    resultIaRepository.findByProject_ProjectId(projectId).ifPresentOrElse(
      result -> {
        resultIaRepository.delete(result);
        log.info("AI result deleted for project ID: {}", projectId);
      },
      () -> log.debug("No AI result to delete for project ID: {}", projectId));
  }

  /**
   * Gets the mapping for the given projectid, assuming the user has completed AI anlysis survey and AI responded
   * successfully.
   *
   * @param projectId the project id
   * @return the mapping
   */
  @Override
  @Transactional(readOnly = true)
  public List<RecommendationItemResponse> getMapping(final String projectId)
    throws AiAnswerNotFoundException, ResultAiNotFoundException {
    final Long projectIdValue;
    try {
      projectIdValue = Long.valueOf(projectId);
    } catch (NumberFormatException e) {
      throw new BadRequestException("Invalid project id: " + projectId, e);
    }

    List<AiAnswer> answerIas = aiAnswerRepository.findByProject_ProjectId(projectIdValue);
    if (answerIas == null || answerIas.isEmpty()) {
      throw new AiAnswerNotFoundException("No answer ai found for this projectId");
    }
    ResultAi resultAi = resultIaRepository.findByProject_ProjectId(projectIdValue)
      .orElseThrow(() -> new ResultAiNotFoundException("No result ai found for this projectId"));

    log.info("Building AI recommandation mapping for project ID: {}", projectIdValue);
    return recommendationService.getMappings(answerIas, resultAi);
  }

}
