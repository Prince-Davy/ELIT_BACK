package com.elit.outildedecision.ai.result.controller;

import com.elit.outildedecision.ai.answer.dto.AnswerRequest;
import com.elit.outildedecision.ai.result.dto.ResultResponse;
import com.elit.outildedecision.ai.result.service.ResultIaService;
import com.elit.outildedecision.shared.exception.ResponseNotFoundException;
import com.elit.outildedecision.shared.exception.handler.RestResponseEntityExceptionHandler;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class IaResultControllerTest {

  private static final String BASE_URI = "/result/ai";
  private static final long PROJECT_ID = 1L;
  private static final long RESULT_ID = 2L;

  @Mock
  private ResultIaService resultIaService;

  @InjectMocks
  private IaResultController iaResultController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  private ResultResponse resultResponse;

  @BeforeEach
  void setUp() {
    mockMvc = standaloneSetup(iaResultController)
      .setControllerAdvice(new RestResponseEntityExceptionHandler())
      .build();

    objectMapper = new ObjectMapper();

    resultResponse = new ResultResponse();
    resultResponse.setId(RESULT_ID);
    resultResponse.setPredictedMethod("Traditionnelle");
    resultResponse.setProjectId(PROJECT_ID);
    resultResponse.setPredictedProba(82.34);
  }

  @Test
  void saveAiResponse_shouldReturnOk_andDelegateToService_withCapturedDto() throws Exception {
    // Arrange
    Map<String, String> data = new HashMap<>();
    data.put("Question", "Response");
    AnswerRequest dto = new AnswerRequest();
    dto.setData(data);

    String json = objectMapper.writeValueAsString(dto);

    // Act
    mockMvc.perform(post(BASE_URI + "/" + PROJECT_ID)
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
      .andExpect(status().isOk());

    // Assert - délégation et contenu du DTO
    ArgumentCaptor<AnswerRequest> captor = ArgumentCaptor.forClass(AnswerRequest.class);
    verify(resultIaService, times(1)).generateAndSaveResult(eq(PROJECT_ID), captor.capture());
    verifyNoMoreInteractions(resultIaService);

    AnswerRequest captured = captor.getValue();
    assertThat(captured)
      .as("Le DTO capturé ne doit pas être null")
      .isNotNull();
    assertThat(captured.getData())
      .as("Le contenu du map doit être transmis tel quel au service")
      .isEqualTo(data);
  }


  @Test
  void saveAiResponse_shouldReturnNotFound_whenProjectNotFound() throws Exception {
    // Arrange
    AnswerRequest dto = new AnswerRequest();
    dto.setData(new HashMap<>());
    String json = objectMapper.writeValueAsString(dto);

    doThrow(new ProjectNotFoundException("project not found"))
      .when(resultIaService).generateAndSaveResult(eq(PROJECT_ID), any(AnswerRequest.class));

    // Act + Assert
    mockMvc.perform(post(BASE_URI + "/" + PROJECT_ID)
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
      .andExpect(status().isNotFound());
    verify(resultIaService).generateAndSaveResult(eq(PROJECT_ID), any(AnswerRequest.class));
  }

  @Test
  void getAnalyseIAResult_shouldReturnOkAndBody_whenServiceReturnsModel() throws Exception {
    when(resultIaService.getResultIa(PROJECT_ID)).thenReturn(resultResponse);

    mockMvc.perform(get(BASE_URI + "/" + PROJECT_ID)
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value((int) RESULT_ID))
      .andExpect(jsonPath("$.projectId").value((int) PROJECT_ID))
      .andExpect(jsonPath("$.predictedMethod").value("Traditionnelle"))
      .andExpect(jsonPath("$.predictedProba").value(82.34));

    verify(resultIaService).getResultIa(PROJECT_ID);
  }

  @Test
  void getAnalyseIAResult_shouldReturnNotFound_whenResponseNotFound() throws Exception {
    when(resultIaService.getResultIa(PROJECT_ID)).thenThrow(new ResponseNotFoundException("not found"));

    mockMvc.perform(get(BASE_URI + "/" + PROJECT_ID)
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound());
  }

  @Test
  void getAnalyseIAResult_shouldReturnNotFound_whenProjectNotFound() throws Exception {
    when(resultIaService.getResultIa(PROJECT_ID)).thenThrow(new ProjectNotFoundException("project not found"));

    mockMvc.perform(get(BASE_URI + "/" + PROJECT_ID)
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound());
  }
}
