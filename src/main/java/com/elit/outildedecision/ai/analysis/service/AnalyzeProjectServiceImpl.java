package com.elit.outildedecision.ai.analysis.service;

import com.elit.outildedecision.ai.answer.dto.AiAnswerDTO;
import com.elit.outildedecision.ai.answer.service.AiAnswerService;
import com.elit.outildedecision.ai.methodology.evaluation.exception.UnsupportedModelTypeException;
import com.elit.outildedecision.ai.methodology.evaluation.service.MethodologyEvaluationService;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.ResultAiNotFoundException;
import com.elit.outildedecision.exception.ResultAlreadyExistsException;
import com.elit.outildedecision.service.ProjectService;
import com.elit.outildedecision.service.ResultIaService;
import com.elit.outildedecision.util.FormProgressStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyzeProjectServiceImpl implements AnalyzeProjectService {
  private final ResultIaService resultIaService;
  private final MethodologyEvaluationService methodologyEvaluationService;
  private final AiAnswerService aiAnswerService;
  private final ProjectService projectService;

  /**
   * Analyze project with AI.
   *
   * @param aiAnswerDTO aiAnswerDTO
   * @param projectId   projectId
   * @return status
   * @throws ResultAiNotFoundException     ResultAiNotFoundException
   * @throws ProjectNotFoundException      ProjectNotFoundException
   * @throws ResultAlreadyExistsException  ResultAlreadyExistsException
   * @throws UnsupportedModelTypeException UnsupportedModelTypeException
   */
  @Transactional(rollbackFor = {ResultAiNotFoundException.class, ProjectNotFoundException.class, ResultAlreadyExistsException.class})
  @Override
  public String analyzeProject(final AiAnswerDTO aiAnswerDTO, final long projectId) throws ResultAiNotFoundException, ProjectNotFoundException, ResultAlreadyExistsException, UnsupportedModelTypeException {
    log.info("Starting AI analysis for project ID: {}", projectId);
    aiAnswerService.saveAnswers(projectId, aiAnswerDTO.getData());
    log.info("AI answers successfully saved for project ID: {}", projectId);
    projectService.updateFormTypeStatus(projectId, FormProgressStatus.IA);
    resultIaService.generateAndSaveResult(projectId, aiAnswerDTO);
    methodologyEvaluationService.createEvaluation(aiAnswerDTO, projectId);
    log.info("Project successfully analyzed with AI service.");
    return "Project successfully analyzed with AI service.";
  }
}
