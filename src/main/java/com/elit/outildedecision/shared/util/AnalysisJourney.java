package com.elit.outildedecision.shared.util;

import java.util.Optional;

/**
 * Analysis journey a project can be engaged in. Unlike {@link FormProgressStatus}, which
 * describes the aggregated progress of a project, a journey designates a single decision path
 * whose state is evaluated independently of the others.
 */
public enum AnalysisJourney {

  /**
   * AI based analysis.
   */
  IA,

  /**
   * Collaborative analysis (multi-criteria, predictibility, execution plan).
   */
  COLLABORATIVE,

  /**
   * Audit of an existing project.
   */
  AUDIT;

  /**
   * Resolves the journey targeted by the {@code typeAnalyse} filter value sent by the front.
   *
   * @param filterValue the raw filter value ({@code IA}, {@code COLLABORATIVE}, {@code AUDIT},
   *                    {@code ALL}, {@code BOTH}, {@code None} or {@code null})
   * @return the targeted journey, or {@link Optional#empty()} when no single journey is targeted
   */
  public static Optional<AnalysisJourney> fromFilterValue(final String filterValue) {
    if (filterValue == null) {
      return Optional.empty();
    }
    for (AnalysisJourney journey : values()) {
      if (journey.name().equalsIgnoreCase(filterValue.trim())) {
        return Optional.of(journey);
      }
    }
    return Optional.empty();
  }
}
