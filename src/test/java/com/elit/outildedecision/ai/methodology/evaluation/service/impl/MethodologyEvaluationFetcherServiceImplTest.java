package com.elit.outildedecision.ai.methodology.evaluation.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.elit.outildedecision.ai.answer.dto.AnswerRequest;
import com.elit.outildedecision.ai.client.predictrf.PredictRfApiClient;
import com.elit.outildedecision.ai.client.predictrf.dto.PredictRfRequest;
import com.elit.outildedecision.ai.client.predictrf.dto.PredictRfResponse;
import com.elit.outildedecision.ai.methodology.evaluation.exception.UnsupportedModelTypeException;
import com.elit.outildedecision.ai.methodology.evaluation.service.ModelType;
import com.elit.outildedecision.ai.result.entity.ResultAi;
import com.elit.outildedecision.ai.result.exception.ResultAiNotFoundException;
import com.elit.outildedecision.ai.result.repository.ResultIaRepository;
import com.elit.outildedecision.shared.kernel.entity.Project;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MethodologyEvaluationFetcherServiceImplTest {

  @Mock
  private PredictRfApiClient predictRfApiClient;

  @Mock
  private ResultIaRepository resultIaRepository;

  @InjectMocks
  private MethodologyEvaluationFetcherServiceImpl fetcherService;

  @Test
  void shouldThrowResultAiNotFoundWhenNoResultForProject() {
    // given
    Project project = project(1L);
    AnswerRequest answerRequest = answerRequest();
    when(resultIaRepository.findByProject(project)).thenReturn(Optional.empty());

    // when / then
    assertThatExceptionOfType(ResultAiNotFoundException.class)
        .isThrownBy(() -> fetcherService.fetchEvaluations(answerRequest, project));
    verify(predictRfApiClient, never()).predict(any());
  }

  @Test
  void shouldThrowIllegalArgumentWhenPredictedMethodIsNull() {
    // given
    Project project = project(1L);
    AnswerRequest answerRequest = answerRequest();
    when(resultIaRepository.findByProject(project)).thenReturn(Optional.of(resultAi(null)));

    // when / then
    assertThatIllegalArgumentException()
        .isThrownBy(() -> fetcherService.fetchEvaluations(answerRequest, project));
    verify(predictRfApiClient, never()).predict(any());
  }

  @Test
  void shouldThrowUnsupportedWhenPredictedMethodIsHybride() {
    // given
    Project project = project(1L);
    AnswerRequest answerRequest = answerRequest();
    when(resultIaRepository.findByProject(project)).thenReturn(Optional.of(resultAi("Hybride")));

    // when / then
    assertThatExceptionOfType(UnsupportedModelTypeException.class)
        .isThrownBy(() -> fetcherService.fetchEvaluations(answerRequest, project));
    verify(predictRfApiClient, never()).predict(any());
  }

  @Test
  void shouldBuildRequestWithAgileModelTypeAndReturnClientResponse() throws Exception {
    // given
    Project project = project(1L);
    AnswerRequest answerRequest = answerRequest();
    PredictRfResponse expected = PredictRfResponse.builder().build();
    when(resultIaRepository.findByProject(project)).thenReturn(Optional.of(resultAi("Agile")));
    when(predictRfApiClient.predict(any())).thenReturn(expected);

    // when
    PredictRfResponse response = fetcherService.fetchEvaluations(answerRequest, project);

    // then
    assertThat(response).isSameAs(expected);
    ArgumentCaptor<PredictRfRequest> captor = ArgumentCaptor.forClass(PredictRfRequest.class);
    verify(predictRfApiClient).predict(captor.capture());
    assertThat(captor.getValue().getModelType()).isEqualTo(ModelType.AGILE);
    assertThat(captor.getValue().getData()).isEqualTo(answerRequest.getData());
  }

  @Test
  void shouldBuildRequestWithTraditionalModelType() throws Exception {
    // given
    Project project = project(1L);
    AnswerRequest answerRequest = answerRequest();
    when(resultIaRepository.findByProject(project)).thenReturn(Optional.of(resultAi("Traditionnelle")));
    when(predictRfApiClient.predict(any())).thenReturn(PredictRfResponse.builder().build());

    // when
    fetcherService.fetchEvaluations(answerRequest, project);

    // then
    ArgumentCaptor<PredictRfRequest> captor = ArgumentCaptor.forClass(PredictRfRequest.class);
    verify(predictRfApiClient).predict(captor.capture());
    assertThat(captor.getValue().getModelType()).isEqualTo(ModelType.TRADITIONAL);
  }

  private Project project(final long id) {
    return Project.builder().projectId(id).build();
  }

  private AnswerRequest answerRequest() {
    return AnswerRequest.builder().data(Map.of("Q1", "A1")).build();
  }

  private ResultAi resultAi(final String predictedMethod) {
    ResultAi resultAi = new ResultAi();
    resultAi.setPredictedMethod(predictedMethod);
    return resultAi;
  }
}
