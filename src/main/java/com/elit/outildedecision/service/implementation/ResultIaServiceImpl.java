package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.ai.answer.dto.AiAnswerDTO;
import com.elit.outildedecision.ai.answer.entity.AiAnswer;
import com.elit.outildedecision.ai.answer.exception.AiAnswerNotFoundException;
import com.elit.outildedecision.ai.answer.repository.AiAnswerRepository;
import com.elit.outildedecision.common.service.AiPredictionApiClientService;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.ResultAi;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.ResponseNotFoundException;
import com.elit.outildedecision.exception.ResultAiNotFoundException;
import com.elit.outildedecision.exception.ResultAlreadyExistsException;
import com.elit.outildedecision.model.RecommandationItemModel;
import com.elit.outildedecision.model.ResultAiModel;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.repository.ResultIaRepository;
import com.elit.outildedecision.service.RecommandationService;
import com.elit.outildedecision.service.ResultIaService;
import com.elit.outildedecision.util.ProjectState;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.elit.outildedecision.service.implementation.ProjectServiceImpl.PROJECT_NOT_FOUND;

@Service
@Slf4j
public class ResultIaServiceImpl implements ResultIaService {

  private final ResultIaRepository resultIaRepository;
  private final ProjectRepository projectRepository;
  private final RecommandationService recommandationService;
  private final AiAnswerRepository aiAnswerRepository;
  private final AiPredictionApiClientService aiPredictionApiClientService;

  @Value("${ia.prediction.service.endpoint}")
  private String iaPredictionServiceEndpoint;

  /**
   * ResultIa service implements.
   * @param aiPredictionApiClientService aiPredictionApiClientService
   * @param resultIaRepository resultIaRepository
   * @param projectRepository  projectRepository
   * @param recommandationService reccommandationService
   * @param aiAnswerRepository answerIaRepository
   */
  public ResultIaServiceImpl(final AiPredictionApiClientService aiPredictionApiClientService, final ResultIaRepository resultIaRepository, final ProjectRepository projectRepository,
                             final RecommandationService recommandationService, final AiAnswerRepository aiAnswerRepository) {
    this.aiPredictionApiClientService = aiPredictionApiClientService;
    this.resultIaRepository = resultIaRepository;
    this.projectRepository = projectRepository;
    this.recommandationService = recommandationService;
    this.aiAnswerRepository = aiAnswerRepository;
  }

  /**
   * Send data to AI and get prediction result to save it in the database.
   *
   * @param projectId   ID of the project
   * @param aiAnswerDTO AI answers to be sent
   * @return
   * @throws ProjectNotFoundException     if the project does not exist
   * @throws ResultAlreadyExistsException if a result already exists for the project
   * @throws ResultAiNotFoundException    if the AI service returns no response
   */
  @Override
  @Transactional
  public String generateAndSaveResult(final long projectId, final AiAnswerDTO aiAnswerDTO)
    throws ProjectNotFoundException, ResultAlreadyExistsException, ResultAiNotFoundException {

    log.info("Starting AI analysis for project ID: {}", projectId);

    Project project = projectRepository.findById(projectId)
      .orElseThrow(() -> new ProjectNotFoundException(PROJECT_NOT_FOUND));

    if (resultIaRepository.findByProject(project).isPresent()) {
      log.warn("AI result already exists for project ID: {}", projectId);
      throw new ResultAlreadyExistsException("AI analysis result already exists for this project.");
    }

    Map<String, Object> payload = Map.of("data", aiAnswerDTO.getData());
    log.debug("Payload sent to AI service: {}", payload);

    String response;
    try {
      response = aiPredictionApiClientService
        .sendPostRequest(iaPredictionServiceEndpoint, payload)
        .block();
    } catch (Exception e) {
      log.error("Error calling external AI service: {}", e.getMessage(), e);
      throw new ResultAiNotFoundException("External AI service is unreachable at the moment. Please try again later.");
    }

    if (response == null) {
      log.error("No response received from AI service for project ID: {}", projectId);
      throw new ResultAiNotFoundException("Unable to contact AI service or received empty response.");
    }

    log.debug("Raw response from AI service: {}", response);

    JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
    String predictedMethod = jsonObject.get("predicted_method").getAsString();
    double predictedProba = jsonObject.get("predicted_proba").getAsDouble();

    ResultAi resultAi = new ResultAi();
    resultAi.setProject(project);
    resultAi.setPredictedMethod(predictedMethod);
    resultAi.setPredictedProba(predictedProba);
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
   * @return ResultAiModel
   * @throws ProjectNotFoundException  if project not found
   * @throws ResponseNotFoundException if project doesn't have an analysis IA result
   */
  @Override
  public ResultAiModel getResultIa(final long projectId) throws ProjectNotFoundException, ResponseNotFoundException {
    Project project = projectRepository.findById(projectId)
      .orElseThrow(() -> new ProjectNotFoundException(PROJECT_NOT_FOUND));
    Optional<ResultAi> result = Optional.ofNullable(resultIaRepository.findByProject(project)
      .orElseThrow(() -> new ResponseNotFoundException("Analyse AI result not found for project ID: " + projectId)));
    ResultAiModel resultAnalyseIa = new ResultAiModel();
    resultAnalyseIa.setId(result.get().getId());
    resultAnalyseIa.setProjectId(result.get().getId());
    resultAnalyseIa.setPredictedProba(result.get().getPredictedProba());
    resultAnalyseIa.setPredictedMethod(result.get().getPredictedMethod());
    resultAnalyseIa.setProjectId(result.get().getProject().getProjectId());

    return resultAnalyseIa;
  }

  /**
   * Delete result of analysis AI by id.
   *
   * @param resultIaId id of result AI
   * @return ResponseEntity<String>
   * @throws ResponseNotFoundException if analyse AI result not found
   */
  @Override
  @Transactional
  public ResponseEntity<String> deleteResultAnalysisAI(final long resultIaId) throws ResponseNotFoundException {
    Optional<ResultAi> result = Optional.ofNullable(resultIaRepository.findById(resultIaId)
      .orElseThrow(() -> new ResponseNotFoundException("Analyse AI result not found for ID: " + resultIaId)));
    if (result.isPresent()) {
      resultIaRepository.deleteById(resultIaId);
    }
    return ResponseEntity.ok().body("Le résultat de l'analyse IA pour ce projet a bien été supprimé");
  }

  /**
   * Delete result of analysis AI by project id.
   * @param projectId project id
   * @return ResponseEntity<String>
   */
  @Override
  public ResponseEntity<String> deleteResultAnalysisAIByProjectId(final long projectId) {
    try {
      Project project = projectRepository.findById(projectId)
        .orElseThrow(() -> new ResponseNotFoundException("Project not found for ID: " + projectId));
      ResultAi resultAi = resultIaRepository.findByProject(project)
        .orElseThrow(() -> new ResponseNotFoundException("Analyse AI result not found for project ID: " + projectId));
      resultIaRepository.delete(resultAi);
      return ResponseEntity.ok().body("Le résultat de l'analyse IA pour ce projet a bien été supprimé");
    } catch (ResponseNotFoundException e) {
      return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  /**
   * Gets the mapping for the given projectid, assuming the user has completed AI anlysis survey and AI responded
   * successfully.
   *
   * @param projectId the project id
   * @return the mapping
   */
  @Override
  public ResponseEntity<List<RecommandationItemModel>> getMapping(final String projectId)
    throws AiAnswerNotFoundException, ResultAiNotFoundException {
    Long projectID = Long.valueOf(projectId);

    List<AiAnswer> answerIas = aiAnswerRepository.findByProject_ProjectId(projectID);
    if (answerIas == null || answerIas.isEmpty()) {
      throw new AiAnswerNotFoundException("No answer ai found for this projectId");
    }
    ResultAi resultAi = resultIaRepository.findByProject_ProjectId(projectID)
      .orElseThrow(() -> new ResultAiNotFoundException("No result ai found for this projectId"));

    return recommandationService.getMappings(answerIas, resultAi);
  }

}
