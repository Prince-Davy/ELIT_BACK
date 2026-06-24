package com.elit.outildedecision.shared.config.seed;

/**
 * A single, self-contained unit of startup seeding.
 *
 * <p>Implementations must be <strong>idempotent</strong>: a second run (application restart) must
 * neither duplicate nor overwrite existing data. Each seeder is responsible for its own existence
 * checks before inserting.</p>
 */
public interface DataSeeder {

  /**
   * Human-readable name used for logging (e.g. {@code "demo-users"}).
   *
   * @return the seeder name
   */
  String name();

  /**
   * Executes the seeding. Implementations decide whether there is anything to insert and skip
   * silently (logging the reason) when the data is already present.
   *
   * @return the number of rows inserted (0 when nothing was seeded)
   */
  int seed();
}
