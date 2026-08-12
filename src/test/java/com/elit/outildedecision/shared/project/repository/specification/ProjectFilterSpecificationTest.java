package com.elit.outildedecision.shared.project.repository.specification;

import static org.assertj.core.api.Assertions.assertThat;

import com.elit.outildedecision.audit.entity.Audit;
import com.elit.outildedecision.audit.entity.AuditScore;
import com.elit.outildedecision.collaboration.executionplan.entity.ExecutionPlan;
import com.elit.outildedecision.collaboration.multicriteria.entity.MultiCriteriaAnalysis;
import com.elit.outildedecision.collaboration.predictibility.entity.PredictibilityAnalysis;
import com.elit.outildedecision.ai.result.entity.ResultAi;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.kernel.journey.ProjectJourneyStatusRules;
import com.elit.outildedecision.shared.project.dto.ProjectFilterRequest;
import com.elit.outildedecision.shared.project.entity.ProjectPropertyKey;
import com.elit.outildedecision.shared.project.entity.ProjectUserPermission;
import com.elit.outildedecision.shared.project.repository.ProjectUserPermissionRepository;
import com.elit.outildedecision.shared.util.AnalysisJourney;
import com.elit.outildedecision.shared.util.FormProgressStatus;
import com.elit.outildedecision.shared.util.JourneyState;
import com.elit.outildedecision.shared.util.MethodType;
import com.elit.outildedecision.shared.util.ProjectState;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Integration tests of the project list filter: the status filter must apply to the state of the
 * selected journey and not to the project wide state, which mixes the journeys up (US scenarios).
 *
 * <p>It also freezes the equivalence between the two forms of the rules: what the database
 * filters out must be exactly what {@link ProjectJourneyStatusRules#resolve} computes in Java.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Import({ProjectFilterSpecification.class, ProjectJourneyStatusRules.class})
class ProjectFilterSpecificationTest {

  private static final String USER_EMAIL = "chef@elit.test";

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private ProjectUserPermissionRepository projectUserPermissionRepository;

  @Autowired
  private ProjectFilterSpecification projectFilterSpecification;

  @Autowired
  private ProjectJourneyStatusRules journeyStatusRules;

  private Project aiCompletedCollaborativeInProgress;
  private Project collaborativeCompletedOnly;
  private Project bothCompleted;
  private Project aiInProgress;
  private Project auditCompleted;

  @BeforeEach
  void seedProjects() {
    User chef = new User();
    chef.setUserEmail(USER_EMAIL);
    chef.setUsername("chef");
    entityManager.persist(chef);

    aiCompletedCollaborativeInProgress = persistProject(chef, "IA finished", FormProgressStatus.BOTH);
    persistAiResult(aiCompletedCollaborativeInProgress);
    persistCollaborativeJourney(aiCompletedCollaborativeInProgress, false);

    collaborativeCompletedOnly = persistProject(chef, "Collab finished", FormProgressStatus.COLLABORATIVE);
    persistCollaborativeJourney(collaborativeCompletedOnly, true);

    bothCompleted = persistProject(chef, "Both finished", FormProgressStatus.BOTH);
    persistAiResult(bothCompleted);
    persistCollaborativeJourney(bothCompleted, true);

    aiInProgress = persistProject(chef, "IA running", FormProgressStatus.IA);

    auditCompleted = persistProject(chef, "Audit scored", FormProgressStatus.None);
    persistScoredAudit(auditCompleted, chef);

    entityManager.flush();
    entityManager.clear();
  }

  @Test
  void shouldOnlyReturnProjectsWithAFinishedAiAnalysisWhenFilteringOnCompletedAi() {
    assertThat(projectIdsMatching(AnalysisJourney.IA, JourneyState.COMPLETED))
        .containsExactlyInAnyOrder(aiCompletedCollaborativeInProgress.getProjectId(), bothCompleted.getProjectId());
  }

  @Test
  void shouldOnlyReturnProjectsWithAFinishedCollaborativeAnalysisWhenFilteringOnCompletedCollaborative() {
    assertThat(projectIdsMatching(AnalysisJourney.COLLABORATIVE, JourneyState.COMPLETED))
        .containsExactlyInAnyOrder(collaborativeCompletedOnly.getProjectId(), bothCompleted.getProjectId());
  }

  @Test
  void shouldNotReturnProjectsWithAFinishedAiAnalysisWhenFilteringOnRunningAi() {
    assertThat(projectIdsMatching(AnalysisJourney.IA, JourneyState.IN_PROGRESS))
        .containsExactly(aiInProgress.getProjectId());
  }

  @Test
  void shouldReturnProjectsWhoseCollaborativeAnalysisIsStillRunningWhenFilteringOnRunningCollaborative() {
    assertThat(projectIdsMatching(AnalysisJourney.COLLABORATIVE, JourneyState.IN_PROGRESS))
        .containsExactly(aiCompletedCollaborativeInProgress.getProjectId());
  }

  @Test
  void shouldReturnProjectsWithoutAiJourneyWhenFilteringOnPendingAi() {
    assertThat(projectIdsMatching(AnalysisJourney.IA, JourneyState.NOT_STARTED))
        .containsExactlyInAnyOrder(collaborativeCompletedOnly.getProjectId(), auditCompleted.getProjectId());
  }

  @Test
  void shouldOnlyReturnScoredAuditsWhenFilteringOnCompletedAudit() {
    assertThat(projectIdsMatching(AnalysisJourney.AUDIT, JourneyState.COMPLETED))
        .containsExactly(auditCompleted.getProjectId());
  }

  @Test
  void shouldFilterTheSameProjectsInDatabaseAndInMemoryForEveryJourneyAndState() {
    for (AnalysisJourney journey : AnalysisJourney.values()) {
      for (JourneyState state : JourneyState.values()) {
        assertThat(projectIdsMatching(journey, state))
            .as("journey %s in state %s", journey, state)
            .containsExactlyInAnyOrderElementsOf(projectIdsResolvedInMemory(journey, state));
      }
    }
  }

  private List<Long> projectIdsMatching(final AnalysisJourney journey, final JourneyState state) {
    ProjectFilterRequest filter = new ProjectFilterRequest("", filterStatusOf(state), null, true, 0, 20,
        journey.name());
    return projectUserPermissionRepository
        .findAll(projectFilterSpecification.byFilter(filter, USER_EMAIL, true), Pageable.unpaged())
        .map(permission -> permission.getProject().getProjectId())
        .getContent();
  }

  private List<Long> projectIdsResolvedInMemory(final AnalysisJourney journey, final JourneyState state) {
    return projectUserPermissionRepository.findAll().stream()
        .map(ProjectUserPermission::getProject)
        .filter(project -> journeyStatusRules.resolve(project, journey) == state)
        .map(Project::getProjectId)
        .toList();
  }

  private String filterStatusOf(final JourneyState state) {
    return switch (state) {
      case NOT_STARTED -> ProjectState.PENDING.name();
      case IN_PROGRESS -> ProjectState.IN_PROGRESS.name();
      case COMPLETED -> ProjectState.COMPLETED.name();
    };
  }

  private Project persistProject(final User chef, final String name, final FormProgressStatus formProgressStatus) {
    Project project = new Project();
    project.setName(name);
    project.setDescription(name + " description");
    project.setChefId(chef.getUserId());
    project.setFormProgressStatus(formProgressStatus);
    project.setProjectState(ProjectState.IN_PROGRESS);
    entityManager.persist(project);

    ProjectUserPermission permission = new ProjectUserPermission();
    permission.setId(new ProjectPropertyKey(project.getProjectId(), chef.getUserId()));
    permission.setProject(project);
    permission.setUser(chef);
    permission.setPermission("OWNER");
    entityManager.persist(permission);
    return project;
  }

  private void persistAiResult(final Project project) {
    ResultAi resultAi = new ResultAi();
    resultAi.setProject(project);
    resultAi.setPredictedMethod("AGILE");
    resultAi.setPredictedProba(70.0);
    entityManager.persist(resultAi);
  }

  private void persistCollaborativeJourney(final Project project, final boolean executionPlanFinished) {
    MultiCriteriaAnalysis multiCriteriaAnalysis = new MultiCriteriaAnalysis();
    multiCriteriaAnalysis.setProject(project);
    multiCriteriaAnalysis.setIsFinished(true);
    multiCriteriaAnalysis.setMultiCriteriaAnalysisIteration(1);
    entityManager.persist(multiCriteriaAnalysis);

    PredictibilityAnalysis predictibilityAnalysis = new PredictibilityAnalysis();
    predictibilityAnalysis.setMultiCriteriaAnalysis(multiCriteriaAnalysis);
    predictibilityAnalysis.setIsFinished(true);
    predictibilityAnalysis.setPredictibilityAnalysisIteration(1);
    entityManager.persist(predictibilityAnalysis);

    ExecutionPlan executionPlan = new ExecutionPlan();
    executionPlan.setPredictibilityAnalysis(predictibilityAnalysis);
    executionPlan.setIsFinished(executionPlanFinished);
    executionPlan.setExecutionIteration(1);
    entityManager.persist(executionPlan);
  }

  private void persistScoredAudit(final Project project, final User user) {
    Audit audit = new Audit();
    audit.setProject(project);
    audit.setUser(user);
    audit.setMethod(MethodType.SCRUM);
    audit.setAuditLaunchDate(Timestamp.from(Instant.parse("2026-01-01T00:00:00Z")));
    entityManager.persist(audit);

    AuditScore auditScore = new AuditScore();
    auditScore.setAudit(audit);
    auditScore.setUser(user);
    auditScore.setGlobalScorePercentage(80.0);
    auditScore.setSubmissionDate(Timestamp.from(Instant.parse("2026-01-02T00:00:00Z")));
    entityManager.persist(auditScore);
  }
}
