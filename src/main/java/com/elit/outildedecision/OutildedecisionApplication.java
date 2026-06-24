
package com.elit.outildedecision;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Application entry point.
 *
 * <p>Startup data seeding is handled idempotently by
 * {@link com.elit.outildedecision.shared.config.seed.DataSeedInitializer} (an {@code ApplicationRunner}),
 * not from {@code main}.</p>
 */
@SpringBootApplication
public class OutildedecisionApplication {

  /**
   * Starts the application.
   *
   * @param args the arguments passed to the application
   */
  public static void main(final String[] args) {
    SpringApplication.run(OutildedecisionApplication.class, args);
  }

  /**
   * Provides the application {@link PasswordEncoder} (BCrypt).
   *
   * @return the password encoder bean
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
