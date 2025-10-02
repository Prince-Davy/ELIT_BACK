package com.elit.outildedecision.ai.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AiWebClientConfig {
  /**
   * web client for AI API.
   *
   * @param b       builder
   * @param baseUrl baseurl
   * @return web client
   */
  @Bean
  public WebClient aiWebClient(final WebClient.Builder b, final @Value("${ia.prediction.api.base.url}") String baseUrl) {
    return b.baseUrl(baseUrl).build();
  }
}
