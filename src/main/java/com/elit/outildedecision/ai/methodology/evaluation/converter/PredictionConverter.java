package com.elit.outildedecision.ai.methodology.evaluation.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Converter
public class PredictionConverter implements AttributeConverter<Map<String, Double>, String> {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * convertToDatabaseColumn.
   *
   * @param prediction prediction
   * @return json
   */
  @SneakyThrows
  @Override
  public String convertToDatabaseColumn(final Map<String, Double> prediction) {
    return objectMapper.writeValueAsString(prediction);
  }

  /**
   * convertToEntityAttribute.
   *
   * @param predictionJson predictionJson
   * @return prediction
   */
  @Override
  public Map<String, Double> convertToEntityAttribute(final String predictionJson) {
    try {
      return objectMapper.readValue(
        predictionJson,
        objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Double.class)
      );
    } catch (final IOException e) {
      log.error("JSON reading error", e);
      throw new IllegalArgumentException("Could not convert JSON to map", e);
    }
  }
}
