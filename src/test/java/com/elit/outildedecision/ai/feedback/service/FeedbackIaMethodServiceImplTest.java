package com.elit.outildedecision.ai.feedback.service;

import com.elit.outildedecision.ai.feedback.dto.FeedbackMethodRequest;
import com.elit.outildedecision.ai.feedback.entity.FeedbackIaMethod;
import com.elit.outildedecision.ai.feedback.mapper.FeedbackMapper;
import com.elit.outildedecision.ai.feedback.repository.FeedbackIaMethodRepository;
import com.elit.outildedecision.ai.feedback.service.implementation.FeedbackIaMethodServiceImpl;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.exception.BadRequestException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.project.repository.ProjectRepository;
import com.elit.outildedecision.shared.util.MethodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedbackIaMethodServiceImplTest {

  @Mock
  private FeedbackIaMethodRepository feedbackIaMethodRepository;
  @Mock
  private ProjectRepository projectRepository;
  @Mock
  private FeedbackMapper feedbackMapper;
  @InjectMocks
  private FeedbackIaMethodServiceImpl service;

  private Project project;

  @BeforeEach
  void setUp() {
    project = new Project();
    project.setProjectId(1L);
  }

  @Test
  void saveFeedback_ShouldCreate_WhenNoExisting() throws ProjectNotFoundException {
    FeedbackMethodRequest request = FeedbackMethodRequest.builder()
        .isLiked(true).feedbackAnswer("ok").projectId(1L).userId(2L).methodName(MethodType.VMODEL.toString()).build();

    when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
    when(feedbackIaMethodRepository.findByProject_ProjectIdAndUser_UserId(1L, 2L)).thenReturn(Optional.empty());

    String message = service.saveFeedback(request);

    assertThat(message).isEqualTo("Feedback enregistré avec succès.");
    verify(feedbackIaMethodRepository, times(1)).save(any(FeedbackIaMethod.class));
  }

  @Test
  void saveFeedback_ShouldUpdate_WhenExisting() throws ProjectNotFoundException {
    FeedbackMethodRequest request = FeedbackMethodRequest.builder()
        .isLiked(false).feedbackAnswer("updated").projectId(1L).userId(2L).methodName(MethodType.SCRUM.toString()).build();
    FeedbackIaMethod existing = new FeedbackIaMethod();
    existing.setId(5L);

    when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
    when(feedbackIaMethodRepository.findByProject_ProjectIdAndUser_UserId(1L, 2L)).thenReturn(Optional.of(existing));

    String message = service.saveFeedback(request);

    assertThat(message).isEqualTo("Feedback mis à jour avec succès.");
    verify(feedbackIaMethodRepository, times(1)).save(existing);
  }

  @Test
  void saveFeedback_ShouldThrowBadRequest_WhenProjectIdMissing() {
    FeedbackMethodRequest request = FeedbackMethodRequest.builder()
        .isLiked(true).feedbackAnswer("ok").userId(2L).methodName(MethodType.SCRUM.toString()).build();

    assertThatExceptionOfType(BadRequestException.class)
        .isThrownBy(() -> service.saveFeedback(request));
    verify(feedbackIaMethodRepository, never()).save(any());
  }

  @Test
  void saveFeedback_ShouldThrowProjectNotFound_WhenProjectMissing() {
    FeedbackMethodRequest request = FeedbackMethodRequest.builder()
        .isLiked(true).feedbackAnswer("ok").projectId(1L).userId(2L).methodName(MethodType.SCRUM.toString()).build();
    when(projectRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatExceptionOfType(ProjectNotFoundException.class)
        .isThrownBy(() -> service.saveFeedback(request));
    verify(feedbackIaMethodRepository, never()).save(any());
  }
}
