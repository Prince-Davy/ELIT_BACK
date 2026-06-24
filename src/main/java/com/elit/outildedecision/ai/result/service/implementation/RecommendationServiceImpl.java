package com.elit.outildedecision.ai.result.service.implementation;

import com.elit.outildedecision.ai.answer.entity.AiAnswer;
import com.elit.outildedecision.ai.result.dto.QuestionMappingModel;
import com.elit.outildedecision.ai.result.dto.RecommendationItemResponse;
import com.elit.outildedecision.ai.result.dto.RecommendationLevel;
import com.elit.outildedecision.ai.result.entity.ResultAi;
import com.elit.outildedecision.ai.result.service.RecommendationService;
import com.elit.outildedecision.shared.util.JsonResourceLoader;
import com.elit.outildedecision.shared.util.MethodologyType;
import com.elit.outildedecision.shared.util.RecommendationIaMethodologyDeserializer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

/**
 * Service de recommandations basé sur les réponses de l'IA et un fichier de mapping JSON.
 */
@Service
@Slf4j
public final class RecommendationServiceImpl implements RecommendationService {

  @Value("${file.recommandations-file}")
  private String recommendationsFile;

  private final JsonResourceLoader jsonResourceLoader;

  // Index pour accès direct par id de question
  private Map<String, QuestionMappingModel> questionIndex = new HashMap<>();

  /**
   * Construit une instance du service de recommandations.
   * <p>
   * Le chargement effectif des données et la création de l’index sont réalisés lors
   * de la phase d’initialisation du composant.
   *
   * @param jsonResourceLoader le chargeur de ressources JSON avec dégradation gracieuse
   */
  public RecommendationServiceImpl(final JsonResourceLoader jsonResourceLoader) {
    this.jsonResourceLoader = jsonResourceLoader;
  }

  /**
   * Charge le fichier JSON et prépare l'index des questions.
   */
  @PostConstruct
  void init() {
    final TypeReference<List<QuestionMappingModel>> questionListType = new TypeReference<>() { };
    final List<QuestionMappingModel> questionMappings = jsonResourceLoader.loadList(
        recommendationsFile, questionListType, mapper -> {
          final SimpleModule module = new SimpleModule();
          module.addDeserializer(MethodologyType.class, new RecommendationIaMethodologyDeserializer());
          mapper.registerModule(module);
        }, new ArrayList<>());

    questionIndex = questionMappings.stream()
      .collect(Collectors.toMap(QuestionMappingModel::getId, Function.identity()));
    log.info("Recommendation mappings indexed: {} questions", questionIndex.size());
  }

  /** {@inheritDoc} */
  @Override
  public List<RecommendationItemResponse> getMappings(final List<AiAnswer> answers, final ResultAi resultAi) {
    if (answers == null || answers.isEmpty() || resultAi == null || resultAi.getPredictedMethod() == null) {
      return new ArrayList<>();
    }

    MethodologyType aiResultMethodology = MethodologyType.find(resultAi.getPredictedMethod());
    List<RecommendationItemResponse> result = new ArrayList<>();

    for (AiAnswer answer : answers) {
      if (answer == null) {
        continue;
      }

      String questionId = answer.getQuestion();
      String responseId = answer.getAnswer();

      QuestionMappingModel question = questionIndex.get(questionId);
      if (question == null) {
        log.trace("No question found for id: {}", questionId);
      } else {
        question.getResponses().stream()
          .filter(r -> Objects.equals(String.valueOf(r.getId()), responseId))
          .findFirst()
          .ifPresentOrElse(response -> response.getRecommendations().stream()
            .filter(rec -> rec.getId().equals(aiResultMethodology))
            .map(rec -> new RecommendationItemResponse(
              rec.getLevel().toString(),
              rec.getTitle(),
              rec.getDescription()))
            .forEach(result::add),
            () -> log.trace("No response found for id: {} in question: {}", responseId, questionId));
      }
    }

    result.sort(Comparator.comparing(item -> RecommendationLevel.valueOf(item.getColor())));
    return result;
  }
}
