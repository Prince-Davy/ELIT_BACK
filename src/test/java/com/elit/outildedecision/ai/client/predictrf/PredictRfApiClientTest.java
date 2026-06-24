package com.elit.outildedecision.ai.client.predictrf;

import com.elit.outildedecision.ai.client.predictrf.dto.MethodologyEvaluationExternalDto;
import com.elit.outildedecision.ai.client.predictrf.dto.PredictRfRequest;
import com.elit.outildedecision.ai.client.predictrf.dto.PredictRfResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ExtendWith(MockitoExtension.class)
class PredictRfApiClientTest {

  private static MockWebServer mockWebServer;
  private PredictRfApiClient apiClient;

  @BeforeAll
  static void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();
  }

  @AfterAll
  static void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @BeforeEach
  void initialize() {
    String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
    apiClient = new PredictRfApiClient(WebClient.builder().baseUrl(baseUrl).build());
  }

  @SneakyThrows
  @Test
  void testPostPredictRf() {
    // Arrange
    MethodologyEvaluationExternalDto agile = MethodologyEvaluationExternalDto.builder()
      .predictedMethodology("Agile")
      .predictedScore(0.92)
      .featureImportante(Map.of("team_size", 0.5, "project_duration", 0.42))
      .build();

    MethodologyEvaluationExternalDto waterfall = MethodologyEvaluationExternalDto.builder()
      .predictedMethodology("Waterfall")
      .predictedScore(0.75)
      .featureImportante(Map.of("budget", 0.6, "risk_level", 0.3))
      .build();

    PredictRfResponse responseObject = PredictRfResponse.builder()
      .predictions(List.of(agile, waterfall))
      .build();

    ObjectMapper objectMapper = new ObjectMapper();
    String jsonResponse = objectMapper.writeValueAsString(responseObject);

    mockWebServer.enqueue(new MockResponse()
      .setBody(jsonResponse)
      .addHeader("Content-Type", "application/json"));

    // Act
    PredictRfResponse response = apiClient.predict(new PredictRfRequest());

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.getPredictions()).hasSize(2);
    assertThat(response.getPredictions().get(0).getPredictedMethodology()).isEqualTo("Agile");
    assertThat(response.getPredictions().get(1).getPredictedMethodology()).isEqualTo("Waterfall");
  }

  @SneakyThrows
  @Test
  void testPostPredictRf_shouldHandle422Error() {
    // Arrange
    mockWebServer.enqueue(new MockResponse()
      .setResponseCode(422)
      .setBody("Invalid input data")
      .addHeader("Content-Type", "text/plain"));

    // Act & Assert
    assertThatExceptionOfType(RuntimeException.class)
        .isThrownBy(() -> apiClient.predict(new PredictRfRequest()));
  }
}
