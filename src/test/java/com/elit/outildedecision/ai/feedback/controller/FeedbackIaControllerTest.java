package com.elit.outildedecision.ai.feedback.controller;

import com.elit.outildedecision.ai.feedback.dto.FeedbackRequest;
import com.elit.outildedecision.ai.feedback.dto.FeedbackResponse;
import com.elit.outildedecision.ai.feedback.exception.FeedBackNotFoundException;
import com.elit.outildedecision.ai.feedback.service.FeedbackIaService;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.common.dto.SimpleMessageDto;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedbackIaControllerTest {

  @Mock
  private FeedbackIaService feedbackIaService;
  @InjectMocks
  private FeedbackIaController feedbackIaController;

  private FeedbackRequest request;
  private FeedbackResponse response;

  @BeforeEach
  void setUp() {
    Project project = new Project();
    project.setProjectId(1L);
    User user = new User();
    user.setUserId(1L);
    request = FeedbackRequest.builder()
        .id(1L).isLiked(true).methodology("Agile").feedbackAnswer("Great project")
        .project(project).user(user).build();
    response = FeedbackResponse.builder()
        .isLiked(true).methodology("Agile").feedbackAnswer("Great project").projectId(1L).userId(1L).build();
  }

  @Test
  void contextLoad() {
    assertThat(feedbackIaController).isNotNull();
  }

  @Test
  void shouldSaveFeedback() throws ProjectNotFoundException {
    when(feedbackIaService.saveFeedback(request)).thenReturn("Feedback enregistré avec succès.");

    SimpleMessageDto result = feedbackIaController.saveFeedback(request, 1L);

    assertThat(result.getStatus()).isEqualTo(200);
    assertThat(result.getMessage()).isEqualTo("Feedback enregistré avec succès.");
    verify(feedbackIaService, times(1)).saveFeedback(request);
  }

  @Test
  void shouldGetFeedbackByProjectIdAndUserId() throws FeedBackNotFoundException {
    when(feedbackIaService.getFeedbackByProjectIdAndUserId(1L, 1L)).thenReturn(response);

    FeedbackResponse result = feedbackIaController.getFeedbackByProjectIdAndUserId(1L, 1L);

    assertThat(result).isEqualTo(response);
    verify(feedbackIaService, times(1)).getFeedbackByProjectIdAndUserId(1L, 1L);
  }

  @Test
  void shouldDeleteFeedbackByUserAndProject() {
    SimpleMessageDto result = feedbackIaController.deleteFeedbackByUserAndProject(1L, 1L);

    assertThat(result.getStatus()).isEqualTo(200);
    assertThat(result.getMessage()).isEqualTo("Le FeedBack a bien été supprimé");
    verify(feedbackIaService, times(1)).deleteFeedbackByUserAndProject(1L, 1L);
  }
}
