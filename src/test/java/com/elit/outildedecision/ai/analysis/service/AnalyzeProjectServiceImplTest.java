package com.elit.outildedecision.ai.analysis.service;

import com.elit.outildedecision.ai.analysis.service.implementation.AnalyzeProjectServiceImpl;
import com.elit.outildedecision.ai.answer.dto.AnswerRequest;
import com.elit.outildedecision.ai.answer.service.AiAnswerService;
import com.elit.outildedecision.ai.methodology.evaluation.service.MethodologyEvaluationService;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.ai.result.exception.ResultAiNotFoundException;
import com.elit.outildedecision.ai.result.exception.ResultAlreadyExistsException;
import com.elit.outildedecision.shared.project.service.ProjectService;
import com.elit.outildedecision.ai.result.service.ResultIaService;
import com.elit.outildedecision.shared.util.FormProgressStatus;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AnalyzeProjectServiceImplTest {

  @Mock
  private ResultIaService resultIaService;

  @Mock
  private MethodologyEvaluationService methodologyEvaluationService;

  @Mock
  private AiAnswerService aiAnswerService;

  @Mock
  private ProjectService projectService;

  @InjectMocks
  private AnalyzeProjectServiceImpl analyzeProjectService;

  private AnswerRequest dto;

  @BeforeEach
  void setup() {
    dto = new AnswerRequest();
    dto.setData(Map.of());
  }

  @SneakyThrows
  @Test
  void testAnalyzeProject_ThrowsResultAiNotFoundException() {
    long projectId = 1L;

    doThrow(new ResultAiNotFoundException("AI result not found"))
      .when(resultIaService).generateAndSaveResult(eq(projectId), any());

    assertThatExceptionOfType(ResultAiNotFoundException.class)
        .isThrownBy(() -> analyzeProjectService.analyzeProject(dto, projectId));
  }

  @SneakyThrows
  @Test
  void testAnalyzeProject_ThrowsProjectNotFoundException() {
    long projectId = 1L;

    doThrow(new ProjectNotFoundException("Project not found"))
      .when(projectService).updateFormTypeStatus(eq(projectId), any());

    assertThatExceptionOfType(ProjectNotFoundException.class)
        .isThrownBy(() -> analyzeProjectService.analyzeProject(dto, projectId));
  }

  @SneakyThrows
  @Test
  void testAnalyzeProject_ThrowsResultAlreadyExistsException() {
    long projectId = 1L;

    doThrow(new ResultAlreadyExistsException("Result already exists"))
      .when(resultIaService).generateAndSaveResult(eq(projectId), any());

    assertThatExceptionOfType(ResultAlreadyExistsException.class)
        .isThrownBy(() -> analyzeProjectService.analyzeProject(dto, projectId));
  }

  @Test
  void testAnalyzeProject_Success() throws Exception {
    long projectId = 1L;

    String result = analyzeProjectService.analyzeProject(dto, projectId);

    assertThat(result).isEqualTo("Project successfully analyzed with AI service.");
    verify(aiAnswerService).saveAnswers(projectId, dto.getData());
    verify(projectService).updateFormTypeStatus(projectId, FormProgressStatus.IA);
    verify(resultIaService).generateAndSaveResult(projectId, dto);
    verify(methodologyEvaluationService).createEvaluation(dto, projectId);
  }
}
