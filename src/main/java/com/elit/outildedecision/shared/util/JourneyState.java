package com.elit.outildedecision.shared.util;

import java.util.Optional;

/**
 * State of a single {@link AnalysisJourney} for a given project. It is deliberately distinct from
 * {@link ProjectState}, which is a project wide state shared by every journey and therefore cannot
 * tell whether a specific journey is finished.
 */
public enum JourneyState {

  /**
   * The journey has never been started for this project.
   */
  NOT_STARTED,

  /**
   * The journey has been started but has not produced its final result yet.
   */
  IN_PROGRESS,

  /**
   * The journey has produced its final result.
   */
  COMPLETED;

  /**
   * Resolves the journey state targeted by the {@code status} filter value sent by the front.
   *
   * @param filterValue the raw filter value ({@code PENDING}, {@code IN_PROGRESS},
   *                    {@code COMPLETED}, {@code ALL} or {@code null})
   * @return the targeted state, or {@link Optional#empty()} when no state is targeted
   */
  public static Optional<JourneyState> fromProjectStateFilter(final String filterValue) {
    if (filterValue == null) {
      return Optional.empty();
    }
    String normalizedValue = filterValue.trim();
    if (ProjectState.PENDING.name().equalsIgnoreCase(normalizedValue)) {
      return Optional.of(NOT_STARTED);
    }
    if (ProjectState.IN_PROGRESS.name().equalsIgnoreCase(normalizedValue)) {
      return Optional.of(IN_PROGRESS);
    }
    if (ProjectState.COMPLETED.name().equalsIgnoreCase(normalizedValue)) {
      return Optional.of(COMPLETED);
    }
    return Optional.empty();
  }
}
