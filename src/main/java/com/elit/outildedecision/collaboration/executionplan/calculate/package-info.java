
/**
 * Pure calculation components of the execution plan feature.
 *
 * <p>{@link com.elit.outildedecision.collaboration.executionplan.calculate.ExecutionPlanScoreCalculator}
 * counts the votes of each method and selects the most-voted method(s) from data already loaded in
 * memory, with no repository access. The surrounding service keeps the load/persist responsibility
 * while the scoring logic stays isolated and unit-testable (SRP).</p>
 */

package com.elit.outildedecision.collaboration.executionplan.calculate;
