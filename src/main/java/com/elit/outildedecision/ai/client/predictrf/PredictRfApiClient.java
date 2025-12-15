package com.elit.outildedecision.ai.client.predictrf;

import com.elit.outildedecision.ai.client.predictrf.dto.PredictRfRequest;
import com.elit.outildedecision.ai.client.predictrf.dto.PredictRfResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@RequiredArgsConstructor
@Component
public class PredictRfApiClient {
  private final WebClient aiWebClient;

  @Value("${ia.prediction.api.endpoint.process-predict-rf}")
  private String endpointPredictRf;

  /**
   * Call AI API predict rf.
   *
   * @param request request
   * @return response
   */
  public PredictRfResponse predict(final PredictRfRequest request) {
    log.debug("" + request);
    return aiWebClient.post()
      .uri(endpointPredictRf)
      .bodyValue(request)
      .retrieve()
      .onStatus(
        HttpStatusCode::is4xxClientError, response ->
          response.bodyToMono(String.class)
            .map(body -> {
              log.error("422 Error body: {}", body);
              return new RuntimeException("422 Error: " + body);
            })
      )
      .bodyToMono(PredictRfResponse.class)
      .block();
  }
}
