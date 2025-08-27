package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.ai.answer.entity.AiAnswer;
import com.elit.outildedecision.entity.ResultAi;
import com.elit.outildedecision.model.QuestionMappingModel;
import com.elit.outildedecision.model.RecommandationItemModel;
import com.elit.outildedecision.model.RecommandationLevel;
import com.elit.outildedecision.service.RecommandationService;
import com.elit.outildedecision.util.MethodologyType;
import com.elit.outildedecision.util.RecommandationIaMethodologyDeserializer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Service de recommandations basé sur les réponses de l'IA et un fichier de mapping JSON.
 */
@Service
public final class RecommandationServiceImpl implements RecommandationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(RecommandationServiceImpl.class);

  @Value("${file.recommandations-file}")
  private String recommandationsFile;

  private final ResourceLoader resourceLoader;

  // Données chargées depuis le JSON
  private List<QuestionMappingModel> questionMappings = new ArrayList<>();

  // Index pour accès direct par id de question
  private Map<String, QuestionMappingModel> questionIndex = new HashMap<>();

  /**
   * Construit une instance du service de recommandations.
   * <p>
   * Ce constructeur injecte le {@code ResourceLoader} requis pour localiser et lire
   * les ressources nécessaires (notamment le fichier JSON de mappage des recommandations).
   * Le chargement effectif des données et la création de l’index sont réalisés lors
   * de la phase d’initialisation du composant.
   *
   * @param resourceLoader le chargeur de ressources utilisé pour accéder aux fichiers applicatifs
   */

  public RecommandationServiceImpl(final ResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader;
  }

  /**
   * Charge le fichier JSON et prépare l'index des questions.
   */
  @PostConstruct
  void init() {
    try (InputStream is = resourceLoader.getResource(recommandationsFile).getInputStream()) {
      ObjectMapper mapper = new ObjectMapper();
      SimpleModule module = new SimpleModule();
      module.addDeserializer(MethodologyType.class, new RecommandationIaMethodologyDeserializer());
      mapper.registerModule(module);

      TypeReference<List<QuestionMappingModel>> questionListType = new TypeReference<>() { };
      questionMappings = mapper.readValue(is, questionListType);

      questionIndex = questionMappings.stream()
        .collect(Collectors.toMap(QuestionMappingModel::getId, Function.identity()));
      LOGGER.info("Recommandation mappings loaded: {} questions indexed", questionIndex.size());

    } catch (Exception e) {
      LOGGER.info("Tried to lookup file at: {}", recommandationsFile);
      LOGGER.error("Startup error, cannot read json recommandation file", e);
      LOGGER.error("Continuing without json recommandations, all subsequent calls will result in no data");
      questionMappings = new ArrayList<>();
      questionIndex = new HashMap<>();
    }
  }

  @Override
  public ResponseEntity<List<RecommandationItemModel>> getMappings(final List<AiAnswer> answers, final ResultAi resultAi) {
    if (answers == null || answers.isEmpty() || resultAi == null || resultAi.getPredictedMethod() == null) {
      return ResponseEntity.ok(Collections.emptyList());
    }

    MethodologyType aiResultMethodology = MethodologyType.find(resultAi.getPredictedMethod());
    List<RecommandationItemModel> result = new ArrayList<>();

    for (AiAnswer answer : answers) {
      if (answer == null) {
        continue;
      }

      String questionId = answer.getQuestion();
      String responseId = answer.getAnswer();

      QuestionMappingModel question = questionIndex.get(questionId);
      if (question == null) {
        LOGGER.trace("No question found for id: {}", questionId);
        continue;
      }

      question.getResponses().stream()
        .filter(r -> Objects.equals(String.valueOf(r.getId()), responseId))
        .findFirst()
        .ifPresentOrElse(response -> {
          response.getRecommandations().stream()
            .filter(rec -> rec.getId().equals(aiResultMethodology))
            .map(rec -> new RecommandationItemModel(
              rec.getLevel().toString(),
              rec.getTitle(),
              rec.getDescription()))
            .forEach(result::add);
        }, () -> LOGGER.trace("No response found for id: {} in question: {}", responseId, questionId));
    }

    result.sort(Comparator.comparing(item -> RecommandationLevel.valueOf(item.getColor())));
    return ResponseEntity.ok(result);
  }
}
