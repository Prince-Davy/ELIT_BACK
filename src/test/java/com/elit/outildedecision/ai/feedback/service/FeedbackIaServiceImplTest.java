package com.elit.outildedecision.ai.feedback.service;

import com.elit.outildedecision.ai.feedback.dto.FeedbackRequest;
import com.elit.outildedecision.ai.feedback.dto.FeedbackResponse;
import com.elit.outildedecision.ai.feedback.entity.FeedbackIa;
import com.elit.outildedecision.ai.feedback.exception.FeedBackNotFoundException;
import com.elit.outildedecision.ai.feedback.mapper.FeedbackMapper;
import com.elit.outildedecision.ai.feedback.repository.FeedbackIaRepository;
import com.elit.outildedecision.ai.feedback.service.implementation.FeedbackIaServiceImpl;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.exception.ConflictException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.project.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedbackIaServiceImplTest {

  @Mock
  private FeedbackIaRepository feedbackIaRepository;
  @Mock
  private ProjectRepository projectRepository;
  @Mock
  private FeedbackMapper feedbackMapper;
  @InjectMocks
  private FeedbackIaServiceImpl feedbackIaService;

  private Project project;
  private User user;
  private FeedbackIa feedbackIa;
  private FeedbackRequest request;

  @BeforeEach
  void setUp() {
    project = new Project();
    project.setProjectId(1L);
    user = new User();
    user.setUserId(1L);

    feedbackIa = new FeedbackIa();
    feedbackIa.setId(1L);
    feedbackIa.setProject(project);
    feedbackIa.setUser(user);
    feedbackIa.setFeedbackAnswer("Test Feedback");
    feedbackIa.setMethodology("Agile");
    feedbackIa.setIsLiked(true);

    request = FeedbackRequest.builder()
        .id(1L).isLiked(true).methodology("Agile").feedbackAnswer("Test Feedback")
        .project(project).user(user).build();
  }

  @Test
  void saveFeedback_ShouldSaveFeedback_WhenNoConflict() throws ProjectNotFoundException {
    when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
    when(feedbackIaRepository.findByProject_ProjectIdAndUser_UserId(1L, 1L)).thenReturn(Optional.empty());
    when(feedbackMapper.toEntity(request)).thenReturn(feedbackIa);

    String message = feedbackIaService.saveFeedback(request);

    assertThat(message).isEqualTo("Feedback enregistré avec succès.");
    verify(feedbackIaRepository, times(1)).save(feedbackIa);
  }

  @Test
  void saveFeedback_ShouldThrowConflict_WhenFeedbackExists() {
    when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
    when(feedbackIaRepository.findByProject_ProjectIdAndUser_UserId(1L, 1L)).thenReturn(Optional.of(feedbackIa));

    assertThatExceptionOfType(ConflictException.class)
        .isThrownBy(() -> feedbackIaService.saveFeedback(request))
        .withMessage("Vous avez déjà un feedback pour ce projet!");
    verify(feedbackIaRepository, never()).save(any());
  }

  @Test
  void saveFeedback_ShouldThrowProjectNotFound_WhenProjectDoesNotExist() {
    when(projectRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatExceptionOfType(ProjectNotFoundException.class)
        .isThrownBy(() -> feedbackIaService.saveFeedback(request));
    verify(feedbackIaRepository, never()).save(any());
  }

  @Test
  void getFeedbackByProjectIdAndUserId_ShouldReturnFeedback() throws FeedBackNotFoundException {
    FeedbackResponse expected = FeedbackResponse.builder()
        .isLiked(true).methodology("Agile").feedbackAnswer("Test Feedback").projectId(1L).userId(1L).build();
    when(feedbackIaRepository.findByProject_ProjectIdAndUser_UserId(1L, 1L)).thenReturn(Optional.of(feedbackIa));
    when(feedbackMapper.toResponse(feedbackIa)).thenReturn(expected);

    FeedbackResponse result = feedbackIaService.getFeedbackByProjectIdAndUserId(1L, 1L);

    assertThat(result).isNotNull();
    assertThat(result.getFeedbackAnswer()).isEqualTo("Test Feedback");
  }

  @Test
  void getFeedbackByProjectIdAndUserId_ShouldThrow_WhenMissing() {
    when(feedbackIaRepository.findByProject_ProjectIdAndUser_UserId(1L, 1L)).thenReturn(Optional.empty());

    assertThatExceptionOfType(FeedBackNotFoundException.class)
        .isThrownBy(() -> feedbackIaService.getFeedbackByProjectIdAndUserId(1L, 1L))
        .withMessage("Ce Feedback n'existe pas !");
  }

  @Test
  void deleteFeedbackByUserAndProject_ShouldDelete_WhenExists() {
    when(feedbackIaRepository.existsByProject_ProjectIdAndUser_UserId(1L, 1L)).thenReturn(true);

    feedbackIaService.deleteFeedbackByUserAndProject(1L, 1L);

    verify(feedbackIaRepository, times(1)).deleteByProject_ProjectIdAndUser_UserId(1L, 1L);
  }

  @Test
  void deleteFeedbackByUserAndProject_ShouldThrowNotFound_WhenMissing() {
    when(feedbackIaRepository.existsByProject_ProjectIdAndUser_UserId(1L, 1L)).thenReturn(false);

    assertThatExceptionOfType(FeedBackNotFoundException.class)
        .isThrownBy(() -> feedbackIaService.deleteFeedbackByUserAndProject(1L, 1L));
    verify(feedbackIaRepository, never()).deleteByProject_ProjectIdAndUser_UserId(any(), any());
  }

  @Test
  void deleteFeedbacksByProjectId_ShouldDeleteAllFeedbacks() {
    List<FeedbackIa> feedbacks = List.of(feedbackIa);
    when(feedbackIaRepository.findByProject_ProjectId(1L)).thenReturn(feedbacks);

    feedbackIaService.deleteFeedbacksByProjectId(1L);

    verify(feedbackIaRepository, times(1)).deleteAll(feedbacks);
  }

  @Test
  void deleteFeedbacksByProjectId_ShouldDoNothing_WhenEmpty() {
    when(feedbackIaRepository.findByProject_ProjectId(1L)).thenReturn(List.of());

    feedbackIaService.deleteFeedbacksByProjectId(1L);

    verify(feedbackIaRepository, never()).deleteAll(any());
  }
}
