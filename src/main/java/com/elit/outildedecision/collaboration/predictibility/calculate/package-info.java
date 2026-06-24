
/**
 * Pure calculation components of the predictibility feature.
 *
 * <p>{@link com.elit.outildedecision.collaboration.predictibility.calculate.MethodologyScoreCalculator}
 * computes, from the confirmed votes already loaded in memory, the score of each methodology and the
 * methodology gathering the highest cumulated votes, with no repository access. The surrounding
 * service keeps the load/persist responsibility while the scoring logic stays isolated and
 * unit-testable (SRP).</p>
 */

package com.elit.outildedecision.collaboration.predictibility.calculate;
