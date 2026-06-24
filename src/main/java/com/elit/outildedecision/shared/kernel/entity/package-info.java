
/**
 * Shared kernel — root aggregate JPA entities.
 * <p>Holds the {@code Project} / {@code User} aggregates that carry inverse {@code @OneToMany}
 * collections towards several bounded contexts ({@code ai}/{@code collaboration}/{@code audit}).
 * As a deliberate <em>Shared Kernel</em> (DDD), this is the single place allowed to reference
 * context entities downward through those inverse mappings; the {@code shared}-leaf ArchUnit rule
 * carries an explicit, documented exception for {@code ..shared.kernel..} (see
 * {@code ArchitectureTest#sharedShouldNotDependOnFeatureContexts}). Schema is frozen
 * ({@code ddl-auto}); do not alter {@code @Table}/{@code @Column} mappings.</p>
 * <ul>
 *   <li>{@link com.elit.outildedecision.shared.kernel.entity.Project} maps the {@code project} table</li>
 *   <li>{@link com.elit.outildedecision.shared.kernel.entity.User} maps the {@code user} table</li>
 * </ul>
 */

package com.elit.outildedecision.shared.kernel.entity;
