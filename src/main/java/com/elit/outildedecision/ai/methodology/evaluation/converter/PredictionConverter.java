package com.elit.outildedecision.ai.methodology.evaluation.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;
import java.util.Map;

/** Prediction converter class. */
@Slf4j
@Converter
public class PredictionConverter implements AttributeConverter<Map<String, Double>, String> {

  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * convertToDatabaseColumn.
   *
   * @param prediction prediction
   * @return json
   */
  @Override
  public String convertToDatabaseColumn(final Map<String, Double> prediction) {
    try {
      return objectMapper.writeValueAsString(prediction);
    } catch (final JsonProcessingException e) {
      log.error("JSON writing error", e);
      throw new IllegalArgumentException("Could not convert map to JSON", e);
    }
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
