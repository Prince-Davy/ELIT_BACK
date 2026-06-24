package com.elit.outildedecision.ai.result.dto;

import com.elit.outildedecision.shared.util.MethodologyType;
import com.elit.outildedecision.shared.util.RecommendationIaMethodologyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class RecommendationModelTest {

  @Test
  void testWellFormedInstance() throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    String jsonPath = "com/elit/outildedecision/ai/result/recommendation-model-test.json";

    String expectedTitle = "Niveau d'implication des participants - Moyen";
    String expectedDescription = "La méthodologie agile peut convenir à une équipe pouvant s'adapter aux changements, car elle favorise une approche itérative et flexible, permettant une adaptation continue.";
    RecommendationLevel expectedLevel = RecommendationLevel.Yellow;

    InputStream inputStream = Objects.requireNonNull(classLoader.getResourceAsStream(jsonPath));

    ObjectMapper mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addDeserializer(MethodologyType.class, new RecommendationIaMethodologyDeserializer());
    mapper.registerModule(module);

    RecommendationModel recommendationModel = mapper.readValue(inputStream, RecommendationModel.class);
    assertThat(recommendationModel).isNotNull();
    assertThat(recommendationModel.getId().toString()).isEqualTo(MethodologyType.AGILE.toString());
    assertThat(recommendationModel.getTitle()).isEqualTo(expectedTitle);
    assertThat(recommendationModel.getDescription()).isEqualTo(expectedDescription);
    assertThat(recommendationModel.getLevel()).isEqualTo(expectedLevel);
  }

}
