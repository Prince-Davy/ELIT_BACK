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
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

  private AiAnswerDTO dto;

  @BeforeEach
  void setup() {
    dto = new AiAnswerDTO();
    dto.setData(Map.of());
  }

  @SneakyThrows
  @Test
  void testAnalyzeProject_ThrowsResultAiNotFoundException() {
    long projectId = 1L;

    doThrow(new ResultAiNotFoundException("AI result not found"))
      .when(resultIaService).generateAndSaveResult(eq(projectId), any());

    assertThrows(ResultAiNotFoundException.class, () ->
      analyzeProjectService.analyzeProject(dto, projectId));
  }

  @SneakyThrows
  @Test
  void testAnalyzeProject_ThrowsProjectNotFoundException() {
    long projectId = 1L;

    doThrow(new ProjectNotFoundException("Project not found"))
      .when(projectService).updateFormTypeStatus(eq(projectId), any());

    assertThrows(ProjectNotFoundException.class, () ->
      analyzeProjectService.analyzeProject(dto, projectId));
  }

  @SneakyThrows
  @Test
  void testAnalyzeProject_ThrowsResultAlreadyExistsException() {
    long projectId = 1L;

    doThrow(new ResultAlreadyExistsException("Result already exists"))
      .when(resultIaService).generateAndSaveResult(eq(projectId), any());

    assertThrows(ResultAlreadyExistsException.class, () ->
      analyzeProjectService.analyzeProject(dto, projectId));
  }

  @Test
  void testAnalyzeProject_Success() throws Exception {
    long projectId = 1L;

    String result = analyzeProjectService.analyzeProject(dto, projectId);

    assertEquals("Project successfully analyzed with AI service.", result);
    verify(aiAnswerService).saveAnswers(projectId, dto.getData());
    verify(projectService).updateFormTypeStatus(projectId, FormProgressStatus.IA);
    verify(resultIaService).generateAndSaveResult(projectId, dto);
    verify(methodologyEvaluationService).createEvaluation(dto, projectId);
  }
}
