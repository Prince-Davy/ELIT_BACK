package com.elit.outildedecision.shared.config.seed;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Runs the registered {@link DataSeeder}s once at application startup, on every environment.
 *
 * <p>This is the reference component for project logging conventions: it logs the start of the
 * seeding, the result of each seeder (rows inserted or skip reason) and a final summary.</p>
 *
 * <p>Behaviour:</p>
 * <ul>
 *   <li>Globally toggled by {@code app.seed.enabled} (default {@code true}); each seeder may expose
 *       its own finer-grained switch (e.g. {@code app.seed.demo.enabled}).</li>
 *   <li>Idempotent by delegation: each seeder checks for existing data before inserting, so a
 *       restart neither duplicates nor overwrites data.</li>
 *   <li>On the {@code test} profile a failing seeder is logged and swallowed so it never breaks the
 *       test context; on any other profile a failure aborts startup, since a partially seeded
 *       database is an inconsistent state.</li>
 * </ul>
 */
@Component
@Slf4j
public final class DataSeedInitializer implements ApplicationRunner {

  private static final String TEST_PROFILE = "test";

  private final List<DataSeeder> seeders;
  private final Environment environment;
  private final boolean enabled;

  /**
   * Builds the initializer with every discovered seeder.
   *
   * @param seeders     all {@link DataSeeder} beans, injected by Spring (extensible without change)
   * @param environment the Spring environment, used to detect the {@code test} profile
   * @param enabled     whether seeding is enabled at all ({@code app.seed.enabled})
   */
  public DataSeedInitializer(final List<DataSeeder> seeders,
                             final Environment environment,
                             @Value("${app.seed.enabled:true}") final boolean enabled) {
    this.seeders = seeders;
    this.environment = environment;
    this.enabled = enabled;
  }

  /** {@inheritDoc} */
  @Override
  public void run(final ApplicationArguments args) {
    if (!enabled) {
      log.info("Data seeding skipped: disabled by app.seed.enabled=false");
      return;
    }

    log.info("Data seeding started ({} seeder(s))", seeders.size());
    int totalInserted = 0;
    for (final DataSeeder seeder : seeders) {
      totalInserted += runSeeder(seeder);
    }
    log.info("Data seeding finished: {} row(s) inserted across {} seeder(s)",
        totalInserted, seeders.size());
  }

  private int runSeeder(final DataSeeder seeder) {
    try {
      int inserted = seeder.seed();
      if (inserted > 0) {
        log.info("Seed [{}] executed: {} row(s) inserted", seeder.name(), inserted);
      } else {
        log.info("Seed [{}] skipped: data already present or nothing to insert", seeder.name());
      }
      return inserted;
    } catch (RuntimeException e) {
      if (isTestProfile()) {
        log.error("Seed [{}] failed under test profile; ignoring to keep the test context up",
            seeder.name(), e);
        return 0;
      }
      log.error("Seed [{}] failed; aborting startup to avoid an inconsistent database",
          seeder.name(), e);
      throw e;
    }
  }

  private boolean isTestProfile() {
    for (final String profile : environment.getActiveProfiles()) {
      if (TEST_PROFILE.equalsIgnoreCase(profile)) {
        return true;
      }
    }
    return false;
  }
}
