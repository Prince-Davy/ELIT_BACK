package com.elit.outildedecision.ai.answer.controller;

import com.elit.outildedecision.ai.answer.dto.AiAnswerDTO;
import com.elit.outildedecision.ai.answer.exception.AiAnswerNotFoundException;
import com.elit.outildedecision.ai.answer.service.AiAnswerService;
import com.elit.outildedecision.common.dto.SimpleMessageDto;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.service.ProjectService;
import com.elit.outildedecision.util.FormProgressStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Collections;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
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

  @InjectMocks
  private AiAnswerController controller;

  @Test
  void saveAnswers_shouldReturnOkResponse() throws ProjectNotFoundException {
    // given
    Long projectId = 1L;
    Map<String, String> data = Map.of("q1", "a1");
    AiAnswerDTO dto = new AiAnswerDTO(data);

    // when
    ResponseEntity<SimpleMessageDto> response = controller.saveAnswers(projectId, dto);

    // then
    assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertEquals("AI answers saved successfully", response.getBody().getMessage());
    assertEquals(HttpStatus.OK.value(), response.getBody().getStatus());
    assertNotNull(response.getBody().getTimestamp());

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
    AiAnswerDTO dto = new AiAnswerDTO(data);

    doThrow(new ProjectNotFoundException("Projet introuvable"))
      .when(aiAnswerService).saveAnswers(projectId, data);

    assertThrows(ProjectNotFoundException.class, () -> controller.saveAnswers(projectId, dto));
    verify(aiAnswerService, times(1)).saveAnswers(projectId, data);
  }

  @Test
  void getAnswers_shouldReturnDto() throws Exception {
    Long projectId = 1L;
    when(aiAnswerService.getAnswersByProject(projectId)).thenReturn(Collections.emptyList());

    ResponseEntity<AiAnswerDTO> response = controller.getAnswers(projectId);

    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    verify(aiAnswerService, times(1)).getAnswersByProject(projectId);
  }

  @Test
  void getAnswers_shouldThrowAiAnswerNotFoundException() throws Exception {
    Long projectId = 42L;
    when(aiAnswerService.getAnswersByProject(projectId))
      .thenThrow(new AiAnswerNotFoundException("Aucune réponse IA trouvée"));

    assertThrows(AiAnswerNotFoundException.class, () -> controller.getAnswers(projectId));
    verify(aiAnswerService, times(1)).getAnswersByProject(projectId);
  }
}
