package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.AnswerIa;
import com.elit.outildedecision.entity.ResultAi;
import com.elit.outildedecision.exception.AnswerIaNotFoundException;
import com.elit.outildedecision.exception.ResultAiNotFoundException;
import com.elit.outildedecision.model.QuestionMappingModelKiviat;
import com.elit.outildedecision.model.RecommandationKiviatModel;
import com.elit.outildedecision.model.ThemeModelKiviatId;
import com.elit.outildedecision.repository.AnswerIaRepository;
import com.elit.outildedecision.repository.ResultIaRepository;
import com.elit.outildedecision.service.KiviatService;
import com.elit.outildedecision.util.MethodologyType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @inheritDoc .
 */
@Service
public class KiviatServiceImpl implements KiviatService {

  private static final Logger LOGGER = LoggerFactory.getLogger(KiviatServiceImpl.class);
  private final AnswerIaRepository answerIaRepository;
  private final ResultIaRepository resultIaRepository;

  @Value("${file.kiviatProject-file}")
  private String kiviatProjectFile;

  private final ResourceLoader resourceLoader;

  private List<QuestionMappingModelKiviat> questions;
  /**
   * Post bean construct init to load json file from disk.
   */
  @PostConstruct
  void init() {
    try (InputStream is = resourceLoader.getResource(kiviatProjectFile).getInputStream()) {
      ObjectMapper mapper = new ObjectMapper();
      TypeReference<List<QuestionMappingModelKiviat>> questionListTypeReference = new TypeReference<>() {
      };

      questions = mapper.readValue(is, questionListTypeReference);
    } catch (Exception e) {
      LOGGER.info("Tried to lookup file at : {}", kiviatProjectFile);
      LOGGER.error("Startup error, cannot read json kiviatProjectFile file: ", e);
      LOGGER.error("Continuing without json kiviatProjectFile, all subsequent call will result in no data");
      questions = new ArrayList<>();
    }
  }

  /**
   * Constructor for KiviatServiceImpl.
   * @param answerIaRepository get answers from users
   * @param resultIaRepository get methodology choice from AI
   * @param resourceLoader the resource loader
   */
  public KiviatServiceImpl(final AnswerIaRepository answerIaRepository,
                           final ResultIaRepository resultIaRepository,
                           final ResourceLoader resourceLoader) {
    this.answerIaRepository = answerIaRepository;
    this.resultIaRepository = resultIaRepository;
    this.resourceLoader = resourceLoader;
  }


  /**
   * @inheritDoc.
   */
  @Override
  public ResponseEntity<RecommandationKiviatModel> getKiviatMethodologyValues(final Long projectId) throws ResultAiNotFoundException {
    RecommandationKiviatModel result;

    // Parse methodology type, fail-fast if doesn't exists

    ResultAi resultAi = resultIaRepository.findByProject_ProjectId(projectId)
      .orElseThrow(() -> new ResultAiNotFoundException("No result ai found for this projectId"));

    MethodologyType aiResultMethodology = MethodologyType.find(resultAi.getPredictedMethod());


    switch (aiResultMethodology) {
      case HYBRID:
        result = new RecommandationKiviatModel(aiResultMethodology, 3, 2.5, 2.5, 3, 3);
        break;
      case AGILE:
        result = new RecommandationKiviatModel(aiResultMethodology, 5, 4.5, 1.5, 1, 5);
        break;
      case CLASSIC:
        result = new RecommandationKiviatModel(aiResultMethodology, 1, 1.5, 4.5, 5, 1);
        break;
        default:
          LOGGER.error("Problem with the methodology answer: {}", aiResultMethodology);
          throw new IllegalArgumentException("Unknown methodology type: " + aiResultMethodology); //Should Never happen

    }
    return ResponseEntity.ok().body(result);
  }


  /**
   * @inheritDoc .
   */
  @Override
  public ResponseEntity<RecommandationKiviatModel> getKiviatProjetValues(final Long projectId) throws  AnswerIaNotFoundException {
    RecommandationKiviatModel result = new RecommandationKiviatModel();
    // Créer une HashMap pour stocker les en-têtes et leurs valeurs
    Map<ThemeModelKiviatId, List<Integer>> table = new HashMap<>();
    //Attribution des entetes
    for (ThemeModelKiviatId theme : ThemeModelKiviatId.values()) {
      table.put(theme, new ArrayList<>());
    }
    //Récuperation  des réponses du projet
    List<AnswerIa> answersIas = answerIaRepository.findByProject_ProjectId(projectId);
    if (answersIas == null || answersIas.isEmpty()) {
      LOGGER.error("Answers IA not found: {}, projectID : {}", answersIas, projectId);
      throw new AnswerIaNotFoundException("No answer ai found for this projectId");
    }

    //Parcours le Json KiviatProjectMatrix
    questions.forEach(q ->
      answersIas.stream()
        .filter(a -> q.getQuestionNumber().toString().equals(a.getQuestion()))
        .forEach(a ->
          q.getResponses().stream()
            .filter(resp -> resp.getLabel().equals(a.getAnswer()))
            .forEach(r -> table.get(q.getTheme()).add(r.getNote()))
        )
    );

    LOGGER.info("Matrix filtered ok");
    LOGGER.debug("Values of categories" + table);


    Map<String, Double> averages = new HashMap<>();
    table.forEach((key, value) -> {
      double average = value.stream().mapToInt(Integer::intValue).average().orElse(0);
      average = Math.round(average * 100.0) / 100.0;
      averages.put(String.valueOf(key), average);
    });

    LOGGER.debug("Values of averages : {}", averages);

    result.setFlexibiliteSolution(averages.getOrDefault("FlexibiliteSolution", 0.0));
    result.setConfianceEtDelegation(averages.getOrDefault("ConfianceEtDelegation", 0.0));
    result.setFlexibiliteGestion(averages.getOrDefault("FlexibiliteGestion", 0.0));
    result.setCoordination(averages.getOrDefault("Coordination", 0.0));
    result.setMaitrisePerimetre(averages.getOrDefault("MaitrisePerimetre", 0.0));

    return ResponseEntity.ok().body(result);
  }
}
