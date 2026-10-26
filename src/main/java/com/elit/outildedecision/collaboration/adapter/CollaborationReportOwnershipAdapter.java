package com.elit.outildedecision.collaboration.adapter;

import com.elit.outildedecision.collaboration.executionplan.entity.ExecutionPlan;
import com.elit.outildedecision.collaboration.executionplan.repository.ExecutionPlanRepository;
import com.elit.outildedecision.collaboration.multicriteria.entity.MultiCriteriaAnalysis;
import com.elit.outildedecision.collaboration.multicriteria.repository.MultiCriteriaAnalysisRepository;
import com.elit.outildedecision.collaboration.predictibility.entity.PredictibilityAnalysis;
import com.elit.outildedecision.collaboration.predictibility.repository.PredictibilityAnalysisRepository;
import com.elit.outildedecision.shared.port.CollaborationReportOwnershipPort;
import com.elit.outildedecision.shared.util.ReportTypes;
import java.util.Objects;
import org.springframework.stereotype.Component;

/** Checks collaborative report ownership within the Collaboration context. */
@Component
public final class CollaborationReportOwnershipAdapter implements CollaborationReportOwnershipPort {
  private final MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository;
  private final PredictibilityAnalysisRepository predictibilityAnalysisRepository;
  private final ExecutionPlanRepository executionPlanRepository;

  /**
   * Creates the adapter using Collaboration repositories.
   *
   * @param multiCriteriaAnalysisRepository the multicriteria repository
   * @param predictibilityAnalysisRepository the predictibility repository
   * @param executionPlanRepository the execution plan repository
   */
  public CollaborationReportOwnershipAdapter(
      final MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository,
      final PredictibilityAnalysisRepository predictibilityAnalysisRepository,
      final ExecutionPlanRepository executionPlanRepository) {
    this.multiCriteriaAnalysisRepository = multiCriteriaAnalysisRepository;
    this.predictibilityAnalysisRepository = predictibilityAnalysisRepository;
    this.executionPlanRepository = executionPlanRepository;
  }

  /**
   * Checks the complete project relationship of a collaborative analysis.
   *
   * @param type the report type
   * @param analyseId the requested analysis identifier
   * @param projectId the requested project identifier
   * @return true if the analysis exists in the requested project
   */
  @Override
  public boolean belongsToProject(final ReportTypes type, final Long analyseId, final Long projectId) {
    if (type == null || analyseId == null || projectId == null) {
      return false;
    }
    return switch (type) {
      case MULTI_CRITERIA_ANALYSIS -> multiCriteriaAnalysisRepository.findById(analyseId)
        .filter(analysis -> belongsToProject(analysis, projectId)).isPresent();
      case PREDICTIBILITY_ANALYSIS -> predictibilityAnalysisRepository.findById(analyseId)
        .filter(analysis -> belongsToProject(analysis, projectId)).isPresent();
      case EXECUTION_PLAN -> executionPlanRepository.findById(analyseId)
        .filter(plan -> belongsToProject(plan, projectId)).isPresent();
      default -> false;
    };
  }

  private boolean belongsToProject(final MultiCriteriaAnalysis analysis, final Long projectId) {
    return analysis.getProject() != null
      && Objects.equals(analysis.getProject().getProjectId(), projectId);
  }

  private boolean belongsToProject(final PredictibilityAnalysis analysis, final Long projectId) {
    return analysis.getMultiCriteriaAnalysis() != null
      && belongsToProject(analysis.getMultiCriteriaAnalysis(), projectId);
  }

  private boolean belongsToProject(final ExecutionPlan plan, final Long projectId) {
    return plan.getPredictibilityAnalysis() != null
      && belongsToProject(plan.getPredictibilityAnalysis(), projectId);
  }
}
