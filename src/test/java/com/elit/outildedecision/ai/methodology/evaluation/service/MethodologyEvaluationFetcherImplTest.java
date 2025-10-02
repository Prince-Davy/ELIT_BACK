package com.elit.outildedecision.ai.methodology.evaluation.service;

import com.elit.outildedecision.ai.answer.dto.AiAnswerDTO;
import com.elit.outildedecision.ai.client.predictrf.PredictRfApiClient;
import com.elit.outildedecision.ai.client.predictrf.dto.MethodologyEvaluationExternalDto;
import com.elit.outildedecision.ai.client.predictrf.dto.PredictRfRequest;
import com.elit.outildedecision.ai.client.predictrf.dto.PredictRfResponse;
import com.elit.outildedecision.ai.methodology.evaluation.service.impl.MethodologyEvaluationFetcherServiceImpl;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.ResultAi;
import com.elit.outildedecision.repository.ResultIaRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class MethodologyEvaluationFetcherImplTest {
  @Mock
  PredictRfApiClient predictRfApiClient;

  @Mock
  ResultIaRepository resultIaRepository;

  @InjectMocks
  MethodologyEvaluationFetcherServiceImpl methodologyEvaluationFetcherService;

  @SneakyThrows
  @Test
  void testFetch() {
    // Arrange
    AiAnswerDTO aiAnswerDTO = new AiAnswerDTO();
    aiAnswerDTO.setData(Map.of("feature1", "123"));

    Project project = Project.builder().projectId(1L).build();

    ResultAi resultAi = new ResultAi();
    resultAi.setPredictedMethod("Agile");

    Mockito.when(resultIaRepository.findByProject(project))
      .thenReturn(Optional.of(resultAi));

    MethodologyEvaluationExternalDto dto = MethodologyEvaluationExternalDto.builder()
      .predictedMethodology("kanban")
      .predictedScore(10.1)
      .featureImportante(Map.of("feature1", 23.1))
      .build();

    PredictRfResponse expectedResponse = new PredictRfResponse(List.of(dto));

    Mockito.when(predictRfApiClient.predict(any(PredictRfRequest.class)))
      .thenReturn(expectedResponse);

    // Act
    PredictRfResponse result = methodologyEvaluationFetcherService.fetchEvaluations(aiAnswerDTO, project);

    // Assert
    assertEquals(expectedResponse, result);

    ArgumentCaptor<PredictRfRequest> captor = ArgumentCaptor.forClass(PredictRfRequest.class);
    Mockito.verify(predictRfApiClient).predict(captor.capture());

    PredictRfRequest capturedRequest = captor.getValue();
    assertEquals(aiAnswerDTO.getData(), capturedRequest.getData());
    assertEquals(ModelType.AGILE, capturedRequest.getModelType());
  }
}
