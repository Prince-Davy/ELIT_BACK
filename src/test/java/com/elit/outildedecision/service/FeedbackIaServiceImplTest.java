package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.FeedbackIa;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.model.FeedBackIaModel;
import com.elit.outildedecision.repository.FeedbackIaRepository;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.service.implementation.FeedbackIaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FeedbackIaServiceImplTest {

  @Mock
  private FeedbackIaRepository feedbackIaRepository;

  @Mock
  private ProjectRepository projectRepository;

  @InjectMocks
  private FeedbackIaServiceImpl feedbackIaService;

  private FeedbackIa feedbackIa;
  private Project project;
  private User user;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // Mock entities
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
  }

  @Test
  void saveFeedback_ShouldSaveFeedback_WhenNoConflict() throws ProjectNotFoundException {
    when(projectRepository.findById(project.getProjectId())).thenReturn(Optional.of(project));
    when(feedbackIaRepository.findByProject_ProjectIdAndUser_UserId(project.getProjectId(), user.getUserId()))
      .thenReturn(null);

    ResponseEntity<Object> response = feedbackIaService.saveFeedback(feedbackIa);

    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertEquals("Feedback enregistré avec succès.", response.getBody());
    verify(feedbackIaRepository, times(1)).save(feedbackIa);
  }

  @Test
  void saveFeedback_ShouldReturnConflict_WhenFeedbackExists() throws ProjectNotFoundException {
    when(projectRepository.findById(project.getProjectId())).thenReturn(Optional.of(project));
    when(feedbackIaRepository.findByProject_ProjectIdAndUser_UserId(project.getProjectId(), user.getUserId()))
      .thenReturn(feedbackIa);

    ResponseEntity<Object> response = feedbackIaService.saveFeedback(feedbackIa);

    assertNotNull(response);
    assertEquals(409, response.getStatusCodeValue());
    assertEquals("Vous avez déjà un feedback pour ce projet!", response.getBody());
    verify(feedbackIaRepository, never()).save(any());
  }

  @Test
  void saveFeedback_ShouldThrowProjectNotFoundException_WhenProjectDoesNotExist() {
    when(projectRepository.findById(project.getProjectId())).thenReturn(Optional.empty());

    assertThrows(ProjectNotFoundException.class, () -> feedbackIaService.saveFeedback(feedbackIa));
    verify(feedbackIaRepository, never()).save(any());
  }

  @Test
  void getAllFeedbacks_ShouldReturnAllFeedbacks() {
    List<FeedbackIa> feedbacks = new ArrayList<>();
    feedbacks.add(feedbackIa);
    when(feedbackIaRepository.findAll()).thenReturn(feedbacks);

    List<FeedBackIaModel> feedbackModels = feedbackIaService.getAllFeedbacks();

    assertNotNull(feedbackModels);
    assertEquals(1, feedbackModels.size());
    assertEquals(feedbackIa.getFeedbackAnswer(), feedbackModels.get(0).getFeedbackAnswer());
  }

  @Test
  void getFeedbackByProjectIdAndUserId_ShouldReturnFeedback() {
    when(feedbackIaRepository.findByProject_ProjectIdAndUser_UserId(project.getProjectId(), user.getUserId()))
      .thenReturn(feedbackIa);

    FeedBackIaModel feedbackModel = feedbackIaService.getFeedbackByProjectIdAndUserId(project.getProjectId(), user.getUserId());

    assertNotNull(feedbackModel);
    assertEquals(feedbackIa.getFeedbackAnswer(), feedbackModel.getFeedbackAnswer());
  }

  @Test
  void getFeedbacksByProject_ShouldReturnFeedbacks() {
    List<FeedbackIa> feedbacks = new ArrayList<>();
    feedbacks.add(feedbackIa);
    when(feedbackIaRepository.findByProject_ProjectId(project.getProjectId())).thenReturn(feedbacks);

    List<FeedBackIaModel> feedbackModels = feedbackIaService.getFeedbacksByProject(project.getProjectId());

    assertNotNull(feedbackModels);
    assertEquals(1, feedbackModels.size());
    assertEquals(feedbackIa.getFeedbackAnswer(), feedbackModels.get(0).getFeedbackAnswer());
  }

  @Test
  void deleteFeedbackByUserAndProject_ShouldDeleteFeedback_WhenExists() {
    when(feedbackIaRepository.findByProject_ProjectIdAndUser_UserId(project.getProjectId(), user.getUserId()))
      .thenReturn(feedbackIa);

    ResponseEntity<Object> response = feedbackIaService.deleteFeedbackByUserAndProject(project.getProjectId(), user.getUserId());

    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertEquals("Le FeedBack a bien été supprimé", response.getBody());
    verify(feedbackIaRepository, times(1)).deleteByProject_ProjectIdAndUser_UserId(project.getProjectId(), user.getUserId());
  }

  @Test
  void deleteFeedbackByUserAndProject_ShouldReturnNotFound_WhenFeedbackDoesNotExist() {
    when(feedbackIaRepository.findByProject_ProjectIdAndUser_UserId(project.getProjectId(), user.getUserId()))
      .thenReturn(null);

    ResponseEntity<Object> response = feedbackIaService.deleteFeedbackByUserAndProject(project.getProjectId(), user.getUserId());

    assertNotNull(response);
    assertEquals(404, response.getStatusCodeValue());
    assertEquals("Feedback non trouvé pour le projet ID " + project.getProjectId() + " et l'utilisateur ID " + user.getUserId(), response.getBody());
  }

  @Test
  void deleteFeedbacksByProjectId_ShouldDeleteAllFeedbacks() {
    List<FeedbackIa> feedbacks = new ArrayList<>();
    feedbacks.add(feedbackIa);
    when(feedbackIaRepository.findByProject_ProjectId(project.getProjectId())).thenReturn(feedbacks);

    ResponseEntity<Object> response = feedbackIaService.deleteFeedbacksByProjectId(project.getProjectId());

    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertEquals("Tous les FeedBacks du projet ont bien été supprimé", response.getBody());
    verify(feedbackIaRepository, times(1)).deleteAll(feedbacks);
  }
}
