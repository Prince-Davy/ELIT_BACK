package com.elit.outildedecision.ai.methodology.evaluation.converter;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PredictionConverterTest {

  private final PredictionConverter converter = new PredictionConverter();

  @Test
  void convertToDatabaseColumn_shouldSerializeMap() {
    Map<String, Double> prediction = Map.of("A", 0.5, "B", 0.8);
    String json = converter.convertToDatabaseColumn(prediction);

    assertTrue(json.contains("\"A\":0.5"));
    assertTrue(json.contains("\"B\":0.8"));
  }

  @Test
  void convertToEntityAttribute_shouldDeserializeJson() {
    String json = "{\"A\":0.5,\"B\":0.8}";
    Map<String, Double> result = converter.convertToEntityAttribute(json);

    assertEquals(0.5, result.get("A"));
    assertEquals(0.8, result.get("B"));
  }

  @Test
  void convertToEntityAttribute_shouldThrowExceptionOnInvalidJson() {
    String invalidJson = "{invalid json}";
    assertThrows(IllegalArgumentException.class, () -> converter.convertToEntityAttribute(invalidJson));
  }
}
