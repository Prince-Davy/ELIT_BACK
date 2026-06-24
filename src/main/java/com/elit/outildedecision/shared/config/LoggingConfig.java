package com.elit.outildedecision.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

/**
 * Configuration class for HTTP request logging.
 * Following Single Responsibility Principle - handles only logging
 * configuration.
 * Enables detailed, non-truncated HTTP request and response logging.
 */
@Configuration
@Profile("local") // Active uniquement en environnement local
public class LoggingConfig {

  /**
   * Creates and configures a CommonsRequestLoggingFilter for detailed HTTP
   * logging.
   * Following Open/Closed Principle - configurable without modifying core
   * functionality.
   *
   * @return configured CommonsRequestLoggingFilter instance
   */
  @Bean
  public CommonsRequestLoggingFilter requestLoggingFilter() {
    CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter(); // Configuration pour logs complets
                                                                                   // non-tronqués
    loggingFilter.setIncludeQueryString(true); // Inclut query parameters
    loggingFilter.setIncludePayload(true); // Inclut body de la requête
    loggingFilter.setIncludeHeaders(true); // Inclut tous les headers

    // Définit une taille maximale très grande pour éviter la troncature (1MB)
    loggingFilter.setMaxPayloadLength(1000000);

    // Messages de log personnalisés
    loggingFilter.setBeforeMessagePrefix("==== HTTP REQUEST START ====\n");
    loggingFilter.setBeforeMessageSuffix("\n==== HTTP REQUEST END ====");
    loggingFilter.setAfterMessagePrefix("==== HTTP RESPONSE START ====\n");
    loggingFilter.setAfterMessageSuffix("\n==== HTTP RESPONSE END ====");

    return loggingFilter;
  }
}
