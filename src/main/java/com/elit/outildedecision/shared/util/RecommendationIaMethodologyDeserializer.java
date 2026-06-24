package com.elit.outildedecision.shared.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Custom deserializer used to parse the recommendations JSON file to a type-bound recommendation id.
 */
@Slf4j
public class RecommendationIaMethodologyDeserializer extends StdDeserializer<MethodologyType> {

  /**
   * @inheritDoc
   */
  public RecommendationIaMethodologyDeserializer() {
    this(null);
  }

  /**
   * @inheritDoc
   * @param vc the type
   */
  public RecommendationIaMethodologyDeserializer(final Class<?> vc) {
    super(vc);
  }

  /**
   * Custom deserializer for MethodologyTypes used in id field of JSON file.
   *
   * @param parser Parsed used for reading JSON content
   * @param ctxt Context that can be used to access information about
   *   this deserialization activity.
   *
   * @return the associated MethodologyTypes. If no MethdologyTypes is found, defaults to agile
   * @throws IOException when the JSONParser encounters an error parsing the JSON
   */
  @Override
  public MethodologyType deserialize(final JsonParser parser, final DeserializationContext ctxt) throws IOException {

    JsonNode node = parser.getCodec().readTree(parser);

    for (MethodologyType type : MethodologyType.values()) {
      if (type.toString().equals(node.asText())) {
        return type;
      }
    }
    log.error("Unknown methodology, tried parsing {}", node.asText());

    MethodologyType defaut = MethodologyType.values()[0];
    log.warn("Continuing with default methodology {}", defaut);
    return defaut;
  }
}
