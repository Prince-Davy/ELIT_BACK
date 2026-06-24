
/**
 * Pure calculation components of the methodology evaluation feature.
 *
 * <p>{@link com.elit.outildedecision.ai.methodology.evaluation.calculate.MethodologyEvaluationSelector}
 * trims the methodology evaluations to their top results and highest-scoring explanatory factors from
 * data already mapped in memory, with no repository access. The surrounding service keeps the
 * load/map responsibility while the selection logic stays isolated and unit-testable (SRP).</p>
 */

package com.elit.outildedecision.ai.methodology.evaluation.calculate;
