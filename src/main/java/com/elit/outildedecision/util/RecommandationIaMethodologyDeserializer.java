package com.elit.outildedecision.util;

import com.elit.outildedecision.enumeration.MethodologyType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Custom deserializer used to parse the file RecommandationIA.json to type-bound recommandation id.
 */
public class RecommandationIaMethodologyDeserializer extends StdDeserializer<MethodologyType> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RecommandationIaMethodologyDeserializer.class);

  /**
   * @inheritDoc
   */
  public RecommandationIaMethodologyDeserializer() {
    this(null);
  }

  /**
   * @inheritDoc
   * @param vc the type
   */
  public RecommandationIaMethodologyDeserializer(final Class<?> vc) {
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
    LOGGER.error("Unknown methodology, tried parsing {}", node.asText());

    MethodologyType defaut = MethodologyType.values()[0];
    LOGGER.warn("Continuing with default methodology {}", defaut);
    return defaut;
  }
}
