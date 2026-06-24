package com.elit.outildedecision.ai.feedback.repository;

import com.elit.outildedecision.ai.feedback.entity.FeedbackIa;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.project.repository.ProjectRepository;
import com.elit.outildedecision.shared.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class FeedbackIaRepositoryTest {

  @Autowired
  private FeedbackIaRepository feedbackIaRepository;
  @Autowired
  private ProjectRepository projectRepository;
  @Autowired
  private UserRepository userRepository;

  private Project project;
  private User user;

  @BeforeEach
  void setUp() {
    user = persistUser("testuser", "testuser@example.com");
    project = persistProject("Test Project");
    persistFeedback("Agile", "Good feedback", project, user);
  }

  @Test
  void shouldReturnFeedbacksWhenProjectHasFeedback() {
    // when
    List<FeedbackIa> feedbacks = feedbackIaRepository.findByProject_ProjectId(project.getProjectId());

    // then
    assertThat(feedbacks).hasSize(1);
    FeedbackIa retrieved = feedbacks.get(0);
    assertThat(retrieved.getMethodology()).isEqualTo("Agile");
    assertThat(retrieved.getFeedbackAnswer()).isEqualTo("Good feedback");
  }

  @Test
  void shouldReturnEmptyListWhenProjectHasNoFeedback() {
    // when
    List<FeedbackIa> feedbacks = feedbackIaRepository.findByProject_ProjectId(-1L);

    // then
    assertThat(feedbacks).isEmpty();
  }

  @Test
  void shouldReturnOnlyFeedbacksOfGivenProject() {
    // given another project with its own user/feedback
    // (project_id and user_id are both unique, so a distinct user is required)
    Project otherProject = persistProject("Other Project");
    User otherUser = persistUser("otheruser", "otheruser@example.com");
    persistFeedback("Scrum", "Other feedback", otherProject, otherUser);

    // when
    List<FeedbackIa> feedbacks = feedbackIaRepository.findByProject_ProjectId(project.getProjectId());

    // then only the feedback of the requested project is returned
    assertThat(feedbacks)
      .hasSize(1)
      .first()
      .satisfies(f -> assertThat(f.getMethodology()).isEqualTo("Agile"));
  }

  @Test
  void shouldReturnFeedbackWhenProjectAndUserMatch() {
    // when
    Optional<FeedbackIa> retrieved =
      feedbackIaRepository.findByProject_ProjectIdAndUser_UserId(project.getProjectId(), user.getUserId());

    // then
    assertThat(retrieved).isPresent();
    assertThat(retrieved.get().getUser().getUserId()).isEqualTo(user.getUserId());
    assertThat(retrieved.get().getProject().getProjectId()).isEqualTo(project.getProjectId());
    assertThat(retrieved.get().getMethodology()).isEqualTo("Agile");
  }

  @Test
  void shouldReturnEmptyWhenUserHasNoFeedbackForProject() {
    // when querying with an unknown user
    Optional<FeedbackIa> retrieved =
      feedbackIaRepository.findByProject_ProjectIdAndUser_UserId(project.getProjectId(), -1L);

    // then
    assertThat(retrieved).isEmpty();
  }

  @Test
  void shouldReturnEmptyWhenProjectDoesNotMatchUser() {
    // when querying the right user but a wrong project
    Optional<FeedbackIa> retrieved =
      feedbackIaRepository.findByProject_ProjectIdAndUser_UserId(-1L, user.getUserId());

    // then
    assertThat(retrieved).isEmpty();
  }

  @Test
  void shouldDeleteFeedbackWhenProjectAndUserMatch() {
    // when
    feedbackIaRepository.deleteByProject_ProjectIdAndUser_UserId(project.getProjectId(), user.getUserId());

    // then
    assertThat(feedbackIaRepository.findByProject_ProjectId(project.getProjectId())).isEmpty();
  }

  @Test
  void shouldNotDeleteAnythingWhenNoFeedbackMatches() {
    // when deleting with identifiers that match nothing
    feedbackIaRepository.deleteByProject_ProjectIdAndUser_UserId(-1L, -1L);

    // then the existing feedback is preserved
    assertThat(feedbackIaRepository.findByProject_ProjectId(project.getProjectId())).hasSize(1);
  }

  @Test
  void shouldDeleteOnlyTargetedFeedback() {
    // given another project/user feedback
    // (unique constraints prevent reusing the same project or user)
    Project otherProject = persistProject("Other Project");
    User otherUser = persistUser("otheruser", "otheruser@example.com");
    persistFeedback("Scrum", "Other feedback", otherProject, otherUser);

    // when deleting the seeded feedback only
    feedbackIaRepository.deleteByProject_ProjectIdAndUser_UserId(project.getProjectId(), user.getUserId());

    // then the other project's feedback is untouched
    assertThat(feedbackIaRepository.findByProject_ProjectId(project.getProjectId())).isEmpty();
    assertThat(feedbackIaRepository.findByProject_ProjectId(otherProject.getProjectId())).hasSize(1);
  }

  private User persistUser(final String username, final String email) {
    User newUser = new User();
    newUser.setUsername(username);
    newUser.setUserEmail(email);
    return userRepository.save(newUser);
  }

  private Project persistProject(final String name) {
    Project newProject = new Project();
    newProject.setName(name);
    newProject.setDescription("Project description");
    return projectRepository.save(newProject);
  }

  private FeedbackIa persistFeedback(
    final String methodology, final String answer, final Project linkedProject, final User linkedUser) {
    return feedbackIaRepository.save(new FeedbackIa(null, true, methodology, answer, linkedProject, linkedUser));
  }
}
