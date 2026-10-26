package com.elit.outildedecision.collaboration.adapter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.elit.outildedecision.collaboration.executionplan.entity.ExecutionPlan;
import com.elit.outildedecision.collaboration.executionplan.repository.ExecutionPlanRepository;
import com.elit.outildedecision.collaboration.multicriteria.entity.MultiCriteriaAnalysis;
import com.elit.outildedecision.collaboration.multicriteria.repository.MultiCriteriaAnalysisRepository;
import com.elit.outildedecision.collaboration.predictibility.entity.PredictibilityAnalysis;
import com.elit.outildedecision.collaboration.predictibility.repository.PredictibilityAnalysisRepository;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.util.ReportTypes;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Tests for ownership of the three collaborative report types. */
@ExtendWith(MockitoExtension.class)
class CollaborationReportOwnershipAdapterTest {
  @Mock private MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository;
  @Mock private PredictibilityAnalysisRepository predictibilityAnalysisRepository;
  @Mock private ExecutionPlanRepository executionPlanRepository;
  @InjectMocks private CollaborationReportOwnershipAdapter adapter;

  private MultiCriteriaAnalysis multiCriteriaAnalysis(final Long projectId) {
    Project project = new Project();
    project.setProjectId(projectId);
    MultiCriteriaAnalysis analysis = new MultiCriteriaAnalysis();
    analysis.setProject(project);
    return analysis;
  }

  @Test
  void multicriteriaBelongsToMatchingProjectOnly() {
    when(multiCriteriaAnalysisRepository.findById(11L))
      .thenReturn(Optional.of(multiCriteriaAnalysis(1L)));
    assertTrue(adapter.belongsToProject(ReportTypes.MULTI_CRITERIA_ANALYSIS, 11L, 1L));
    assertFalse(adapter.belongsToProject(ReportTypes.MULTI_CRITERIA_ANALYSIS, 11L, 2L));
  }

  @Test
  void predictibilityFollowsTheMulticriteriaProject() {
    PredictibilityAnalysis analysis = new PredictibilityAnalysis();
    analysis.setMultiCriteriaAnalysis(multiCriteriaAnalysis(1L));
    when(predictibilityAnalysisRepository.findById(12L)).thenReturn(Optional.of(analysis));
    assertTrue(adapter.belongsToProject(ReportTypes.PREDICTIBILITY_ANALYSIS, 12L, 1L));
    assertFalse(adapter.belongsToProject(ReportTypes.PREDICTIBILITY_ANALYSIS, 12L, 2L));
  }

  @Test
  void executionPlanFollowsTheCompleteOwnershipChain() {
    PredictibilityAnalysis analysis = new PredictibilityAnalysis();
    analysis.setMultiCriteriaAnalysis(multiCriteriaAnalysis(1L));
    ExecutionPlan plan = new ExecutionPlan();
    plan.setPredictibilityAnalysis(analysis);
    when(executionPlanRepository.findById(13L)).thenReturn(Optional.of(plan));
    assertTrue(adapter.belongsToProject(ReportTypes.EXECUTION_PLAN, 13L, 1L));
    assertFalse(adapter.belongsToProject(ReportTypes.EXECUTION_PLAN, 13L, 2L));
  }

  @Test
  void missingAnalysisIsNotOwned() {
    when(multiCriteriaAnalysisRepository.findById(11L)).thenReturn(Optional.empty());
    assertFalse(adapter.belongsToProject(ReportTypes.MULTI_CRITERIA_ANALYSIS, 11L, 1L));
  }

  @Test
  void unsupportedTypeOrMissingIdentifiersIsNotOwned() {
    assertFalse(adapter.belongsToProject(ReportTypes.AUDIT, 11L, 1L));
    assertFalse(adapter.belongsToProject(ReportTypes.MULTI_CRITERIA_ANALYSIS, null, 1L));
    assertFalse(adapter.belongsToProject(ReportTypes.MULTI_CRITERIA_ANALYSIS, 11L, null));
  }
}
