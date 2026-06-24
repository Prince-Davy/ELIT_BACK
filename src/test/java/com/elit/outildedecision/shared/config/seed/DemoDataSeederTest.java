package com.elit.outildedecision.shared.config.seed;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class DemoDataSeederTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  private static final String DEMO_PASSWORD = "Azerty1234!?";

  @Test
  void shouldInsertAllDemoAccountsWhenDatabaseIsEmpty() {
    when(userRepository.existsByUserEmail(anyString())).thenReturn(false);
    when(passwordEncoder.encode(anyString())).thenReturn("hashed");
    DemoDataSeeder seeder = new DemoDataSeeder(userRepository, passwordEncoder, true, DEMO_PASSWORD);

    int inserted = seeder.seed();

    assertThat(inserted).isEqualTo(4);
    verify(userRepository, times(4)).save(org.mockito.ArgumentMatchers.any(User.class));
  }

  @Test
  void shouldHashPasswordAndNeverStoreClearTextWhenSeeding() {
    when(userRepository.existsByUserEmail(anyString())).thenReturn(false);
    when(passwordEncoder.encode(anyString())).thenReturn("hashed");
    DemoDataSeeder seeder = new DemoDataSeeder(userRepository, passwordEncoder, true, DEMO_PASSWORD);

    seeder.seed();

    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(userRepository, times(4)).save(captor.capture());
    assertThat(captor.getAllValues())
        .allSatisfy(user -> assertThat(user.getUserPassword()).isEqualTo("hashed"));
  }

  @Test
  void shouldSkipExistingAccountsToStayIdempotent() {
    when(userRepository.existsByUserEmail("user@user.com")).thenReturn(true);
    when(userRepository.existsByUserEmail("admin@elite.fr")).thenReturn(true);
    when(userRepository.existsByUserEmail("contributer@elit.com")).thenReturn(false);
    when(userRepository.existsByUserEmail("observer@elit.com")).thenReturn(false);
    when(passwordEncoder.encode(anyString())).thenReturn("hashed");
    DemoDataSeeder seeder = new DemoDataSeeder(userRepository, passwordEncoder, true, DEMO_PASSWORD);

    int inserted = seeder.seed();

    assertThat(inserted).isEqualTo(2);
    verify(userRepository, times(2)).save(org.mockito.ArgumentMatchers.any(User.class));
  }

  @Test
  void shouldInsertNothingWhenAllAccountsAlreadyExist() {
    when(userRepository.existsByUserEmail(anyString())).thenReturn(true);
    DemoDataSeeder seeder = new DemoDataSeeder(userRepository, passwordEncoder, true, DEMO_PASSWORD);

    int inserted = seeder.seed();

    assertThat(inserted).isZero();
    verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any(User.class));
  }

  @Test
  void shouldDoNothingWhenDisabled() {
    DemoDataSeeder seeder = new DemoDataSeeder(userRepository, passwordEncoder, false, DEMO_PASSWORD);

    int inserted = seeder.seed();

    assertThat(inserted).isZero();
    verify(userRepository, never()).existsByUserEmail(anyString());
    verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any(User.class));
  }
}
