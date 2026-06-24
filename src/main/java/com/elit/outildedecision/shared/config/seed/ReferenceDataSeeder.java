package com.elit.outildedecision.shared.config.seed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Seeds business reference data (methodologies, methods, survey and execution-plan questions, audit
 * category and global comments) from the SQL scripts under {@code db/seed/reference/}.
 *
 * <p>Each script is a single {@code INSERT} kept as a data resource because it carries large,
 * content-heavy French text that does not belong in Java. A table is seeded only when it is empty
 * ({@code COUNT(*) == 0}), so a restart is idempotent.</p>
 *
 * <p>The scripts use the historical (MySQL) dialect and run on the MySQL environments
 * (local/docker/production). The {@code test} profile uses H2 and seeds its own data, so reference
 * seeding is disabled there via {@code app.seed.reference.enabled=false}.</p>
 */
@Component
@Slf4j
public final class ReferenceDataSeeder implements DataSeeder {

  private static final String SCRIPT_LOCATION = "db/seed/reference/";

  /**
   * Reference tables to seed, in dependency order ({@code methodology} before {@code method}).
   */
  private static final List<String> TABLES = List.of(
      "methodology",
      "method",
      "execution_plan_question",
      "survey_question",
      "category_comment",
      "global_comment");

  private final JdbcTemplate jdbcTemplate;
  private final boolean enabled;

  /**
   * Builds the reference seeder.
   *
   * @param jdbcTemplate template used to count rows and execute the seed scripts
   * @param enabled      whether reference seeding is enabled ({@code app.seed.reference.enabled})
   */
  public ReferenceDataSeeder(final JdbcTemplate jdbcTemplate,
                             @Value("${app.seed.reference.enabled:true}") final boolean enabled) {
    this.jdbcTemplate = jdbcTemplate;
    this.enabled = enabled;
  }

  /** {@inheritDoc} */
  @Override
  public String name() {
    return "reference-data";
  }

  /** {@inheritDoc} */
  @Override
  public int seed() {
    if (!enabled) {
      log.info("Seed [{}] skipped: disabled by app.seed.reference.enabled=false", name());
      return 0;
    }

    int seededTables = 0;
    for (final String table : TABLES) {
      if (seedTable(table)) {
        seededTables++;
      }
    }
    return seededTables;
  }

  private boolean seedTable(final String table) {
    Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + table, Integer.class);
    if (count != null && count > 0) {
      log.debug("Seed [{}]: table {} already populated ({} rows), skipping", name(), table, count);
      return false;
    }
    jdbcTemplate.execute(readScript(table));
    log.debug("Seed [{}]: table {} populated", name(), table);
    return true;
  }

  private String readScript(final String table) {
    ClassPathResource resource = new ClassPathResource(SCRIPT_LOCATION + table + ".sql");
    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(openScript(resource), StandardCharsets.UTF_8))) {
      StringBuilder content = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        content.append(line).append('\n');
      }
      return content.toString();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to read seed script for table " + table, e);
    }
  }

  private InputStream openScript(final ClassPathResource resource) throws IOException {
    if (!resource.exists()) {
      throw new IllegalStateException("Missing seed script: " + resource.getPath());
    }
    return resource.getInputStream();
  }
}
