package com.elit.outildedecision.ai.kiviat.service.implementation;

import com.elit.outildedecision.ai.answer.exception.AiAnswerNotFoundException;
import com.elit.outildedecision.ai.answer.repository.AiAnswerRepository;
import com.elit.outildedecision.ai.kiviat.calculate.KiviatScoreCalculator;
import com.elit.outildedecision.ai.kiviat.mapper.KiviatMapper;
import com.elit.outildedecision.ai.result.exception.ResultAiNotFoundException;
import com.elit.outildedecision.ai.result.repository.ResultIaRepository;
import com.elit.outildedecision.shared.util.JsonResourceLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KiviatServiceImplTest {

  // Chemin du JSON de test placé dans le classpath du projet de test
  private static final String JSON = "com/elit/outildedecision/ai/kiviat/kiviat-project-matrix-test.json";

  private KiviatServiceImpl kiviatServiceImpl;

  @Mock
  private ResultIaRepository resultIaRepository;

  @Mock
  private AiAnswerRepository aiAnswerRepository;

  @Mock
  private KiviatMapper kiviatMapper;

  @BeforeEach
  void setUp(TestInfo testInfo) {
    kiviatServiceImpl = new KiviatServiceImpl(aiAnswerRepository, resultIaRepository,
        new JsonResourceLoader(new DefaultResourceLoader()), kiviatMapper, new KiviatScoreCalculator());
    if (testInfo.getTags().contains("loadJson")) {
      injectJsonPath(JSON);
      kiviatServiceImpl.init();
    }
  }

  private void injectJsonPath(String path) {
    ReflectionTestUtils.setField(kiviatServiceImpl, "kiviatProjectFile", path);
  }

  @Test
  @Tag("loadJson")
  void chargeLeJsonEtRenseigneLesQuestions() {
    @SuppressWarnings("unchecked")
    List<?> questions = (List<?>) ReflectionTestUtils.getField(kiviatServiceImpl, "questions");

    assertThat(questions)
        .as("La liste 'questions' devrait être remplie après le chargement du JSON")
        .isNotNull()
        .isNotEmpty();
  }

  @Test
  void initAvecCheminInvalideCreeListeVide() {
    injectJsonPath("classpath:chemin/invalide/kiviat-project-matrix-test.json");
    kiviatServiceImpl.init();

    @SuppressWarnings("unchecked")
    List<?> questions = (List<?>) ReflectionTestUtils.getField(kiviatServiceImpl, "questions");

    assertThat(questions)
        .as("La liste 'questions' doit être vide si le JSON est introuvable")
        .isNotNull()
        .isEmpty();
  }

  @Test
  void initDeuxFoisAvecMemeFichierCreeNouvelleListeNonVide() {
    injectJsonPath(JSON);
    kiviatServiceImpl.init();

    @SuppressWarnings("unchecked")
    List<?> firstLoad = (List<?>) ReflectionTestUtils.getField(kiviatServiceImpl, "questions");
    assertThat(firstLoad)
        .as("Le premier chargement doit produire une liste non vide")
        .isNotNull()
        .isNotEmpty();

    // Rechargement
    kiviatServiceImpl.init();

    @SuppressWarnings("unchecked")
    List<?> secondLoad = (List<?>) ReflectionTestUtils.getField(kiviatServiceImpl, "questions");
    assertThat(secondLoad)
        .as("Le second chargement doit également produire une liste non vide")
        .isNotNull()
        .isNotEmpty();

    // S'assure qu'une nouvelle instance de liste est recréée à chaque init()
    assertThat(secondLoad)
        .as("Chaque init() doit réaffecter une nouvelle instance de liste")
        .isNotSameAs(firstLoad);
  }

  @Test
  void initPuisCheminInvalideRemplaceParListeVide() {
    // 1) Chargement valide
    injectJsonPath(JSON);
    kiviatServiceImpl.init();
    @SuppressWarnings("unchecked")
    List<?> loaded = (List<?>) ReflectionTestUtils.getField(kiviatServiceImpl, "questions");
    assertThat(loaded)
        .as("Le chargement valide doit remplir la liste")
        .isNotNull()
        .isNotEmpty();

    // 2) Puis fichier invalide
    injectJsonPath("classpath:does/not/exist.json");
    kiviatServiceImpl.init();
    @SuppressWarnings("unchecked")
    List<?> afterInvalid = (List<?>) ReflectionTestUtils.getField(kiviatServiceImpl, "questions");
    assertThat(afterInvalid)
        .as("Après un chemin invalide, la liste doit être vide")
        .isNotNull()
        .isEmpty();
  }

  @Test
  void chargeLeJsonAvecPrefixeClasspath() {
    injectJsonPath("classpath:" + JSON);
    kiviatServiceImpl.init();

    @SuppressWarnings("unchecked")
    List<?> questions = (List<?>) ReflectionTestUtils.getField(kiviatServiceImpl, "questions");

    assertThat(questions)
        .as("Le chargement avec 'classpath:' doit aussi fonctionner")
        .isNotNull()
        .isNotEmpty();
  }

  @Test
  void cheminNullCreeListeVide() {
    injectJsonPath(null);
    kiviatServiceImpl.init();

    @SuppressWarnings("unchecked")
    List<?> questions = (List<?>) ReflectionTestUtils.getField(kiviatServiceImpl, "questions");

    assertThat(questions)
        .as("Avec un chemin null, la liste doit être vide")
        .isNotNull()
        .isEmpty();
  }

  // ------------------- getKiviatMethodologyValues -------------------

  @Test
  void getKiviatMethodologyValues_aucunResultat_declencheResultAiNotFoundException() {
    final long projectId = 2L;

    when(resultIaRepository.findByProject_ProjectId(projectId))
        .thenReturn(Optional.empty());

    assertThatExceptionOfType(ResultAiNotFoundException.class)
        .isThrownBy(() -> kiviatServiceImpl.getKiviatMethodologyValues(projectId));
  }

  // ------------------- getKiviatProjetValues -------------------

  @Test
  void getKiviatProjetValues_aucuneReponse_declencheAiAnswerNotFoundException() {
    final long projectId = 11L;

    when(aiAnswerRepository.findByProject_ProjectId(projectId))
        .thenReturn(Collections.emptyList());

    assertThatExceptionOfType(AiAnswerNotFoundException.class)
        .isThrownBy(() -> kiviatServiceImpl.getKiviatProjetValues(projectId));
  }
}
