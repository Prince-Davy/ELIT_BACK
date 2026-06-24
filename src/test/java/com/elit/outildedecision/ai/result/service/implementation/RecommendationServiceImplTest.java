package com.elit.outildedecision.ai.result.service.implementation;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import com.elit.outildedecision.ai.answer.entity.AiAnswer;
import com.elit.outildedecision.ai.result.dto.RecommendationItemResponse;
import com.elit.outildedecision.ai.result.entity.ResultAi;
import com.elit.outildedecision.shared.util.JsonResourceLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.fail;

/**
 * Tests simplifiés et mis à jour pour RecommendationServiceImpl.
 */
class RecommendationServiceImplTest {

  // Ressources de test existantes (présumées dans le classpath du projet)
  private static final String VALID_JSON = "classpath:com/elit/outildedecision/ai/result/recommendations-test.json";

  private RecommendationServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new RecommendationServiceImpl(new JsonResourceLoader(new DefaultResourceLoader()));
  }

  private void setJsonPath(String path) {
    try {
      Field f = RecommendationServiceImpl.class.getDeclaredField("recommendationsFile");
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
      assertThatCode(() -> service.init())
          .as("L'initialisation avec un JSON valide ne doit pas lever d'exception")
          .doesNotThrowAnyException();
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

        List<RecommendationItemResponse> body = service.getMappings(null, resultAi);
        assertThat(body)
            .as("Lorsque la liste de réponses est nulle, le service doit renvoyer une liste vide")
            .isEmpty();
      }

      @Test
      @DisplayName("answers vide -> liste vide")
      void getMappings_answersEmpty() {
        setJsonPath(VALID_JSON);
        service.init();

        ResultAi resultAi = new ResultAi();
        resultAi.setPredictedMethod("ANY");

        List<RecommendationItemResponse> body = service.getMappings(Collections.emptyList(), resultAi);
        assertThat(body)
            .as("Lorsque la liste de réponses est vide, le service doit renvoyer une liste vide")
            .isEmpty();
      }

      @Test
      @DisplayName("resultAi == null -> liste vide")
      void getMappings_resultAiNull() {
        setJsonPath(VALID_JSON);
        service.init();

        AiAnswer answer = new AiAnswer();
        answer.setQuestion("Q1");
        answer.setAnswer("1");

        List<RecommendationItemResponse> body = service.getMappings(Collections.singletonList(answer), null);
        assertThat(body)
            .as("Lorsque resultAi est nul, le service doit renvoyer une liste vide")
            .isEmpty();
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

        List<RecommendationItemResponse> body = service.getMappings(Collections.singletonList(answer), resultAi);
        assertThat(body)
            .as("Lorsque predictedMethod est nul, le service doit renvoyer une liste vide")
            .isEmpty();
      }
    }
  }
}
