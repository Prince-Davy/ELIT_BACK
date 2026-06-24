
/**
 * Pure calculation components of the multi-criteria feature.
 *
 * <p>{@link com.elit.outildedecision.collaboration.multicriteria.calculate.MultiCriteriaScoreCalculator}
 * computes the eScore and per-criteria scores of a form step from data already loaded in memory,
 * with no repository access. The surrounding service keeps the load/persist responsibility while the
 * scoring logic stays isolated and unit-testable (SRP).</p>
 */

package com.elit.outildedecision.collaboration.multicriteria.calculate;
