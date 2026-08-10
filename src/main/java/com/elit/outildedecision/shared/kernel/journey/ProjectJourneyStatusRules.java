package com.elit.outildedecision.shared.kernel.journey;

import com.elit.outildedecision.collaboration.executionplan.entity.ExecutionPlan;
import com.elit.outildedecision.collaboration.multicriteria.entity.MultiCriteriaAnalysis;
import com.elit.outildedecision.collaboration.predictibility.entity.PredictibilityAnalysis;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.util.AnalysisJourney;
import com.elit.outildedecision.shared.util.FormProgressStatus;
import com.elit.outildedecision.shared.util.JourneyState;
import jakarta.persistence.criteria.AbstractQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Single source of truth for the state of an analysis journey within a project.
 *
 * <p>{@code Project.projectState} is a project wide state overwritten by whichever journey moves
 * last, so it cannot answer "is the AI journey finished?". These rules answer that question per
 * journey and expose the very same rules under two forms that must stay equivalent:
 * {@link #resolve(Project, AnalysisJourney)} for an already loaded aggregate, and
 * {@link #toPredicate(Path, AbstractQuery, CriteriaBuilder, AnalysisJourney, JourneyState)} for
 * database side filtering. {@code ProjectJourneyStatusRulesEquivalenceIT} freezes that equivalence.
 *
 * <p>Rules:
 * <ul>
 *   <li>AI: completed as soon as a {@code ResultAi} exists, in progress once the AI form is
 *       engaged;</li>
 *   <li>collaborative: completed once an execution plan is finished (the vote and brainstorming
 *       steps are therefore still "in progress"), in progress once the collaborative form is
 *       engaged;</li>
 *   <li>audit: completed once an audit carries a score, in progress once an audit exists.</li>
 * </ul>
 */
@Component
public class ProjectJourneyStatusRules {

  /**
   * Resolves the state of the given journey for an already loaded project.
   *
   * @param project the project to inspect
   * @param journey the journey whose state is resolved
   * @return the state of the journey for this project
   */
  public JourneyState resolve(final Project project, final AnalysisJourney journey) {
    if (isCompleted(project, journey)) {
      return JourneyState.COMPLETED;
    }
    if (isEngaged(project, journey)) {
      return JourneyState.IN_PROGRESS;
    }
    return JourneyState.NOT_STARTED;
  }

  /**
   * Builds the criteria predicate selecting the projects whose given journey is in the given state.
   *
   * @param projectPath the path pointing to the project to filter
   * @param query       the query the predicate belongs to, used to host the exists subqueries
   * @param builder     the criteria builder
   * @param journey     the journey the state applies to
   * @param state       the expected state of the journey
   * @return the predicate to add to the query
   */
  public Predicate toPredicate(final Path<Project> projectPath, final AbstractQuery<?> query,
                               final CriteriaBuilder builder, final AnalysisJourney journey,
                               final JourneyState state) {
    Predicate completed = completedPredicate(projectPath, query, builder, journey);
    if (state == JourneyState.COMPLETED) {
      return completed;
    }
    Predicate engaged = engagedPredicate(projectPath, query, builder, journey);
    if (state == JourneyState.IN_PROGRESS) {
      return builder.and(engaged, builder.not(completed));
    }
    return builder.and(builder.not(engaged), builder.not(completed));
  }

  /**
   * Builds the criteria predicate selecting the projects engaged in the given journey, whatever
   * its state.
   *
   * @param projectPath the path pointing to the project to filter
   * @param query       the query the predicate belongs to, used to host the exists subqueries
   * @param builder     the criteria builder
   * @param journey     the journey the project must be engaged in
   * @return the predicate to add to the query
   */
  public Predicate toEngagedPredicate(final Path<Project> projectPath, final AbstractQuery<?> query,
                                      final CriteriaBuilder builder, final AnalysisJourney journey) {
    return engagedPredicate(projectPath, query, builder, journey);
  }

  private boolean isCompleted(final Project project, final AnalysisJourney journey) {
    return switch (journey) {
      case IA -> !safeList(project.getAiResults()).isEmpty();
      case COLLABORATIVE -> hasFinishedExecutionPlan(project);
      case AUDIT -> safeList(project.getAudits()).stream().anyMatch(audit -> audit.getAuditScore() != null);
    };
  }

  private boolean isEngaged(final Project project, final AnalysisJourney journey) {
    return switch (journey) {
      case IA -> isFormEngaged(project, FormProgressStatus.IA);
      case COLLABORATIVE -> isFormEngaged(project, FormProgressStatus.COLLABORATIVE);
      case AUDIT -> !safeList(project.getAudits()).isEmpty();
    };
  }

  private boolean isFormEngaged(final Project project, final FormProgressStatus expectedStatus) {
    FormProgressStatus formProgressStatus = project.getFormProgressStatus();
    return formProgressStatus == expectedStatus || formProgressStatus == FormProgressStatus.BOTH;
  }

  private boolean hasFinishedExecutionPlan(final Project project) {
    for (MultiCriteriaAnalysis multiCriteriaAnalysis : safeList(project.getMultiCriteriaAnalysisList())) {
      PredictibilityAnalysis predictibilityAnalysis = multiCriteriaAnalysis.getPredictibilityAnalysis();
      if (predictibilityAnalysis == null) {
        continue;
      }
      ExecutionPlan executionPlan = predictibilityAnalysis.getExecutionPlan();
      if (executionPlan != null && Boolean.TRUE.equals(executionPlan.getIsFinished())) {
        return true;
      }
    }
    return false;
  }

  private Predicate completedPredicate(final Path<Project> projectPath, final AbstractQuery<?> query,
                                       final CriteriaBuilder builder, final AnalysisJourney journey) {
    return switch (journey) {
      case IA -> existsRelation(projectPath, query, builder, "aiResults");
      case COLLABORATIVE -> existsFinishedExecutionPlan(projectPath, query, builder);
      case AUDIT -> existsRelation(projectPath, query, builder, "audits", "auditScore");
    };
  }

  private Predicate engagedPredicate(final Path<Project> projectPath, final AbstractQuery<?> query,
                                     final CriteriaBuilder builder, final AnalysisJourney journey) {
    return switch (journey) {
      case IA -> formEngagedPredicate(projectPath, FormProgressStatus.IA);
      case COLLABORATIVE -> formEngagedPredicate(projectPath, FormProgressStatus.COLLABORATIVE);
      case AUDIT -> existsRelation(projectPath, query, builder, "audits");
    };
  }

  private Predicate formEngagedPredicate(final Path<Project> projectPath, final FormProgressStatus expectedStatus) {
    return projectPath.get("formProgressStatus").in(expectedStatus, FormProgressStatus.BOTH);
  }

  private Predicate existsFinishedExecutionPlan(final Path<Project> projectPath, final AbstractQuery<?> query,
                                                final CriteriaBuilder builder) {
    RelationSubquery relation = relationSubquery(projectPath, query, builder,
        "multiCriteriaAnalysisList", "predictibilityAnalysis", "executionPlan");
    relation.subquery().where(builder.and(
        builder.equal(relation.projectRoot(), projectPath),
        builder.isTrue(relation.target().get("isFinished"))));
    return builder.exists(relation.subquery());
  }

  private Predicate existsRelation(final Path<Project> projectPath, final AbstractQuery<?> query,
                                   final CriteriaBuilder builder, final String... relationPath) {
    RelationSubquery relation = relationSubquery(projectPath, query, builder, relationPath);
    relation.subquery().where(builder.equal(relation.projectRoot(), projectPath));
    return builder.exists(relation.subquery());
  }

  /**
   * Builds an exists subquery walking the given relation path from a project root. Relations are
   * navigated by name so that the criteria code does not have to reference the entity classes of
   * every bounded context.
   *
   * @param projectPath  the path pointing to the project to correlate the subquery with
   * @param query        the query hosting the subquery
   * @param builder      the criteria builder
   * @param relationPath the relations to join, from the project root
   * @return the subquery under construction, still missing its where clause
   */
  private RelationSubquery relationSubquery(final Path<Project> projectPath, final AbstractQuery<?> query,
                                            final CriteriaBuilder builder, final String... relationPath) {
    Subquery<Integer> subquery = query.subquery(Integer.class);
    Root<Project> projectRoot = subquery.from(Project.class);
    From<?, ?> target = projectRoot;
    for (String relation : relationPath) {
      target = target.join(relation);
    }
    subquery.select(builder.literal(1));
    return new RelationSubquery(subquery, projectRoot, target);
  }

  private <T> List<T> safeList(final List<T> values) {
    return values == null ? Collections.emptyList() : values;
  }

  /**
   * Holder of an exists subquery under construction: the subquery itself, its project root (to
   * correlate with the outer query) and the last joined relation (to constrain).
   */
  private record RelationSubquery(Subquery<Integer> subquery, Root<Project> projectRoot, From<?, ?> target) {
  }
}
