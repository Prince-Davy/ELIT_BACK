package com.elit.outildedecision.ai.feedback.controller;

import com.elit.outildedecision.ai.feedback.dto.FeedbackMethodRequest;
import com.elit.outildedecision.ai.feedback.service.FeedbackIaMethodService;
import com.elit.outildedecision.shared.common.dto.SimpleMessageDto;
import com.elit.outildedecision.shared.exception.handler.RestResponseEntityExceptionHandler;
import com.elit.outildedecision.shared.util.MethodType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FeedbackIaMethodControllerTest {

  @Mock
  private FeedbackIaMethodService feedbackIaMethodService;

  @InjectMocks
  private FeedbackIaMethodController feedbackIaMethodController;

  private FeedbackMethodRequest request;
  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    request = FeedbackMethodRequest.builder()
        .isLiked(true).feedbackAnswer("ok").projectId(1L).userId(2L).methodName(MethodType.SCRUM.toString()).build();
    mockMvc = MockMvcBuilders.standaloneSetup(feedbackIaMethodController)
        .setControllerAdvice(RestResponseEntityExceptionHandler.class)
        .build();
    objectMapper = new ObjectMapper();
  }

  @Test
  void contextLoad() {
    assertThat(feedbackIaMethodController).isNotNull();
  }

  @Test
  void shouldSaveFeedback() throws Exception {
    when(feedbackIaMethodService.saveFeedback(request)).thenReturn("Feedback enregistré avec succès.");

    SimpleMessageDto result = feedbackIaMethodController.saveFeedback(request, 1L);

    assertThat(result.getStatus()).isEqualTo(200);
    assertThat(result.getMessage()).isEqualTo("Feedback enregistré avec succès.");
    verify(feedbackIaMethodService, times(1)).saveFeedback(request);
  }

  @Test
  void shouldAcceptFeedbackWith255Characters() throws Exception {
    String feedbackAnswer = "a".repeat(255);
    FeedbackMethodRequest dtoWith255Chars = FeedbackMethodRequest.builder()
        .isLiked(true).feedbackAnswer(feedbackAnswer).projectId(1L).userId(2L)
        .methodName(MethodType.SCRUM.toString()).build();

    when(feedbackIaMethodService.saveFeedback(any(FeedbackMethodRequest.class)))
        .thenReturn("Feedback enregistré avec succès.");

    mockMvc.perform(post("/feedbacks-method/projectIa/method/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dtoWith255Chars)))
        .andExpect(status().isOk());
  }

  @Test
  void shouldRejectFeedbackWith256Characters() throws Exception {
    String feedbackAnswer = "a".repeat(256);
    FeedbackMethodRequest dtoWith256Chars = FeedbackMethodRequest.builder()
        .isLiked(true).feedbackAnswer(feedbackAnswer).projectId(1L).userId(2L)
        .methodName(MethodType.SCRUM.toString()).build();

    mockMvc.perform(post("/feedbacks-method/projectIa/method/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dtoWith256Chars)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldRejectFeedbackWithExcessiveLength() throws Exception {
    String feedbackAnswer = "a".repeat(500);
    FeedbackMethodRequest dtoWithExcessiveLength = FeedbackMethodRequest.builder()
        .isLiked(true).feedbackAnswer(feedbackAnswer).projectId(1L).userId(2L)
        .methodName(MethodType.SCRUM.toString()).build();

    mockMvc.perform(post("/feedbacks-method/projectIa/method/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dtoWithExcessiveLength)))
        .andExpect(status().isBadRequest());
  }
}
