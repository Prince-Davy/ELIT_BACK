
/**
 * Pure calculation components of the Kiviat feature.
 *
 * <p>{@link com.elit.outildedecision.ai.kiviat.calculate.KiviatScoreCalculator} aggregates the AI
 * answer notes by theme into the radar axis scores from data already loaded in memory, with no
 * repository access. The surrounding service keeps the load responsibility while the scoring logic
 * stays isolated and unit-testable (SRP).</p>
 */

package com.elit.outildedecision.ai.kiviat.calculate;
