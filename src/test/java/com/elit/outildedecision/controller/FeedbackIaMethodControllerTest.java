package com.elit.outildedecision.controller;

import com.elit.outildedecision.dto.FeedbackIaMethodCreateDto;
import com.elit.outildedecision.model.FeedBackIaMethodModel;
import com.elit.outildedecision.service.FeedbackIaMethodService;
import com.elit.outildedecision.util.MethodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("Test")
@RunWith(MockitoJUnitRunner.class)
class FeedbackIaMethodControllerTest {

  @Mock
  private FeedbackIaMethodService feedbackIaMethodService;

  @InjectMocks
  private FeedbackIaMethodController feedbackIaMethodController;

  private FeedbackIaMethodCreateDto dto;
  private FeedBackIaMethodModel model;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    dto = new FeedbackIaMethodCreateDto(true, "ok", 1L, 2L, MethodType.SCRUM.toString());
    model = new FeedBackIaMethodModel(true, "ok", 1L, 2L, MethodType.SCRUM.toString());
  }

  @Test
  void contextLoad() {
    assertNotNull(feedbackIaMethodController);
  }

  @Test
  void shouldSaveFeedback() throws Exception {
    Long ignoredProjectId = 1L;
    ResponseEntity<Object> expected = ResponseEntity.ok("Feedback enregistré avec succès.");
    when(feedbackIaMethodService.saveFeedback(dto)).thenReturn(expected);

    ResponseEntity<Object> response = feedbackIaMethodController.saveFeedback(dto, ignoredProjectId);

    assertThat(response.getStatusCodeValue()).isEqualTo(200);
    assertThat(response.getBody()).isEqualTo("Feedback enregistré avec succès.");
    verify(feedbackIaMethodService, times(1)).saveFeedback(dto);
  }

  @Test
  void shouldGetAllFeedbacks() {
    when(feedbackIaMethodService.getAllFeedbacks()).thenReturn(List.of(model));

    ResponseEntity<List<FeedBackIaMethodModel>> response = feedbackIaMethodController.getAllFeedbacks();

    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());
    verify(feedbackIaMethodService, times(1)).getAllFeedbacks();
  }
}
