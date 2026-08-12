package com.elit.outildedecision.shared.project.repository.specification;

import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.kernel.journey.ProjectJourneyStatusRules;
import com.elit.outildedecision.shared.project.dto.ProjectFilterRequest;
import com.elit.outildedecision.shared.project.entity.ProjectUserPermission;
import com.elit.outildedecision.shared.util.AnalysisJourney;
import com.elit.outildedecision.shared.util.JourneyState;
import com.elit.outildedecision.shared.util.ProjectState;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

/**
 * Builds the specification listing the project permissions matching the filters of the "my
 * projects" page.
 *
 * <p>When an analysis journey is selected, the status filter is applied to the state of that
 * journey only ({@link ProjectJourneyStatusRules}) instead of the project wide
 * {@code projectState}, which is shared by every journey and therefore mixes them up. Without a
 * selected journey the legacy behaviour is kept: the status filter applies to
 * {@code projectState}.
 */
@Component
public class ProjectFilterSpecification {

  /**
   * The journey status rules deciding whether a journey is engaged or completed.
   */
  private final ProjectJourneyStatusRules journeyStatusRules;

  /**
   * Instantiates the specification builder.
   *
   * @param journeyStatusRules the journey status rules
   */
  public ProjectFilterSpecification(final ProjectJourneyStatusRules journeyStatusRules) {
    this.journeyStatusRules = journeyStatusRules;
  }

  /**
   * Builds the specification selecting the project permissions of a user matching the filters.
   *
   * @param filter         the filter criteria coming from the front
   * @param userEmail      the email of the connected user
   * @param createdByUser  {@code true} to select the projects owned by the user, {@code false} to
   *                       select the ones the user only contributes to
   * @return the specification to hand over to the repository
   */
  public Specification<ProjectUserPermission> byFilter(final ProjectFilterRequest filter, final String userEmail,
                                                       final boolean createdByUser) {
    return (root, query, builder) -> {
      Path<Project> projectPath = root.get("project");
      List<Predicate> predicates = new ArrayList<>();
      predicates.add(keywordPredicate(projectPath, builder, filter.getKey()));
      predicates.add(builder.equal(root.get("user").get("userEmail"), userEmail));
      predicates.add(ownershipPredicate(root, projectPath, builder, createdByUser));
      statusPredicate(projectPath, query, builder, filter).ifPresent(predicates::add);
      return builder.and(predicates.toArray(new Predicate[0]));
    };
  }

  private Predicate keywordPredicate(final Path<Project> projectPath, final CriteriaBuilder builder,
                                     final String keyword) {
    String pattern = "%" + (keyword == null ? "" : keyword.toLowerCase()) + "%";
    return builder.or(
        builder.like(builder.lower(projectPath.get("name")), pattern),
        builder.like(builder.lower(projectPath.get("description")), pattern));
  }

  private Predicate ownershipPredicate(final Root<ProjectUserPermission> root, final Path<Project> projectPath,
                                       final CriteriaBuilder builder, final boolean createdByUser) {
    Path<Long> chefId = projectPath.get("chefId");
    Path<Long> userId = root.get("user").get("userId");
    return createdByUser ? builder.equal(chefId, userId) : builder.notEqual(chefId, userId);
  }

  private Optional<Predicate> statusPredicate(final Path<Project> projectPath, final CriteriaQuery<?> query,
                                              final CriteriaBuilder builder, final ProjectFilterRequest filter) {
    Optional<AnalysisJourney> journey = AnalysisJourney.fromFilterValue(filter.getTypeAnalyse());
    Optional<JourneyState> journeyState = JourneyState.fromProjectStateFilter(filter.getStatus());
    if (journey.isPresent()) {
      return Optional.of(journeyState
          .map(state -> journeyStatusRules.toPredicate(projectPath, query, builder, journey.get(), state))
          .orElseGet(() -> journeyStatusRules.toEngagedPredicate(projectPath, query, builder, journey.get())));
    }
    return projectStatePredicate(projectPath, builder, filter.getStatus());
  }

  private Optional<Predicate> projectStatePredicate(final Path<Project> projectPath, final CriteriaBuilder builder,
                                                    final String status) {
    if (status == null || ProjectState.ALL.name().equalsIgnoreCase(status.trim())) {
      return Optional.empty();
    }
    for (ProjectState projectState : ProjectState.values()) {
      if (projectState.name().equalsIgnoreCase(status.trim())) {
        return Optional.of(builder.equal(projectPath.get("projectState"), projectState));
      }
    }
    return Optional.empty();
  }
}
