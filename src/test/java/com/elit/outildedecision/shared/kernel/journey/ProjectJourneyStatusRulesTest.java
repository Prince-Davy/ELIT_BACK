package com.elit.outildedecision.shared.kernel.journey;

import static org.assertj.core.api.Assertions.assertThat;

import com.elit.outildedecision.audit.entity.Audit;
import com.elit.outildedecision.audit.entity.AuditScore;
import com.elit.outildedecision.collaboration.executionplan.entity.ExecutionPlan;
import com.elit.outildedecision.collaboration.multicriteria.entity.MultiCriteriaAnalysis;
import com.elit.outildedecision.collaboration.predictibility.entity.PredictibilityAnalysis;
import com.elit.outildedecision.ai.result.entity.ResultAi;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.util.AnalysisJourney;
import com.elit.outildedecision.shared.util.FormProgressStatus;
import com.elit.outildedecision.shared.util.JourneyState;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Unit tests of the journey status rules: each journey is evaluated on its own data, never on the
 * project wide {@code projectState}.
 */
class ProjectJourneyStatusRulesTest {

  private final ProjectJourneyStatusRules rules = new ProjectJourneyStatusRules();

  @Test
  void shouldReturnCompletedWhenAiResultExists() {
    Project project = projectEngagedIn(FormProgressStatus.BOTH);
    project.setAiResults(List.of(new ResultAi()));

    assertThat(rules.resolve(project, AnalysisJourney.IA)).isEqualTo(JourneyState.COMPLETED);
  }

  @Test
  void shouldReturnInProgressWhenAiFormIsEngagedWithoutResult() {
    Project project = projectEngagedIn(FormProgressStatus.IA);

    assertThat(rules.resolve(project, AnalysisJourney.IA)).isEqualTo(JourneyState.IN_PROGRESS);
  }

  @Test
  void shouldReturnNotStartedWhenOnlyTheCollaborativeFormIsEngaged() {
    Project project = projectEngagedIn(FormProgressStatus.COLLABORATIVE);

    assertThat(rules.resolve(project, AnalysisJourney.IA)).isEqualTo(JourneyState.NOT_STARTED);
  }

  @Test
  void shouldReturnCompletedWhenExecutionPlanIsFinished() {
    Project project = projectEngagedIn(FormProgressStatus.BOTH);
    project.setMultiCriteriaAnalysisList(List.of(multiCriteriaAnalysisWithExecutionPlan(true)));

    assertThat(rules.resolve(project, AnalysisJourney.COLLABORATIVE)).isEqualTo(JourneyState.COMPLETED);
  }

  @Test
  void shouldReturnInProgressWhenExecutionPlanIsNotFinishedYet() {
    Project project = projectEngagedIn(FormProgressStatus.COLLABORATIVE);
    project.setMultiCriteriaAnalysisList(List.of(multiCriteriaAnalysisWithExecutionPlan(false)));

    assertThat(rules.resolve(project, AnalysisJourney.COLLABORATIVE)).isEqualTo(JourneyState.IN_PROGRESS);
  }

  @Test
  void shouldReturnInProgressWhenCollaborativeVoteIsStillOpen() {
    Project project = projectEngagedIn(FormProgressStatus.COLLABORATIVE);
    MultiCriteriaAnalysis multiCriteriaAnalysis = new MultiCriteriaAnalysis();
    multiCriteriaAnalysis.setPredictibilityAnalysis(new PredictibilityAnalysis());
    project.setMultiCriteriaAnalysisList(List.of(multiCriteriaAnalysis));

    assertThat(rules.resolve(project, AnalysisJourney.COLLABORATIVE)).isEqualTo(JourneyState.IN_PROGRESS);
  }

  @Test
  void shouldReturnNotStartedWhenNoCollaborativeFormIsEngaged() {
    Project project = projectEngagedIn(FormProgressStatus.IA);

    assertThat(rules.resolve(project, AnalysisJourney.COLLABORATIVE)).isEqualTo(JourneyState.NOT_STARTED);
  }

  @Test
  void shouldReturnCompletedWhenAnAuditIsScored() {
    Project project = projectEngagedIn(FormProgressStatus.None);
    Audit audit = new Audit();
    audit.setAuditScore(new AuditScore());
    project.setAudits(List.of(audit));

    assertThat(rules.resolve(project, AnalysisJourney.AUDIT)).isEqualTo(JourneyState.COMPLETED);
  }

  @Test
  void shouldReturnInProgressWhenAnAuditExistsWithoutScore() {
    Project project = projectEngagedIn(FormProgressStatus.None);
    project.setAudits(List.of(new Audit()));

    assertThat(rules.resolve(project, AnalysisJourney.AUDIT)).isEqualTo(JourneyState.IN_PROGRESS);
  }

  @Test
  void shouldReturnNotStartedWhenProjectHasNoAudit() {
    Project project = projectEngagedIn(FormProgressStatus.BOTH);

    assertThat(rules.resolve(project, AnalysisJourney.AUDIT)).isEqualTo(JourneyState.NOT_STARTED);
  }

  private Project projectEngagedIn(final FormProgressStatus formProgressStatus) {
    Project project = new Project();
    project.setFormProgressStatus(formProgressStatus);
    project.setAiResults(new ArrayList<>());
    project.setMultiCriteriaAnalysisList(new ArrayList<>());
    project.setAudits(new ArrayList<>());
    return project;
  }

  private MultiCriteriaAnalysis multiCriteriaAnalysisWithExecutionPlan(final boolean finished) {
    ExecutionPlan executionPlan = new ExecutionPlan();
    executionPlan.setIsFinished(finished);
    PredictibilityAnalysis predictibilityAnalysis = new PredictibilityAnalysis();
    predictibilityAnalysis.setExecutionPlan(executionPlan);
    MultiCriteriaAnalysis multiCriteriaAnalysis = new MultiCriteriaAnalysis();
    multiCriteriaAnalysis.setPredictibilityAnalysis(predictibilityAnalysis);
    return multiCriteriaAnalysis;
  }
}
