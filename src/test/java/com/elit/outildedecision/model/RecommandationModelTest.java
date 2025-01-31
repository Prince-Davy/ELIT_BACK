package com.elit.outildedecision.model;

import com.elit.outildedecision.util.MethodologyType;
import com.elit.outildedecision.util.RecommandationIaMethodologyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RecommandationModelTest {

  @Test
  void testWellFormedInstance() throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    String jsonPath = "com/elit/outildedecision/model/RecommandationModelTest.json";

    String expectedTitle = "Niveau d'implication des participants - Moyen";
    String expectedDescription = "La méthodologie agile peut convenir à une équipe pouvant s'adapter aux changements, car elle favorise une approche itérative et flexible, permettant une adaptation continue.";
    RecommandationLevel expectedLevel = RecommandationLevel.Yellow;

    InputStream inputStream = Objects.requireNonNull(classLoader.getResourceAsStream(jsonPath));

    ObjectMapper mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addDeserializer(MethodologyType.class, new RecommandationIaMethodologyDeserializer());
    mapper.registerModule(module);

    RecommandationModel recommandationModel = mapper.readValue(inputStream, RecommandationModel.class);
    assertNotNull(recommandationModel);
    assertEquals(MethodologyType.AGILE.toString(), recommandationModel.getId().toString());
    assertEquals(expectedTitle, recommandationModel.getTitle());
    assertEquals(expectedDescription, recommandationModel.getDescription());
    assertEquals(expectedLevel, recommandationModel.getLevel());
  }

}
