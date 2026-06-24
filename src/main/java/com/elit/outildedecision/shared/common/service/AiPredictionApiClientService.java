package com.elit.outildedecision.shared.common.service;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Service delegating AI prediction HTTP calls to a lower-level HTTP client.
 */
@Service
public class AiPredictionApiClientService {

  private final HttpClientService httpClientService;

  /**
   * Creates a new client service that delegates HTTP calls to the given {@code HttpClientService}.
   *
   * @param httpClientService the low-level HTTP client used to send requests; must not be {@code null}
   */
  public AiPredictionApiClientService(final HttpClientService httpClientService) {
    this.httpClientService = httpClientService;
  }

  /**
   * Sends a POST request to the specified endpoint with the given request body and returns the raw response.
   * Errors are propagated through the returned {@link Mono}.
   *
   * @param endpoint    the target endpoint URL or path
   * @param requestBody the request payload to serialize (e.g., a DTO or Map); may be {@code null} if allowed by the API
   * @return a {@link Mono} emitting the response body as a {@link String}, or an error if the request fails
   */
  public Mono<String> sendPostRequest(final String endpoint, final Object requestBody) {
    return httpClientService.sendRequest(HttpMethod.POST, endpoint, requestBody, String.class);
  }
}
