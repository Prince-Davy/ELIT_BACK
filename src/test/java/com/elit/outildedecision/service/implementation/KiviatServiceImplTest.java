package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.AnswerIa;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.ResultAi;
import com.elit.outildedecision.exception.AnswerIaNotFoundException;
import com.elit.outildedecision.exception.ResultAiNotFoundException;
import com.elit.outildedecision.model.RecommandationKiviatModel;
import com.elit.outildedecision.repository.AnswerIaRepository;
import com.elit.outildedecision.repository.ResultIaRepository;
import com.elit.outildedecision.util.MethodologyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KiviatServiceImplTest {


  private static final String JSON = "com/elit/outildedecision/service/implementation/KiviatProjectMatrixTest.json";


  // SUT
  @InjectMocks
  private KiviatServiceImpl kiviatServiceImpl;
  @Mock
  private ResultIaRepository resultIaRepository;
  @Mock
  private AnswerIaRepository answerIaRepository;


  @BeforeEach
  void setUp(TestInfo testInfo) {
    // Create SUT
    MockitoAnnotations.openMocks(this);
    kiviatServiceImpl = new KiviatServiceImpl(answerIaRepository, resultIaRepository, new DefaultResourceLoader());


      // Also load json if needed
      if (testInfo.getTags().contains("loadJson")) {
        initKiviatService();
      }

  }

  private void initKiviatService() {
    injectJsonPath();
    kiviatServiceImpl.init();
  }

  private void injectJsonPath() {
    ReflectionTestUtils.setField(kiviatServiceImpl, "kiviatProjectFile", KiviatServiceImplTest.JSON);
  }

  /**
   * RecommadationKiviatModel for each values of methodology.
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
   * RecommadationKiviatModel for lowest values answers
   *
   * @return a source for parametrized test (List<AnswersIa>, ModelValues)
   */
  static Stream<Object> answersIaValuesLow() {
    Project project = new Project();
    project.setProjectId(1L);
    List<AnswerIa> answersIas = List.of(
      new AnswerIa("3", "category", "Élevée : Un écart considérable par rapport à la durée initiale est autorisé", project),
      new AnswerIa("4", "category", "Faible : Le budget doit être strictement respecté", project),
      new AnswerIa("5", "category", "Exigences fonctionnelles et non fonctionnelles", project),
      new AnswerIa("6", "category", "Faible : Les besoins doivent êtres strictement respectés", project),
      new AnswerIa("8", "category", "Faible : moins de 10%", project),
      new AnswerIa("9", "category", "Faible", project),
      new AnswerIa("11", "category", "Faible : la présence du client pendant les moments clés du projet", project),
      new AnswerIa("12", "category", "Supérieur à 4 semaines", project),
      new AnswerIa("13", "category", "Élevé", project),
      new AnswerIa("14", "category", "Faible", project),
      new AnswerIa("16", "category", "Faible : il est difficile pour l'équipe d'apprendre de nouvelles connaissances et technologies, de s'adapter aux changements", project)
    );
    RecommandationKiviatModel expectedModel = new RecommandationKiviatModel(null, 2.33, 1.5, 4.5, 5.0, 2.33);
    return Stream.of(Arguments.of(answersIas, expectedModel));
  }

  static Stream<Object> answersIaValuesAdditional() {
    Project project = new Project();
    project.setProjectId(2L);

    // Cas avec des valeurs différentes pour vérifier d'autres comportements
    List<AnswerIa> answersIas = List.of(
      new AnswerIa("3", "category", "Élevée : Un écart considérable par rapport à la durée initiale est autorisé", project),
      new AnswerIa("4", "category", "Faible : Le budget doit être strictement respecté", project),
      new AnswerIa("5", "category", "Exigences fonctionnelles et non fonctionnelles", project),
      new AnswerIa("6", "category", "Faible : Les besoins doivent êtres strictement respectés", project),
      new AnswerIa("8", "category", "Faible : moins de 10%", project),
      new AnswerIa("9", "category", "Faible", project),
      new AnswerIa("11", "category", "Faible : la présence du client pendant les moments clés du projet", project),
      new AnswerIa("12", "category", "Supérieur à 4 semaines", project),
      new AnswerIa("13", "category", "Élevé", project),
      new AnswerIa("14", "category", "Faible", project),
      new AnswerIa("16", "category", "Faible : il est difficile pour l'équipe d'apprendre de nouvelles connaissances et technologies, de s'adapter aux changements", project)
    );
    RecommandationKiviatModel expectedModel = new RecommandationKiviatModel(null, 2.33, 1.5, 4.5, 5.0, 2.33);
    return Stream.of(
      Arguments.of(answersIas, expectedModel)
    );
  }


  @ParameterizedTest
  @MethodSource({"methodologyAndKiviatValues"})
  void getKiviatMethodologyValuesTest(MethodologyType methodology, RecommandationKiviatModel expectedModel) throws ResultAiNotFoundException {
    // Mocking the ResultAi object
    ResultAi resultAi = mock(ResultAi.class);
    Project project = mock(Project.class);
    when(resultAi.getPredictedMethod()).thenReturn(methodology.name());
    when(resultAi.getProject()).thenReturn(project);
    when(project.getProjectId()).thenReturn(1L);
    when(resultAi.getPredictedMethod()).thenReturn(methodology.name());

    when(resultIaRepository.findByProject_ProjectId(1L)).thenReturn(Optional.of(resultAi));

    // Instantiating the class to be tested
    ResponseEntity<RecommandationKiviatModel> response = kiviatServiceImpl.getKiviatMethodologyValues(1L);

    // Assertions
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());

    RecommandationKiviatModel actualModel = response.getBody();
    assertNotNull(actualModel);

    assertEquals(expectedModel, actualModel);
  }

  @Tag("loadJson")
  @ParameterizedTest
  @MethodSource({"answersIaValuesLow", "answersIaValuesAdditional"})
  public void testGetKiviatProjetValues(List<AnswerIa> answersIas, RecommandationKiviatModel expectedModel) throws AnswerIaNotFoundException {
    Long projectId = 1L;

    when(answerIaRepository.findByProject_ProjectId(projectId)).thenReturn(answersIas);

    ResponseEntity<RecommandationKiviatModel> response = kiviatServiceImpl.getKiviatProjetValues(projectId);

    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    RecommandationKiviatModel result = response.getBody();
    assertNotNull(result);
    // Ajoutez des assertions pour vérifier les valeurs dans result
    assertEquals(expectedModel, result);

  }


  @Test
  public void testGetKiviatProjetValues_NoAnswers() {
    Long projectId = 1L;

    when(answerIaRepository.findByProject_ProjectId(projectId)).thenReturn(Collections.emptyList());

    assertThrows(AnswerIaNotFoundException.class, () -> {kiviatServiceImpl.getKiviatProjetValues(projectId);});
  }
}
