package com.elit.outildedecision.shared.config.seed;

import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.user.repository.UserRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds the well-known demo accounts (same accounts as the historical dev seed) so that every
 * environment starts with usable logins.
 *
 * <p>Idempotent: each account is inserted only when its email is not already present, so a restart
 * neither duplicates nor overwrites users. Passwords are hashed with the application
 * {@link PasswordEncoder} (BCrypt), exactly as the rest of the application stores them.</p>
 *
 * <p>Can be turned off independently of reference data via {@code app.seed.demo.enabled} (default
 * {@code true}); it is disabled in the {@code test} profile, where each test seeds its own data.</p>
 */
@Component
@Slf4j
public final class DemoDataSeeder implements DataSeeder {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final boolean enabled;
  private final String demoPassword;

  /**
   * Builds the demo seeder.
   *
   * @param userRepository  repository used to check existence and persist demo users
   * @param passwordEncoder encoder used to hash the demo password
   * @param enabled         whether demo seeding is enabled ({@code app.seed.demo.enabled})
   * @param demoPassword    clear-text password shared by all demo accounts, supplied via
   *                        {@code app.seed.demo.password} (externalised, never hard-coded)
   */
  public DemoDataSeeder(final UserRepository userRepository,
                        final PasswordEncoder passwordEncoder,
                        @Value("${app.seed.demo.enabled:true}") final boolean enabled,
                        @Value("${app.seed.demo.password}") final String demoPassword) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.enabled = enabled;
    this.demoPassword = demoPassword;
  }

  /** {@inheritDoc} */
  @Override
  public String name() {
    return "demo-users";
  }

  /** {@inheritDoc} */
  @Override
  public int seed() {
    if (!enabled) {
      log.info("Seed [{}] skipped: disabled by app.seed.demo.enabled=false", name());
      return 0;
    }

    int inserted = 0;
    for (final DemoUser demoUser : demoUsers()) {
      if (userRepository.existsByUserEmail(demoUser.email())) {
        log.debug("Seed [{}]: account {} already present, skipping", name(), demoUser.email());
        continue;
      }
      userRepository.save(toEntity(demoUser));
      inserted++;
      log.debug("Seed [{}]: created account {}", name(), demoUser.email());
    }
    return inserted;
  }

  private User toEntity(final DemoUser demoUser) {
    return User.builder()
        .username(demoUser.username())
        .userFirstName(demoUser.firstName())
        .userLastName(demoUser.lastName())
        .userEmail(demoUser.email())
        .userPassword(passwordEncoder.encode(demoPassword))
        .isEmailVerified(true)
        .isUserActive(true)
        .isAdmin(demoUser.admin())
        .build();
  }

  private List<DemoUser> demoUsers() {
    return List.of(
        new DemoUser("user", "John", "Doe", "user@user.com", false),
        new DemoUser("admin", "Nancy", "Michel", "admin@elite.fr", true),
        new DemoUser("contributer", "Charles", "Baker", "contributer@elit.com", false),
        new DemoUser("observer", "Rochelle", "Wright", "observer@elit.com", false));
  }

  /**
   * Immutable description of a demo account to seed.
   */
  private record DemoUser(String username, String firstName, String lastName, String email,
                          boolean admin) {
  }
}
