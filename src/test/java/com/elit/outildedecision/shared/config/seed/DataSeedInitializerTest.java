package com.elit.outildedecision.shared.config.seed;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

@ExtendWith(MockitoExtension.class)
class DataSeedInitializerTest {

  @Mock
  private Environment environment;

  @Mock
  private DataSeeder seeder;

  @Test
  void shouldRunSeedersWhenEnabled() {
    when(seeder.seed()).thenReturn(3);
    DataSeedInitializer initializer =
        new DataSeedInitializer(List.of(seeder), environment, true);

    initializer.run(null);

    verify(seeder).seed();
  }

  @Test
  void shouldNotRunSeedersWhenDisabled() {
    DataSeedInitializer initializer =
        new DataSeedInitializer(List.of(seeder), environment, false);

    initializer.run(null);

    verify(seeder, never()).seed();
  }

  @Test
  void shouldAbortStartupWhenSeederFailsOutsideTestProfile() {
    when(environment.getActiveProfiles()).thenReturn(new String[] {"local"});
    when(seeder.seed()).thenThrow(new IllegalStateException("boom"));
    when(seeder.name()).thenReturn("failing");
    DataSeedInitializer initializer =
        new DataSeedInitializer(List.of(seeder), environment, true);

    assertThatThrownBy(() -> initializer.run(null))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("boom");
  }

  @Test
  void shouldSwallowSeederFailureUnderTestProfile() {
    when(environment.getActiveProfiles()).thenReturn(new String[] {"test"});
    when(seeder.seed()).thenThrow(new IllegalStateException("boom"));
    when(seeder.name()).thenReturn("failing");
    DataSeedInitializer initializer =
        new DataSeedInitializer(List.of(seeder), environment, true);

    assertThatCode(() -> initializer.run(null)).doesNotThrowAnyException();
  }
}
