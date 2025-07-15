package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.AnswerIa;
import com.elit.outildedecision.entity.ResultAi;
import com.elit.outildedecision.exception.MalformedDataException;
import com.elit.outildedecision.model.QuestionMappingModel;
import com.elit.outildedecision.model.RecommandationItemModel;
import com.elit.outildedecision.model.RecommandationLevel;
import com.elit.outildedecision.model.RecommandationModel;
import com.elit.outildedecision.model.ResponseModel;
import com.elit.outildedecision.service.RecommandationService;
import com.elit.outildedecision.enumeration.MethodologyType;
import com.elit.outildedecision.util.RecommandationIaMethodologyDeserializer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @inheritDoc .
 */
@Service
public class RecommandationServiceImpl implements RecommandationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(RecommandationServiceImpl.class);

  public static final String NO_SUCH_QUESTION = "No such question in JSON matching : {0}";
  public static final String NO_SUCH_RESPONSE = "No such response in JSON matching : {0}";
  public static final String NO_SUCH_RECOMMANDATION = "No such recommandation in JSON matching : {0}";

  @Value("${file.recommandations-file}")
  private String recommandationsFile;

  private final ResourceLoader resourceLoader;

  private List<QuestionMappingModel> questions;

  /**
   * Constructor for RecommandationServiceImpl.
   *
   * @param resourceLoader the resource loader
   */
  public RecommandationServiceImpl(final ResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader;
  }

  /**
   * Post bean construct init to load json file from disk.
   * FIXME : This method is not meant to be visible / callable outside and thus is meant to be private,
   * FIXME : but making this method private will fail pmd verification and thus CI build. To fix this we should
   * FIXME : include a custom pmd rule. Since I can't make it work, the only solution I found is to make it
   * FIXME : package-private. If you can fix it you're a hero :-)
   */
  @PostConstruct
  void init() {
    try (InputStream is = resourceLoader.getResource(recommandationsFile).getInputStream()) {
      ObjectMapper mapper = new ObjectMapper();
      SimpleModule module = new SimpleModule();
      module.addDeserializer(MethodologyType.class, new RecommandationIaMethodologyDeserializer());
      mapper.registerModule(module);

      TypeReference<List<QuestionMappingModel>> questionListTypeReference = new TypeReference<>() {
        //
      };

      questions = mapper.readValue(is, questionListTypeReference);
    } catch (Exception e) {
      LOGGER.info("Tried to lookup file at : {}", recommandationsFile);
      LOGGER.error("Startup error, cannot read json recommandation file: ", e);
      LOGGER.error("Continuing without json recommandations, all subsequent call will result in no data");
      questions = new ArrayList<>();
    }
  }

  /**
   * @inheritDoc .
   */
  @Override
  public ResponseEntity<List<RecommandationItemModel>> getMappings(final List<AnswerIa> answers, final ResultAi resultAi) {
    List<RecommandationItemModel> result = new ArrayList<>();

    // Parse methodology type, fail-fast if doesn't exists
    MethodologyType aiResultMethodology = MethodologyType.find(resultAi.getPredictedMethod());

    // First, list all question numbers in Json
    List<Integer> questionNumbersInJson = questions.stream()
      .map(QuestionMappingModel::getQuestionNumber)
      .collect(Collectors.toList());

    // Filter all AnswersIa accordings to numbers present in Json
    List<AnswerIa> filteredAnswers = answers.stream()
      // contains performance is ok regarding question number (as of commit date)
      .filter(answer -> questionNumbersInJson.contains(Integer.valueOf(answer.getQuestion())))
      .collect(Collectors.toList());

    // construct result list by iterating on filtered answers
    filteredAnswers.forEach(answer -> {

      LOGGER.trace("Processing answer : {}", answer);

      QuestionMappingModel questionMappingModel = questions.stream()
        // Look for same question number
        .filter(question -> question.getQuestionNumber().equals(Integer.valueOf(answer.getQuestion())))
        // Fail-fast, should not happen
        .findFirst().orElseThrow(() -> new MalformedDataException(MessageFormat.format(NO_SUCH_QUESTION, answer.getQuestion())));

      LOGGER.trace("Found question mapping model : {}", questionMappingModel);

      ResponseModel responseModel = questionMappingModel
        .getResponses()
        .stream()
        // Look for label matching question -> need to harmonize labels ???
        .filter(response -> response.getLabel().equals(answer.getAnswer()))
        // Again, fail-fast, should not happen unless there is a mismatch in question labels. Need to store in DB?
        .findFirst().orElseThrow(() -> new MalformedDataException(MessageFormat.format(NO_SUCH_RESPONSE, answer.getAnswer())));

      LOGGER.trace("Found response model : {}", responseModel);

      RecommandationModel recommandationModel = responseModel
        .getRecommandations()
        .stream()
        // Look for recommandation matching AI result
        .filter(recommandation -> recommandation.getId().equals(aiResultMethodology))
        // Fail-fast (even at last ;p), should not happen
        .findFirst().orElseThrow(() -> new MalformedDataException(MessageFormat.format(NO_SUCH_RECOMMANDATION, aiResultMethodology)));

      LOGGER.trace("Found recommandationModel: {}", recommandationModel);

      result.add(new RecommandationItemModel(recommandationModel.getLevel().toString(), recommandationModel.getTitle(),
        recommandationModel.getDescription()));
    });

    result.sort(Comparator.comparing(
      recommandationItemModel -> RecommandationLevel.valueOf(recommandationItemModel.getColor())));

    return ResponseEntity.ok().body(result);
  }


}
