package com.elit.outildedecision.model;

import com.elit.outildedecision.enumeration.MethodologyType;
import com.elit.outildedecision.util.RecommandationIaMethodologyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    module.addDeserializer(MethodologyType.class, new RecommandationIaMethodologyDeserializer());
    mapper.registerModule(module);

    QuestionMappingModel questionMappingModel = mapper.readValue(inputStream, QuestionMappingModel.class);
    assertNotNull(questionMappingModel);
    assertEquals(ID, questionMappingModel.getId());
    assertEquals(QUESTION_TEXT, questionMappingModel.getQuestionText());
    assertEquals(QUESTION_NUMBER, questionMappingModel.getQuestionNumber());
    assertEquals(RESPONSE_SIZE, questionMappingModel.getResponses().size());
  }

}
