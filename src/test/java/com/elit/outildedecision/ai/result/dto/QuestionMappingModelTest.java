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

class QuestionMappingModelTest {

  private static final String ID = "ProjectComplexity";
  private static final String QUESTION_TEXT = "Quelle est la complexité du projet ?";
  private static final Integer QUESTION_NUMBER = 1;
  private static final int RESPONSE_SIZE = 3;
  private static final String JSON_PATH = "com/elit/outildedecision/model/QuestionMappingModelTest.json";

  @Test
  void testWellFormedInstance() throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();

    InputStream inputStream = Objects.requireNonNull(classLoader.getResourceAsStream(JSON_PATH));

    ObjectMapper mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addDeserializer(MethodologyType.class, new RecommendationIaMethodologyDeserializer());
    mapper.registerModule(module);

    QuestionMappingModel questionMappingModel = mapper.readValue(inputStream, QuestionMappingModel.class);
    assertThat(questionMappingModel).isNotNull();
    assertThat(questionMappingModel.getId()).isEqualTo(ID);
    assertThat(questionMappingModel.getQuestionText()).isEqualTo(QUESTION_TEXT);
    assertThat(questionMappingModel.getQuestionNumber()).isEqualTo(QUESTION_NUMBER);
    assertThat(questionMappingModel.getResponses()).hasSize(RESPONSE_SIZE);
  }

}
