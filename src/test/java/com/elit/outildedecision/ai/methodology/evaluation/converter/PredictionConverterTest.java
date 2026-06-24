package com.elit.outildedecision.ai.methodology.evaluation.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class PredictionConverterTest {

  private final PredictionConverter converter = new PredictionConverter();

  @Test
  void shouldSerializeMapToJson() {
    // given: a LinkedHashMap keeps a deterministic order for the assertion
    Map<String, Double> prediction = new LinkedHashMap<>();
    prediction.put("Q1", 3.0);
    prediction.put("Q2", 1.5);

    // when
    String json = converter.convertToDatabaseColumn(prediction);

    // then
    assertThat(json).isEqualTo("{\"Q1\":3.0,\"Q2\":1.5}");
  }

  @Test
  void shouldDeserializeJsonToMap() {
    // when
    Map<String, Double> prediction = converter.convertToEntityAttribute("{\"Q1\":3.0,\"Q2\":1.5}");

    // then
    assertThat(prediction).containsExactlyInAnyOrderEntriesOf(Map.of("Q1", 3.0, "Q2", 1.5));
  }

  @Test
  void shouldRoundTripMap() {
    // given
    Map<String, Double> prediction = Map.of("Q1", 3.0, "Q2", 1.5);

    // when
    Map<String, Double> result =
        converter.convertToEntityAttribute(converter.convertToDatabaseColumn(prediction));

    // then
    assertThat(result).isEqualTo(prediction);
  }

  @Test
  void shouldThrowIllegalArgumentWhenJsonIsInvalid() {
    // when / then
    assertThatIllegalArgumentException()
        .isThrownBy(() -> converter.convertToEntityAttribute("not-json"));
  }
}
