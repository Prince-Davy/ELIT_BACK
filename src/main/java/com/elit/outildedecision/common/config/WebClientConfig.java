package com.elit.outildedecision.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Common WebClient configuration.
 *
 * Provides shared HTTP client components used across the application.
 * This configuration class is not designed for inheritance.
 */
@Configuration
public class WebClientConfig {

  /**
   * Creates a reusable WebClient.Builder bean.
   *
   * Intended to be injected and optionally customized before building a WebClient
   * instance. Prefer composition over inheritance for further customization.
   *
   * @return a preconfigured WebClient.Builder
   */
  @Bean
  public WebClient.Builder webClientBuilder() {
    return WebClient.builder();
  }
}
