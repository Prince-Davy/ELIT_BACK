package com.elit.outildedecision.service.implementation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.elit.outildedecision.ai.answer.entity.AiAnswer;
import com.elit.outildedecision.entity.ResultAi;
import com.elit.outildedecision.model.RecommandationItemModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;

import org.springframework.core.io.ResourceLoader;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests simplifiés et mis à jour pour RecommandationServiceImpl.
 */
class RecommandationServiceImplTest {

  // Ressources de test existantes (présumées dans le classpath du projet)
  private static final String VALID_JSON = "classpath:com/elit/outildedecision/service/implementation/RecommandationsIATest.json";
  private static final String INVALID_JSON = "classpath:com/elit/outildedecision/service/implementation/RecommandationsIATest_invalid.json";
  private static final String INVALID_PATH = "classpath:does/not/exist.json";

  private ResourceLoader resourceLoader;
  private RecommandationServiceImpl service;

  @BeforeEach
  void setUp() {
    resourceLoader = new DefaultResourceLoader();
    service = new RecommandationServiceImpl(resourceLoader);
  }

  private void setJsonPath(String path) {
    try {
      Field f = RecommandationServiceImpl.class.getDeclaredField("recommandationsFile");
      f.setAccessible(true);
      f.set(service, path);
    } catch (Exception e) {
      fail("Impossible d'injecter le chemin du JSON dans le service: " + e.getMessage());
    }
  }

  @Nested
  @DisplayName("Init du service")
  class InitTests {

    @Test
    @DisplayName("init() avec un JSON valide - smoke test")
    void init_ok_smoke() {
      setJsonPath(VALID_JSON);
      assertDoesNotThrow(() -> service.init(), "L'initialisation avec un JSON valide ne doit pas lever d'exception");
    }

    @Nested
    @DisplayName("Cas de garde (entrées nulles/vides)")
    class GuardClauseTests {

      @Test
      @DisplayName("answers == null -> liste vide")
      void getMappings_answersNull() {
        setJsonPath(VALID_JSON);
        service.init();

        ResultAi resultAi = new ResultAi();
        resultAi.setPredictedMethod("ANY");

        List<RecommandationItemModel> body = service.getMappings(null, resultAi).getBody();
        assertNotNull(body);
        assertTrue(body.isEmpty(), "Lorsque la liste de réponses est nulle, le service doit renvoyer une liste vide");
      }

      @Test
      @DisplayName("answers vide -> liste vide")
      void getMappings_answersEmpty() {
        setJsonPath(VALID_JSON);
        service.init();

        ResultAi resultAi = new ResultAi();
        resultAi.setPredictedMethod("ANY");

        List<RecommandationItemModel> body = service.getMappings(Collections.emptyList(), resultAi).getBody();
        assertNotNull(body);
        assertTrue(body.isEmpty(), "Lorsque la liste de réponses est vide, le service doit renvoyer une liste vide");
      }

      @Test
      @DisplayName("resultAi == null -> liste vide")
      void getMappings_resultAiNull() {
        setJsonPath(VALID_JSON);
        service.init();

        AiAnswer answer = new AiAnswer();
        answer.setQuestion("Q1");
        answer.setAnswer("1");

        List<RecommandationItemModel> body = service.getMappings(Collections.singletonList(answer), null).getBody();
        assertNotNull(body);
        assertTrue(body.isEmpty(), "Lorsque resultAi est nul, le service doit renvoyer une liste vide");
      }

      @Test
      @DisplayName("resultAi.predictedMethod == null -> liste vide")
      void getMappings_predictedMethodNull() {
        setJsonPath(VALID_JSON);
        service.init();

        AiAnswer answer = new AiAnswer();
        answer.setQuestion("Q1");
        answer.setAnswer("1");

        ResultAi resultAi = new ResultAi();
        resultAi.setPredictedMethod(null);

        List<RecommandationItemModel> body = service.getMappings(Collections.singletonList(answer), resultAi).getBody();
        assertNotNull(body);
        assertTrue(body.isEmpty(), "Lorsque predictedMethod est nul, le service doit renvoyer une liste vide");
      }
    }
  }
}
