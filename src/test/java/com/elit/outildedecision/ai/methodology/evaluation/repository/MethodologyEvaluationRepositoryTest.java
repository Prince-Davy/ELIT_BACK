package com.elit.outildedecision.ai.methodology.evaluation.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.elit.outildedecision.ai.methodology.evaluation.entity.MethodologyEvaluation;
import com.elit.outildedecision.shared.config.JpaAuditingConfiguration;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.project.repository.ProjectRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaAuditingConfiguration.class)
@ActiveProfiles("test")
class MethodologyEvaluationRepositoryTest {

  @Autowired
  private MethodologyEvaluationRepository methodologyEvaluationRepository;

  @Autowired
  private ProjectRepository projectRepository;

  private Project project;

  @BeforeEach
  void setUp() {
    project = newProject("Test Project");
  }

  @Test
  void shouldReturnEvaluationsOfTheGivenProject() {
    // given
    methodologyEvaluationRepository.save(evaluation(project, "Scrum", 0.9));
    methodologyEvaluationRepository.save(evaluation(project, "Kanban", 0.7));

    // when
    List<MethodologyEvaluation> evaluations = methodologyEvaluationRepository.findByProject(project);

    // then
    assertThat(evaluations)
        .extracting(MethodologyEvaluation::getMethodology)
        .containsExactlyInAnyOrder("Scrum", "Kanban");
  }

  @Test
  void shouldNotReturnEvaluationsOfAnotherProject() {
    // given
    Project otherProject = newProject("Other Project");
    methodologyEvaluationRepository.save(evaluation(project, "Scrum", 0.9));
    methodologyEvaluationRepository.save(evaluation(otherProject, "Kanban", 0.7));

    // when
    List<MethodologyEvaluation> evaluations = methodologyEvaluationRepository.findByProject(project);

    // then
    assertThat(evaluations)
        .extracting(MethodologyEvaluation::getMethodology)
        .containsExactly("Scrum");
  }

  @Test
  void shouldReturnEmptyWhenProjectHasNoEvaluation() {
    // when
    List<MethodologyEvaluation> evaluations = methodologyEvaluationRepository.findByProject(project);

    // then
    assertThat(evaluations).isEmpty();
  }

  private Project newProject(final String name) {
    Project newProject = new Project();
    newProject.setName(name);
    return projectRepository.save(newProject);
  }

  private MethodologyEvaluation evaluation(final Project project, final String methodology,
                                           final double score) {
    return MethodologyEvaluation.builder()
        .project(project)
        .methodology(methodology)
        .score(score)
        .build();
  }
}
