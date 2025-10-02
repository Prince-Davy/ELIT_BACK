package com.elit.outildedecision.ai.methodology.evaluation.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    String predictionJson = null;
    try {
      predictionJson = objectMapper.writeValueAsString(prediction);
    } catch (final JsonProcessingException e) {
      log.error("JSON writing error", e);
      throw new IllegalArgumentException("Could not convert map to JSON", e);
    }
    return predictionJson;
  }

  /**
   * convertToEntityAttribute.
   *
   * @param predictionJson predictionJson
   * @return prediction
   */
  @Override
  public Map<String, Double> convertToEntityAttribute(final String predictionJson) {
    Map<String, Double> prediction = null;
    try {
      prediction = objectMapper.readValue(predictionJson, new TypeReference<HashMap<String, Double>>() {
      });
    } catch (final IOException e) {
      log.error("JSON reading error", e);
      throw new IllegalArgumentException("Could not convert map to JSON", e);
    }
    return prediction;
  }
}
