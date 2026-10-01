package com.elit.outildedecision.shared.common.config;

import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

/**
 * Common WebClient configuration.
 * Provides shared HTTP client components used across the application.
 * This configuration class is not designed for inheritance.
 */
@Configuration
@Slf4j
public class WebClientConfig {

  @Value("${ia.prediction.ssl.trust-all:false}")
  private boolean trustAllCertificates;

  /**
   * Creates a reusable WebClient.Builder bean.
   * Intended to be injected and optionally customized before building a WebClient
   * instance. Prefer composition over inheritance for further customization.
   *
   * @return a preconfigured WebClient.Builder
   */
  @Bean
  public WebClient.Builder webClientBuilder() {
    WebClient.Builder builder = WebClient.builder();
    if (trustAllCertificates) {
      log.warn("TLS certificate verification is DISABLED for outbound AI prediction calls (ia.prediction.ssl.trust-all=true). "
        + "This must never be enabled outside local development.");
      builder.clientConnector(new ReactorClientHttpConnector(buildInsecureHttpClient()));
    }
    return builder;
  }

  private HttpClient buildInsecureHttpClient() {
    return HttpClient.create().secure(sslSpec -> {
      try {
        sslSpec.sslContext(SslContextBuilder.forClient()
          .trustManager(InsecureTrustManagerFactory.INSTANCE)
          .build());
      } catch (SSLException e) {
        throw new IllegalStateException("Unable to build insecure SSL context for local AI prediction client", e);
      }
    });
  }
}
