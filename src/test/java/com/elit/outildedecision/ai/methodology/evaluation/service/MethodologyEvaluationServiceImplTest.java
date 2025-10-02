package com.elit.outildedecision.ai.methodology.evaluation.service;


import com.elit.outildedecision.ai.answer.dto.AiAnswerDTO;
import com.elit.outildedecision.ai.answer.entity.AiAnswer;
import com.elit.outildedecision.ai.answer.service.AiAnswerService;
import com.elit.outildedecision.ai.client.predictrf.dto.MethodologyEvaluationExternalDto;
import com.elit.outildedecision.ai.client.predictrf.dto.PredictRfResponse;
import com.elit.outildedecision.ai.client.predictrf.mapper.MethodologyEvaluationExternalMapper;
import com.elit.outildedecision.ai.methodology.evaluation.api.dto.MethodologyEvaluationDto;
import com.elit.outildedecision.ai.methodology.evaluation.api.mapper.MethodologyEvaluationMapper;
import com.elit.outildedecision.ai.methodology.evaluation.entity.MethodologyEvaluation;
import com.elit.outildedecision.ai.methodology.evaluation.repository.MethodologyEvaluationRepository;
import com.elit.outildedecision.ai.methodology.evaluation.service.impl.MethodologyEvaluationServiceImpl;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.ResultAi;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.repository.ResultIaRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MethodologyEvaluationServiceImplTest {

  @Mock
  private ProjectRepository projectRepository;

  @Mock
  private MethodologyEvaluationRepository methodologyEvaluationRepository;

  @Mock
  private MethodologyEvaluationFetcherService methodologyEvaluationFetcherService;

  @Mock
  private AiAnswerService aiAnswerService;

  @Mock
  private ResultIaRepository resultIaRepository;

  private MethodologyEvaluationExternalMapper methodologyEvaluationExternalMapper = new MethodologyEvaluationExternalMapper();

  private MethodologyEvaluationMapper methodologyEvaluationMapper = new MethodologyEvaluationMapper();

  @InjectMocks
  private MethodologyEvaluationServiceImpl service;

  @BeforeEach
  void init() {
    service = new MethodologyEvaluationServiceImpl(projectRepository, methodologyEvaluationRepository, methodologyEvaluationFetcherService, methodologyEvaluationExternalMapper, aiAnswerService, methodologyEvaluationMapper, resultIaRepository);
  }

  @SneakyThrows
  @Test
  void testCreateEvaluation() {
    // Arrange
    AiAnswerDTO aiAnswerDTO = new AiAnswerDTO();
    aiAnswerDTO.setData(Map.of("feature1", "123"));

    long projectId = 1L;
    Project project = Project.builder().projectId(1L).build();

    ResultAi resultAi = new ResultAi();
    resultAi.setPredictedMethod("Agile");

    MethodologyEvaluationExternalDto dto = MethodologyEvaluationExternalDto.builder()
      .predictedMethodology("kanban")
      .predictedScore(10.1)
      .featureImportante(Map.of("feature1", 23.1))
      .build();

    PredictRfResponse mockResponse = new PredictRfResponse(List.of(dto));

    // mocks
    when(projectRepository.findById(projectId))
      .thenReturn(Optional.of(project));

    when(methodologyEvaluationFetcherService.fetchEvaluations(aiAnswerDTO, project))
      .thenReturn(mockResponse);

    when(methodologyEvaluationRepository.saveAll(anyList()))
      .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    List<MethodologyEvaluation> result = service.createEvaluation(aiAnswerDTO, projectId);

    // Assert
    assertEquals(1, result.size());
    assertEquals("kanban", result.get(0).getMethodology());
    assertEquals(10.1, result.get(0).getScore());
    assertEquals(project, result.get(0).getProject());
  }


  @SneakyThrows
  @Test
  void shouldSortAndLimitTopFactors() {
    // Arrange
    long projectId = 1L;
    int maxFactors = 2;

    Project project = Project.builder()
      .projectId(projectId)
      .name("P1")
      .build();

    // Map question -> score (non trié)
    Map<String, Double> qScores = Map.of(
      "Q1", 0.10,
      "Q2", 0.70,
      "Q3", 0.30
    );

    MethodologyEvaluation evalScrum = MethodologyEvaluation.builder()
      .project(project)
      .methodology("scrum")
      .score(0.90)
      .questionScores(qScores)
      .build();

    MethodologyEvaluation evalSafe = MethodologyEvaluation.builder()
      .project(project)
      .methodology("safe")
      .score(0.80)
      .questionScores(qScores)
      .build();


    // Réponses IA pour enrichir les facteurs (le mapper pourra les utiliser)
    List<AiAnswer> aiAnswers = List.of(
      buildAiAnswer("Q1", "CatA", "Ans1", project),
      buildAiAnswer("Q2", "CatB", "Ans2", project),
      buildAiAnswer("Q3", "CatC", "Ans3", project)
    );

    ResultAi resultAi = new ResultAi(1L, "Agile", 1.0, project);

    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    when(methodologyEvaluationRepository.findByProject(project)).thenReturn(List.of(evalScrum, evalSafe));
    when(aiAnswerService.getAnswersByProject(projectId)).thenReturn(aiAnswers);
    when(resultIaRepository.findByProject(project)).thenReturn(Optional.of(resultAi));

    // Act
    List<MethodologyEvaluationDto> result = service.getMethodologyEvaluations(projectId, maxFactors);

    // Assert
    assertThat(result, hasSize(2));
    MethodologyEvaluationDto dto = result.get(0);

    Map<String, MethodologyEvaluationDto> byMethodology = result.stream()
      .collect(java.util.stream.Collectors.toMap(
        MethodologyEvaluationDto::getMethodology,
        java.util.function.Function.identity()
      ));

    MethodologyEvaluationDto scrumDto = byMethodology.get("scrum");
    assertNotNull(scrumDto);
    assertThat(scrumDto.getScore(), closeTo(0.90, 1e-9));
    assertNotNull(scrumDto.getExplanatoryFactorDto());
    assertThat(scrumDto.getExplanatoryFactorDto(), hasSize(2));
    assertThat(scrumDto.getExplanatoryFactorDto().get(0).getQuestionScore(), closeTo(0.70, 1e-9));
    assertThat(scrumDto.getExplanatoryFactorDto().get(1).getQuestionScore(), closeTo(0.30, 1e-9));

    MethodologyEvaluationDto safeDto = byMethodology.get("safe");
    assertNotNull(safeDto);
    assertThat(safeDto.getScore(), closeTo(0.80, 1e-9));
    assertNotNull(safeDto.getExplanatoryFactorDto());
    assertThat(safeDto.getExplanatoryFactorDto(), hasSize(2));
    assertThat(safeDto.getExplanatoryFactorDto().get(0).getQuestionScore(), closeTo(0.70, 1e-9));
    assertThat(safeDto.getExplanatoryFactorDto().get(1).getQuestionScore(), closeTo(0.30, 1e-9));
  }


  private AiAnswer buildAiAnswer(String question, String category, String answer, Project project) {
    AiAnswer a = new AiAnswer();
    a.setQuestion(question);
    a.setCategory(category);
    a.setAnswer(answer);
    a.setProject(project);
    return a;
  }

  @SneakyThrows
  @Test
  void shouldReturnEmptyListWhenNoEvaluations() {
    // Arrange
    long projectId = 3L;
    Project project = Project.builder().projectId(projectId).name("P3").build();
    ResultAi resultAi = new ResultAi(1L, "Agile", 1.0, project);

    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    when(methodologyEvaluationRepository.findByProject(project)).thenReturn(List.of());
    when(aiAnswerService.getAnswersByProject(projectId)).thenReturn(List.of());
    when(resultIaRepository.findByProject(project)).thenReturn(Optional.of(resultAi));

    // Act
    List<MethodologyEvaluationDto> result = service.getMethodologyEvaluations(projectId, 3);

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
}
