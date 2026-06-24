package com.elit.outildedecision.ai.answer.controller;

import com.elit.outildedecision.ai.answer.dto.AnswerRequest;
import com.elit.outildedecision.ai.answer.dto.AnswerResponse;
import com.elit.outildedecision.ai.answer.entity.AiAnswer;
import com.elit.outildedecision.ai.answer.exception.AiAnswerNotFoundException;
import com.elit.outildedecision.ai.answer.mapper.AnswerMapper;
import com.elit.outildedecision.ai.answer.service.AiAnswerService;
import com.elit.outildedecision.shared.common.dto.SimpleMessageDto;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.project.service.ProjectService;
import com.elit.outildedecision.shared.util.FormProgressStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiAnswerControllerTest {

  @Mock
  private AiAnswerService aiAnswerService;

  @Mock
  private ProjectService projectService;

  @Mock
  private AnswerMapper answerMapper;

  @InjectMocks
  private AiAnswerController controller;

  @Test
  void saveAnswers_shouldReturnCreatedResponse() throws ProjectNotFoundException {
    // given
    Long projectId = 1L;
    Map<String, String> data = Map.of("q1", "a1");
    AnswerRequest request = new AnswerRequest(data);

    // when
    ResponseEntity<SimpleMessageDto> response = controller.saveAnswers(projectId, request);

    // then
    assertThat(response.getStatusCodeValue()).isEqualTo(HttpStatus.CREATED.value());
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getMessage()).isEqualTo("AI answers saved successfully");
    assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.CREATED.value());
    assertThat(response.getBody().getTimestamp()).isNotNull();

    verify(aiAnswerService, times(1))
      .saveAnswers(eq(projectId), argThat(m -> m.equals(data)));

    verify(projectService, times(1))
      .updateFormTypeStatus(eq(projectId), eq(FormProgressStatus.IA));

    verifyNoMoreInteractions(aiAnswerService, projectService);
  }


  @Test
  void saveAnswers_shouldThrowProjectNotFoundException() throws Exception {
    Long projectId = 99L;
    Map<String, String> data = Map.of("qX", "aX");
    AnswerRequest request = new AnswerRequest(data);

    doThrow(new ProjectNotFoundException("Projet introuvable"))
      .when(aiAnswerService).saveAnswers(projectId, data);

    assertThatExceptionOfType(ProjectNotFoundException.class)
        .isThrownBy(() -> controller.saveAnswers(projectId, request));
    verify(aiAnswerService, times(1)).saveAnswers(projectId, data);
  }

  @Test
  void getAnswers_shouldReturnDto() throws Exception {
    Long projectId = 1L;
    List<AiAnswer> answers = Collections.emptyList();
    when(aiAnswerService.getAnswersByProject(projectId)).thenReturn(answers);
    when(answerMapper.toResponse(answers))
      .thenReturn(AnswerResponse.builder().data(Collections.emptyMap()).build());

    ResponseEntity<AnswerResponse> response = controller.getAnswers(projectId);

    assertThat(response.getStatusCodeValue()).isEqualTo(200);
    assertThat(response.getBody()).isNotNull();
    verify(aiAnswerService, times(1)).getAnswersByProject(projectId);
    verify(answerMapper, times(1)).toResponse(answers);
  }

  @Test
  void getAnswers_shouldThrowAiAnswerNotFoundException() throws Exception {
    Long projectId = 42L;
    when(aiAnswerService.getAnswersByProject(projectId))
      .thenThrow(new AiAnswerNotFoundException("Aucune réponse IA trouvée"));

    assertThatExceptionOfType(AiAnswerNotFoundException.class)
        .isThrownBy(() -> controller.getAnswers(projectId));
    verify(aiAnswerService, times(1)).getAnswersByProject(projectId);
  }
}
