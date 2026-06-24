package com.elit.outildedecision.shared.config.seed;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

@ExtendWith(MockitoExtension.class)
class ReferenceDataSeederTest {

  @Mock
  private JdbcTemplate jdbcTemplate;

  @Test
  void shouldSeedEveryEmptyTable() {
    when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenReturn(0);
    ReferenceDataSeeder seeder = new ReferenceDataSeeder(jdbcTemplate, true);

    int seededTables = seeder.seed();

    assertThat(seededTables).isEqualTo(6);
    verify(jdbcTemplate, org.mockito.Mockito.times(6)).execute(anyString());
  }

  @Test
  void shouldSkipTablesThatAlreadyHaveData() {
    when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenReturn(5);
    ReferenceDataSeeder seeder = new ReferenceDataSeeder(jdbcTemplate, true);

    int seededTables = seeder.seed();

    assertThat(seededTables).isZero();
    verify(jdbcTemplate, never()).execute(anyString());
  }

  @Test
  void shouldDoNothingWhenDisabled() {
    ReferenceDataSeeder seeder = new ReferenceDataSeeder(jdbcTemplate, false);

    int seededTables = seeder.seed();

    assertThat(seededTables).isZero();
    verify(jdbcTemplate, never()).queryForObject(anyString(), eq(Integer.class));
    verify(jdbcTemplate, never()).execute(anyString());
  }
}
