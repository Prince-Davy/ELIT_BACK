package com.elit.outildedecision.ai.analysis.service.implementation;

import com.elit.outildedecision.ai.analysis.service.AnalyzeProjectService;
import com.elit.outildedecision.ai.answer.dto.AnswerRequest;
import com.elit.outildedecision.ai.answer.service.AiAnswerService;
import com.elit.outildedecision.ai.methodology.evaluation.exception.UnsupportedModelTypeException;
import com.elit.outildedecision.ai.methodology.evaluation.service.MethodologyEvaluationService;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.ai.result.exception.ResultAiNotFoundException;
import com.elit.outildedecision.ai.result.exception.ResultAlreadyExistsException;
import com.elit.outildedecision.shared.project.service.ProjectService;
import com.elit.outildedecision.ai.result.service.ResultIaService;
import com.elit.outildedecision.shared.util.FormProgressStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Analyze project service impl class. */
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
   * @param answerRequest the AI questionnaire answers
   * @param projectId   projectId
   * @return status
   * @throws ResultAiNotFoundException     ResultAiNotFoundException
   * @throws ProjectNotFoundException      ProjectNotFoundException
   * @throws ResultAlreadyExistsException  ResultAlreadyExistsException
   * @throws UnsupportedModelTypeException UnsupportedModelTypeException
   */
  @Transactional(rollbackFor = {ResultAiNotFoundException.class, ProjectNotFoundException.class, ResultAlreadyExistsException.class})
  @Override
  public String analyzeProject(final AnswerRequest answerRequest, final long projectId) throws ResultAiNotFoundException, ProjectNotFoundException, ResultAlreadyExistsException {
    log.info("Starting AI analysis for project ID: {}", projectId);
    aiAnswerService.saveAnswers(projectId, answerRequest.getData());
    log.info("AI answers successfully saved for project ID: {}", projectId);
    projectService.updateFormTypeStatus(projectId, FormProgressStatus.IA);
    resultIaService.generateAndSaveResult(projectId, answerRequest);

    // Call methodology evaluation but do NOT fail the whole AI analysis if it is not supported for the
    // predicted model (e.g. Hybride projects). createEvaluation is annotated noRollbackFor that
    // exception so throwing it does not poison this shared transaction; we simply catch it and let the
    // analysis commit normally.
    try {
      methodologyEvaluationService.createEvaluation(answerRequest, projectId);
    } catch (UnsupportedModelTypeException e) {
      log.warn("Methodology evaluation not supported for project {}: {}. Continuing AI analysis.", projectId, e.getMessage());
    }
    log.info("Project successfully analyzed with AI service.");
    return "Project successfully analyzed with AI service.";
  }
}
