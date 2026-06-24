package com.elit.outildedecision.ai.methodology.evaluation.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.elit.outildedecision.ai.methodology.evaluation.api.dto.MethodologyEvaluationDto;
import com.elit.outildedecision.ai.methodology.evaluation.exception.UnsupportedModelTypeException;
import com.elit.outildedecision.ai.methodology.evaluation.service.MethodologyEvaluationService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class MethodologyEvaluationControllerTest {

  private static final Long PROJECT_ID = 1L;
  private static final int MAX_FACTORS = 5;

  @Mock
  private MethodologyEvaluationService methodologyEvaluationService;

  @InjectMocks
  private MethodologyEvaluationController controller;

  @Test
  void shouldReturnEvaluationsFromService() throws Exception {
    // given
    List<MethodologyEvaluationDto> evaluations =
        List.of(MethodologyEvaluationDto.builder().methodology("Scrum").build());
    when(methodologyEvaluationService.getMethodologyEvaluations(PROJECT_ID, MAX_FACTORS))
        .thenReturn(evaluations);

    // when
    ResponseEntity<List<MethodologyEvaluationDto>> response =
        controller.getMethodologyEvaluations(PROJECT_ID);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isSameAs(evaluations);
  }

  @Test
  void shouldReturnEmptyListWhenModelTypeUnsupported() throws Exception {
    // given
    when(methodologyEvaluationService.getMethodologyEvaluations(eq(PROJECT_ID), eq(MAX_FACTORS)))
        .thenThrow(new UnsupportedModelTypeException("Hybride is not supported yet"));

    // when
    ResponseEntity<List<MethodologyEvaluationDto>> response =
        controller.getMethodologyEvaluations(PROJECT_ID);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEmpty();
  }
}
