package com.elit.outildedecision.service.implementation;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import com.elit.outildedecision.MemoryTestAppender;
import com.elit.outildedecision.entity.AnswerIa;
import com.elit.outildedecision.entity.ResultAi;
import com.elit.outildedecision.model.RecommandationItemModel;
import com.elit.outildedecision.model.RecommandationKiviatModel;
import com.elit.outildedecision.model.RecommandationLevel;
import com.elit.outildedecision.util.MethodologyType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
class RecommandationServiceImplTest {

  public static final String LOGGER_NAME = "com.elit.outildedecision.service.implementation.RecommandationServiceImpl";
  public static final String CONTINUING_WITHOUT = "Continuing without json recommandations, all subsequent call will result in no data";
  public static final String STARTUP_ERROR = "Startup error, cannot read json recommandation file: ";
  public static final String TRIED_LOOKUP = "Tried to lookup file at : ";
  private final MemoryTestAppender appender = new MemoryTestAppender();

  // Json files
  private static final String VALID_JSON = "com/elit/outildedecision/service/implementation/RecommandationsIATest.json";
  private static final String INVALID_JSON = "com/elit/outildedecision/service/implementation/RecommandationsIATest_invalid.json";
  private static final String INVALID_PATH = "dummy";

  // SUT
  private RecommandationServiceImpl recommandationServiceImpl;

  @BeforeEach
  void setUp(TestInfo testInfo) {
    // Create SUT
    recommandationServiceImpl = new RecommandationServiceImpl(new DefaultResourceLoader());

    // Initialize logging interception
    Logger logger = (Logger) LoggerFactory.getLogger(LOGGER_NAME);
    logger.setLevel(Level.TRACE);
    logger.addAppender(appender);

    appender.setContext((Context) LoggerFactory.getILoggerFactory());
    appender.start();

    // Also load json if needed
    if (testInfo.getTags().contains("loadJson")) {
      initRecommandationService();
    }
  }

  @AfterEach
  void tearDown() {
    appender.reset();
    appender.stop();
  }

  /**
   * Source for getMappingsTest both answer found and not found in json.
   *
   * @return a source for parametrized test
   */
  static Stream<Arguments> getMappingsAllAnswers() {
    List<AnswerIa> answers = List.of(
      new AnswerIa("1", "", "Élevée (ensemble important de systèmes différents qui fonctionnent ensemble)", null),
      new AnswerIa("2", "", "De 12 à 24 mois", null),
      new AnswerIa("3", "", "Faible : La durée doit être strictement respectée", null),
      new AnswerIa("4", "", "Moyenne : Une déviation insignifiante du budget est permise", null),
      new AnswerIa("5", "", "Exigences fonctionnelles et non fonctionnelles", null),
      new AnswerIa("6", "", "Moyenne : Une déviation insignifiante des besoins est permise", null),
      new AnswerIa("7", "", "Moyen : adaptation de technologies familières", null),
      new AnswerIa("8", "", "Moyen : de 10% à 50%", null),
      new AnswerIa("9", "", "Moyen", null),
      new AnswerIa("10", "", "Non", null),
      new AnswerIa("11", "", "Moyen : participtaion du client pendant la création", null),
      new AnswerIa("12", "", "Supérieure à 4 semaines", null),
      new AnswerIa("13", "", "Moyen", null),
      new AnswerIa("14", "", "Moyen", null),
      new AnswerIa("15", "", "2 équipes", null),
      new AnswerIa("16", "", "Élevé : absorbe facilement les nouvelles connaissances, peut s'adapter aux changements", null),
      new AnswerIa("17", "", "Élevé (plus de 5 ans)", null),
      new AnswerIa("18", "", "Faible (moins de 2 ans)", null)
    );

    MethodologyType methodology = MethodologyType.HYBRID;

    List<RecommandationItemModel> recommmandations = List.of(
      new RecommandationItemModel("Red", "Flexibilité sur la durée - Faible", "La complexité de coordination dans un modèle hybride peut rendre le respect strict des délais plus difficile, surtout avec des équipes ajustant fréquemment leur travail."),
      new RecommandationItemModel("Green", "Niveau de connaissance du client sur ses besoins - Moyen", "Lorsque les besoins sont modérément bien définis, ce modèle permet de combiner une base structurée avec une certaine flexibilité pour affiner les spécifications."),
      new RecommandationItemModel("Green", "Niveau d'engagement de la direction - Moyen", "Cette approche requiert un engagement modéré du management, permettant de superviser tout en laissant une flexibilité d’ajustement aux équipes."),
      new RecommandationItemModel("Green","Défi technologique - Moyen", "La souplesse d’une approche hybride peut faciliter l’adaptation aux évolutions technologiques tout en préservant un cadre structuré."),
      new RecommandationItemModel("Green", "Niveau de risque - Moyen", "La méthodologie hybride permet une bonne identification des risques dans la phase de plannification initiale et une certaine réactivité pour s'ajuster en cours de projet si nécessaire."),
      new RecommandationItemModel("Green", "Niveau d'implication du client - Moyen", "Ce modèle facilite la participation client à des étapes clés, intégrant leurs retours sans perturber la structure globale."),
      new RecommandationItemModel("Green", "Flexibilité sur le respect des besoins du client - Moyenne", "Ce modèle convient pour des projets où les exigences du client peuvent légèrement évoluer sans bouleverser le planning."),
      new RecommandationItemModel("Green", "Flexibilité sur le budget - Moyenne", "La méthodologie hybride est recommandée lorsque des ajustements mineurs au budget sont permis. Elle permet de maintenir un certain contrôle tout en s'adaptant aux changements modérés."),
      new RecommandationItemModel("Yellow", "Niveau d'implication des participants - Elevé", "Pour des équipes très impliquées et adaptables, l’approche hybride pourrait limiter leur potentiel en imposant des phases structurées, freinant parfois leur capacité d'initiative."),
      new RecommandationItemModel("Yellow", "Complexité du projet - Élevée", "La coordination des équipes et des pratiques peut être compliquée dans un projet géré en hybride, entrainant des risques de retards.")
    );
    return Stream.of(
      Arguments.of(answers, methodology, recommmandations)
    );
  }

  /**
   * RecommadationKiviatModel for each values of methodology
   *
   * @return a source for parametrized test (Methodology, ModelValues)
   */
  static Stream<Object> methodologyAndKiviatValues() {
    return Stream.of(
      Arguments.of(MethodologyType.HYBRID, new RecommandationKiviatModel(MethodologyType.HYBRID, 3, 2.5, 2.5, 3, 3)),
      Arguments.of(MethodologyType.AGILE, new RecommandationKiviatModel(MethodologyType.AGILE, 5, 4.5, 1.5, 1, 5)),
      Arguments.of(MethodologyType.CLASSIC, new RecommandationKiviatModel(MethodologyType.CLASSIC, 1, 1.5, 4.5, 5, 1))
    );
  }

  /**
   * Test init in default conditions - file present, well formatted.
   */
  @Test
  void testInitOk() {
    injectJsonPath(VALID_JSON);
    assertDoesNotThrow(() -> recommandationServiceImpl.init());
    assertEquals(0, appender.countEventsForLogger(LOGGER_NAME));

    // We still need to mock result ai to avoid npe on methodology and thus need to mock both parameter :-(
    ResultAi mockResultAi = mock(ResultAi.class);
    when(mockResultAi.getPredictedMethod()).thenReturn(MethodologyType.AGILE.toString());
    List<AnswerIa> mocKAnswers = mock(List.class);

    ResponseEntity<List<RecommandationItemModel>> response = recommandationServiceImpl.getMappings(mocKAnswers, mockResultAi);
    assertNotNull(response);
    assertTrue(Objects.requireNonNull(response.getBody()).isEmpty());
  }

  /**
   * Test init in default conditions - file present, well formatted.
   */
  @Test
  void testInitWrongFormat() {
    injectJsonPath(INVALID_JSON);
    assertDoesNotThrow(() -> recommandationServiceImpl.init());
    assertEquals(3, appender.countEventsForLogger(LOGGER_NAME));
    List<ILoggingEvent> errorEvents = appender.search(Level.ERROR);
    assertNotNull(errorEvents);
    assertEquals(2, errorEvents.size());
    assertTrue(errorEvents.get(0).getMessage().contains(STARTUP_ERROR));
    assertTrue(errorEvents.get(1).getMessage().contains(CONTINUING_WITHOUT));
    List<ILoggingEvent> infoEvents = appender.search(Level.INFO);
    assertNotNull(infoEvents);
    assertEquals(1, infoEvents.size());
    assertTrue(infoEvents.get(0).getMessage().contains(TRIED_LOOKUP));

    // We still need to mock result ai to avoid npe on methodology and thus need to mock both parameter :-(
    ResultAi mockResultAi = mock(ResultAi.class);
    when(mockResultAi.getPredictedMethod()).thenReturn("Hybride");
    List<AnswerIa> mocKAnswers = mock(List.class);

    ResponseEntity<List<RecommandationItemModel>> response = recommandationServiceImpl.getMappings(mocKAnswers, mockResultAi);
    assertNotNull(response);
    assertTrue(Objects.requireNonNull(response.getBody()).isEmpty());
  }

  /**
   * Test init in default conditions - file present, well formatted.
   */
  @Test
  void testInitWrongPath() {
    injectJsonPath(INVALID_PATH);
    assertDoesNotThrow(() -> recommandationServiceImpl.init());
    assertEquals(3, appender.countEventsForLogger(LOGGER_NAME));
    List<ILoggingEvent> errorEvents = appender.search(Level.ERROR);
    assertNotNull(errorEvents);
    assertEquals(2, errorEvents.size());
    assertTrue(errorEvents.get(0).getMessage().contains(STARTUP_ERROR));
    assertTrue(errorEvents.get(1).getMessage().contains(CONTINUING_WITHOUT));
    List<ILoggingEvent> infoEvents = appender.search(Level.INFO);
    assertNotNull(infoEvents);
    assertEquals(1, infoEvents.size());
    assertTrue(infoEvents.get(0).getMessage().contains(TRIED_LOOKUP));

    // We still need to mock result ai to avoid npe on methodology and thus need to mock both parameter :-(
    ResultAi mockResultAi = mock(ResultAi.class);
    when(mockResultAi.getPredictedMethod()).thenReturn("Hybride");
    List<AnswerIa> mocKAnswers = mock(List.class);

    ResponseEntity<List<RecommandationItemModel>> response = recommandationServiceImpl.getMappings(mocKAnswers, mockResultAi);
    assertNotNull(response);
    assertTrue(Objects.requireNonNull(response.getBody()).isEmpty());
  }

  /**
   * Inits RecommandationServiceImpl with correct json.
   */
  private void initRecommandationService() {
    injectJsonPath(RecommandationServiceImplTest.VALID_JSON);
    recommandationServiceImpl.init();
  }

  /**
   * Inject the json path into recommandation service.
   *
   * @param jsonPath the path to json recommandation file
   */
  private void injectJsonPath(String jsonPath) {
    ReflectionTestUtils.setField(recommandationServiceImpl, "recommandationsFile", jsonPath);
  }

  @Tag("loadJson")
  @ParameterizedTest
  @MethodSource({
    "getMappingsAllAnswers"
  })
  void getMappingsTest(List<AnswerIa> answers, MethodologyType methodology,
                       List<RecommandationItemModel> expectedRecommandations) {

    ResultAi resultAi = new ResultAi();
    resultAi.setPredictedMethod(methodology.toString());

    ResponseEntity<List<RecommandationItemModel>> response =
      recommandationServiceImpl.getMappings(answers, resultAi);
    assertNotNull(response);

    List<RecommandationItemModel> actualRecommandations = response.getBody();
    assertNotNull(actualRecommandations);
    assertEquals(expectedRecommandations.size(), actualRecommandations.size());
    // Use of contains all allows for more flexibility about the order returned than using List#equals
    // Since this has quadratic complexity, if the question number starts to grow, consider refactoring
    // the test method (do NOT return a Set<> we need to order green > yellow > red, or do sorting in front?).
    assertTrue(expectedRecommandations.containsAll(actualRecommandations));
    assertTrue(actualRecommandations.containsAll(expectedRecommandations));

    // Check for green elements to be first, then yellow, then red, as per requirements
    // Use for loop + fail(), faster than stream and maybe iterator? and avoid unnecessary dependency
    RecommandationItemModel previousRecommandation = null;
    for (RecommandationItemModel actualRecommandation : actualRecommandations) {
      if (previousRecommandation != null) {
        String previousRecommandationColor = previousRecommandation.getColor();
        String actualRecommandationColor = actualRecommandation.getColor();
        if (RecommandationLevel.valueOf(previousRecommandationColor).compareTo(RecommandationLevel.valueOf(actualRecommandationColor)) > 0) {
          String message = MessageFormat.format(
            "Recommandation are not correctly sorted: found {0} before {1}",
            previousRecommandation,
            actualRecommandation
          );
          fail(message);
        }
      }
      previousRecommandation = actualRecommandation;
    }

  }

  @ParameterizedTest
  @MethodSource("methodologyAndKiviatValues")
  void getKiviatProjectValuesTest(MethodologyType methodology, RecommandationKiviatModel expectedModel) {
    // Mocking the ResultAi object
    ResultAi resultAi = mock(ResultAi.class);
    when(resultAi.getPredictedMethod()).thenReturn(methodology.name());

    // Instantiating the class to be tested
    ResponseEntity<RecommandationKiviatModel> response = recommandationServiceImpl.getKiviatProjectValues(resultAi);

    // Assertions
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());

    RecommandationKiviatModel actualModel = response.getBody();
    assertNotNull(actualModel);

    assertEquals(expectedModel, actualModel);
  }
}
