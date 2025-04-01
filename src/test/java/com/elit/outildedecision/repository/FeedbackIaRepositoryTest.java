package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.FeedbackIa;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class FeedbackIaRepositoryTest {

  @Autowired
  private FeedbackIaRepository feedbackIaRepository;
  @Autowired
  private ProjectRepository projectRepository;
  @Autowired
  private UserRepository userRepository;
  private Project project;
  private User user;
  private FeedbackIa feedback;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setUserId(1L);
    user.setUsername("testuser");
    user.setUserEmail("testuser@example.com");
    user = userRepository.save(user);

    project = new Project();
    project.setProjectId(1L);
    project.setName("Test Project");
    project.setDescription("Project description");
    project = projectRepository.save(project);

    feedback = new FeedbackIa(null, true, "Agile", "Good feedback", project, user);
    feedback = feedbackIaRepository.save(feedback);
  }

  @Test
  @Disabled
  void testFindByProjectId_ShouldReturnFeedbacks() {
    List<FeedbackIa> feedbacks = feedbackIaRepository.findByProject_ProjectId(project.getProjectId());

    assertThat(feedbacks).isNotNull();
    assertThat(feedbacks).hasSize(1);

    FeedbackIa retrievedFeedback = feedbacks.get(0);
    assertThat(retrievedFeedback.getMethodology()).isEqualTo("Agile");
    assertThat(retrievedFeedback.getFeedbackAnswer()).isEqualTo("Good feedback");
  }

  @Test
  @Disabled
  void testFindByProjectId_WhenNoFeedbacks_ShouldReturnEmptyList() {
    List<FeedbackIa> feedbacks = feedbackIaRepository.findByProject_ProjectId(-1L);

    assertThat(feedbacks).isEmpty();
  }

  @Test
  @Disabled
  void testFindByProjectIdAndUserId_ShouldReturnFeedback() {
    FeedbackIa retrievedFeedback = feedbackIaRepository.findByProject_ProjectIdAndUser_UserId(project.getProjectId(), user.getUserId())
      .orElse(null);

    assertThat(retrievedFeedback).isNotNull();
    assertThat(retrievedFeedback.getUser().getUserId()).isEqualTo(user.getUserId());
    assertThat(retrievedFeedback.getProject().getProjectId()).isEqualTo(project.getProjectId());
    assertThat(retrievedFeedback.getMethodology()).isEqualTo("Agile");
  }

  @Test
  @Disabled
  void testFindByProjectIdAndUserId_WhenNoFeedbacks_ShouldReturnEmptyList() {
    FeedbackIa feedback = feedbackIaRepository.findByProject_ProjectIdAndUser_UserId(project.getProjectId(), -1L)
      .orElse(null);

    assertThat(feedback).isNull();
  }

  @Test
  @Disabled
  void testDeleteByProjectIdAndUserId_ShouldDeleteFeedback() {
    feedbackIaRepository.deleteByProject_ProjectIdAndUser_UserId(project.getProjectId(), user.getUserId());

    List<FeedbackIa> feedbacks = feedbackIaRepository.findByProject_ProjectId(project.getProjectId());

    assertThat(feedbacks).isEmpty();
  }

  @Test
  @Disabled
  void testDeleteByProjectIdAndUserId_WhenNoFeedback_ShouldNotThrowError() {
    feedbackIaRepository.deleteByProject_ProjectIdAndUser_UserId(-1L, -1L);

    List<FeedbackIa> feedbacks = feedbackIaRepository.findByProject_ProjectId(project.getProjectId());

    assertThat(feedbacks).hasSize(1);
  }
}
