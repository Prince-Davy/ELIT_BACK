package com.elit.outildedecision.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Low-level reactive HTTP client wrapper centralizing base URL, headers, and error handling
 * for outbound calls. Built on top of Spring WebClient. Intended to be stateless and reusable.
 */
@Service
@Slf4j
public class HttpClientService {

  private final WebClient webClient;
  private final String baseUrl;

  /**
   * Initializes the HTTP client with the provided WebClient {@code Builder} and base URL.
   *
   * @param webClientBuilder a preconfigured WebClient builder (e.g., codecs, filters, timeouts); must not be {@code null}
   * @param baseUrl the base URL to prefix to relative endpoints; must not be {@code null} or blank
   */
  public HttpClientService(final WebClient.Builder webClientBuilder,
                                   final @Value("${ia.prediction.api.base.url}") String baseUrl) {
    this.baseUrl = baseUrl;
    this.webClient = webClientBuilder.baseUrl(baseUrl).build();
  }

  /**
   * Generic method to send HTTP requests.
   *
   * @param method      HTTP method (GET, POST, PUT, DELETE)
   * @param endpoint    endpoint path relative to base URL
   * @param requestBody request body (for POST, PUT), null otherwise
   * @param responseType expected response class type
   * @param <T>         type of the response
   * @return Mono<T> reactive response
   */
  public <T> Mono<T> sendRequest(final HttpMethod method, final String endpoint, final Object requestBody, final Class<T> responseType) {
    String fullUrl = baseUrl + endpoint;
    log.info("{} request to {}", method.name(), fullUrl);

    WebClient.RequestBodySpec requestSpec = webClient.method(method).uri(endpoint);

    if (requestBody != null && (method == HttpMethod.POST || method == HttpMethod.PUT)) {
      requestSpec = (WebClient.RequestBodySpec) requestSpec.bodyValue(requestBody);
    }

    return requestSpec
      .retrieve()
      .bodyToMono(responseType)
      .doOnSuccess(response -> log.info("Successfully received response from {}", fullUrl))
      .doOnError(error -> {
        if (error instanceof WebClientResponseException) {
          WebClientResponseException ex = (WebClientResponseException) error;
          log.error("HTTP error {} during call to {}: {}", ex.getRawStatusCode(), fullUrl, ex.getResponseBodyAsString());
        } else {
          log.error("Error during call to {}: {}", fullUrl, error.getMessage());
        }
      });
  }
}
