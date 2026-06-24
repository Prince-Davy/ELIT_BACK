package com.elit.outildedecision.ai.methodology.evaluation.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.elit.outildedecision.ai.answer.dto.AnswerRequest;
import com.elit.outildedecision.ai.answer.entity.AiAnswer;
import com.elit.outildedecision.ai.answer.service.AiAnswerService;
import com.elit.outildedecision.ai.client.predictrf.dto.MethodologyEvaluationExternalDto;
import com.elit.outildedecision.ai.client.predictrf.dto.PredictRfResponse;
import com.elit.outildedecision.ai.client.predictrf.mapper.MethodologyEvaluationExternalMapper;
import com.elit.outildedecision.ai.methodology.evaluation.api.dto.MethodologyEvaluationDto;
import com.elit.outildedecision.ai.methodology.evaluation.api.mapper.MethodologyEvaluationMapper;
import com.elit.outildedecision.ai.methodology.evaluation.calculate.MethodologyEvaluationSelector;
import com.elit.outildedecision.ai.methodology.evaluation.entity.MethodologyEvaluation;
import com.elit.outildedecision.ai.methodology.evaluation.repository.MethodologyEvaluationRepository;
import com.elit.outildedecision.ai.methodology.evaluation.service.MethodologyEvaluationFetcherService;
import com.elit.outildedecision.ai.result.entity.ResultAi;
import com.elit.outildedecision.ai.result.exception.ResultAiNotFoundException;
import com.elit.outildedecision.ai.result.repository.ResultIaRepository;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.project.repository.ProjectRepository;
import com.elit.outildedecision.ai.methodology.evaluation.exception.UnsupportedModelTypeException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MethodologyEvaluationServiceImplTest {

  private static final long PROJECT_ID = 1L;

  @Mock
  private ProjectRepository projectRepository;
  @Mock
  private MethodologyEvaluationRepository methodologyEvaluationRepository;
  @Mock
  private MethodologyEvaluationFetcherService methodologyEvaluationFetcherService;
  @Mock
  private MethodologyEvaluationExternalMapper methodologyEvaluationExternalMapper;
  @Mock
  private AiAnswerService aiAnswerService;
  @Mock
  private MethodologyEvaluationMapper methodologyEvaluationMapper;
  @Mock
  private ResultIaRepository resultIaRepository;
  @Mock
  private MethodologyEvaluationSelector methodologyEvaluationSelector;

  @InjectMocks
  private MethodologyEvaluationServiceImpl service;

  // ---- createEvaluation ----

  @Test
  void shouldThrowProjectNotFoundWhenCreatingEvaluationForUnknownProject() {
    // given
    when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.empty());

    // when / then
    assertThatExceptionOfType(ProjectNotFoundException.class)
        .isThrownBy(() -> service.createEvaluation(answerRequest(), PROJECT_ID));
    verifyNoInteractions(methodologyEvaluationFetcherService);
    verify(methodologyEvaluationRepository, never()).saveAll(anyList());
  }

  @Test
  void shouldMapPredictionsAttachProjectAndSaveWhenCreatingEvaluation() throws Exception {
    // given
    Project project = project();
    AnswerRequest answerRequest = answerRequest();
    MethodologyEvaluationExternalDto dto1 = externalDto("Scrum", 0.9);
    MethodologyEvaluationExternalDto dto2 = externalDto("Kanban", 0.7);
    MethodologyEvaluation entity1 = MethodologyEvaluation.builder().methodology("Scrum").build();
    MethodologyEvaluation entity2 = MethodologyEvaluation.builder().methodology("Kanban").build();

    when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
    when(methodologyEvaluationFetcherService.fetchEvaluations(answerRequest, project))
        .thenReturn(PredictRfResponse.builder().predictions(List.of(dto1, dto2)).build());
    when(methodologyEvaluationExternalMapper.toEntity(dto1)).thenReturn(entity1);
    when(methodologyEvaluationExternalMapper.toEntity(dto2)).thenReturn(entity2);
    when(methodologyEvaluationRepository.saveAll(anyList())).thenReturn(List.of(entity1, entity2));

    // when
    List<MethodologyEvaluation> result = service.createEvaluation(answerRequest, PROJECT_ID);

    // then
    assertThat(result).containsExactly(entity1, entity2);
    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<MethodologyEvaluation>> captor = ArgumentCaptor.forClass(List.class);
    verify(methodologyEvaluationRepository).saveAll(captor.capture());
    assertThat(captor.getValue())
        .allSatisfy(evaluation -> assertThat(evaluation.getProject()).isSameAs(project));
  }

  // ---- getMethodologyEvaluations ----

  @Test
  void shouldThrowProjectNotFoundWhenGettingEvaluationsForUnknownProject() {
    // given
    when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.empty());

    // when / then
    assertThatExceptionOfType(ProjectNotFoundException.class)
        .isThrownBy(() -> service.getMethodologyEvaluations(PROJECT_ID, 5));
  }

  @Test
  void shouldThrowResultAiNotFoundWhenNoResultForProject() {
    // given
    Project project = project();
    when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
    when(resultIaRepository.findByProject(project)).thenReturn(Optional.empty());

    // when / then
    assertThatExceptionOfType(ResultAiNotFoundException.class)
        .isThrownBy(() -> service.getMethodologyEvaluations(PROJECT_ID, 5));
  }

  @Test
  void shouldThrowUnsupportedWhenPredictedMethodIsHybride() {
    // given
    Project project = project();
    when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
    when(resultIaRepository.findByProject(project)).thenReturn(Optional.of(resultAi("Hybride")));

    // when / then
    assertThatExceptionOfType(UnsupportedModelTypeException.class)
        .isThrownBy(() -> service.getMethodologyEvaluations(PROJECT_ID, 5));
    verify(methodologyEvaluationRepository, never()).findByProject(any());
    verifyNoInteractions(methodologyEvaluationMapper, methodologyEvaluationSelector);
  }

  @Test
  void shouldMapEvaluationsWithAnswersAndDelegateToSelector() throws Exception {
    // given
    Project project = project();
    MethodologyEvaluation eval1 = MethodologyEvaluation.builder().methodology("Scrum").build();
    MethodologyEvaluation eval2 = MethodologyEvaluation.builder().methodology("Kanban").build();
    List<AiAnswer> answers = List.of(AiAnswer.builder().question("Q1").answer("A1").build());
    MethodologyEvaluationDto dto1 = MethodologyEvaluationDto.builder().methodology("Scrum").build();
    MethodologyEvaluationDto dto2 = MethodologyEvaluationDto.builder().methodology("Kanban").build();
    List<MethodologyEvaluationDto> selected = List.of(dto1);

    when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
    when(resultIaRepository.findByProject(project)).thenReturn(Optional.of(resultAi("Agile")));
    when(methodologyEvaluationRepository.findByProject(project)).thenReturn(List.of(eval1, eval2));
    when(aiAnswerService.getAnswersByProject(PROJECT_ID)).thenReturn(answers);
    when(methodologyEvaluationMapper.toDto(eval1, answers)).thenReturn(dto1);
    when(methodologyEvaluationMapper.toDto(eval2, answers)).thenReturn(dto2);
    when(methodologyEvaluationSelector.selectTopEvaluations(anyList(), eq(5))).thenReturn(selected);

    // when
    List<MethodologyEvaluationDto> result = service.getMethodologyEvaluations(PROJECT_ID, 5);

    // then
    assertThat(result).isSameAs(selected);
    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<MethodologyEvaluationDto>> captor = ArgumentCaptor.forClass(List.class);
    verify(methodologyEvaluationSelector).selectTopEvaluations(captor.capture(), eq(5));
    assertThat(captor.getValue()).containsExactly(dto1, dto2);
  }

  private Project project() {
    return Project.builder().projectId(PROJECT_ID).build();
  }

  private AnswerRequest answerRequest() {
    return AnswerRequest.builder().data(Map.of("Q1", "A1")).build();
  }

  private ResultAi resultAi(final String predictedMethod) {
    ResultAi resultAi = new ResultAi();
    resultAi.setPredictedMethod(predictedMethod);
    return resultAi;
  }

  private MethodologyEvaluationExternalDto externalDto(final String methodology, final double score) {
    return MethodologyEvaluationExternalDto.builder()
        .predictedMethodology(methodology)
        .predictedScore(score)
        .build();
  }
}
