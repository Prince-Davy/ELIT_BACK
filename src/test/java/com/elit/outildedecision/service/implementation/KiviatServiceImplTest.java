package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.ai.answer.exception.AiAnswerNotFoundException;
import com.elit.outildedecision.ai.answer.repository.AiAnswerRepository;
import com.elit.outildedecision.exception.ResultAiNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.test.util.ReflectionTestUtils;


import com.elit.outildedecision.repository.ResultIaRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KiviatServiceImplTest {

  // Chemin du JSON de test placé dans le classpath du projet de test
  private static final String JSON = "com/elit/outildedecision/service/implementation/KiviatProjectMatrixTest.json";

  private KiviatServiceImpl kiviatServiceImpl;

  @Mock
  private ResultIaRepository resultIaRepository;

  @Mock
  private AiAnswerRepository aiAnswerRepository;

  @BeforeEach
  void setUp(TestInfo testInfo) {
    kiviatServiceImpl = new KiviatServiceImpl(aiAnswerRepository, resultIaRepository, new DefaultResourceLoader());
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

    assertNotNull(questions, "La liste 'questions' ne doit pas être nulle après init()");
    assertFalse(questions.isEmpty(), "La liste 'questions' devrait être remplie après le chargement du JSON");
  }

  @Test
  void initAvecCheminInvalideCreeListeVide() {
    injectJsonPath("classpath:chemin/invalide/KiviatProjectMatrixTest.json");
    kiviatServiceImpl.init();

    @SuppressWarnings("unchecked")
    List<?> questions = (List<?>) ReflectionTestUtils.getField(kiviatServiceImpl, "questions");

    assertNotNull(questions, "La liste 'questions' ne doit pas être nulle même en cas d'erreur de chargement");
    assertTrue(questions.isEmpty(), "La liste 'questions' doit être vide si le JSON est introuvable");
  }

  @Test
  void initDeuxFoisAvecMemeFichierCreeNouvelleListeNonVide() {
    injectJsonPath(JSON);
    kiviatServiceImpl.init();

    @SuppressWarnings("unchecked")
    List<?> firstLoad = (List<?>) ReflectionTestUtils.getField(kiviatServiceImpl, "questions");
    assertNotNull(firstLoad);
    assertFalse(firstLoad.isEmpty(), "Le premier chargement doit produire une liste non vide");

    // Rechargement
    kiviatServiceImpl.init();

    @SuppressWarnings("unchecked")
    List<?> secondLoad = (List<?>) ReflectionTestUtils.getField(kiviatServiceImpl, "questions");
    assertNotNull(secondLoad);
    assertFalse(secondLoad.isEmpty(), "Le second chargement doit également produire une liste non vide");

    // S'assure qu'une nouvelle instance de liste est recréée à chaque init()
    assertNotSame(firstLoad, secondLoad, "Chaque init() doit réaffecter une nouvelle instance de liste");
  }

  @Test
  void initPuisCheminInvalideRemplaceParListeVide() {
    // 1) Chargement valide
    injectJsonPath(JSON);
    kiviatServiceImpl.init();
    @SuppressWarnings("unchecked")
    List<?> loaded = (List<?>) ReflectionTestUtils.getField(kiviatServiceImpl, "questions");
    assertNotNull(loaded);
    assertFalse(loaded.isEmpty(), "Le chargement valide doit remplir la liste");

    // 2) Puis fichier invalide
    injectJsonPath("classpath:does/not/exist.json");
    kiviatServiceImpl.init();
    @SuppressWarnings("unchecked")
    List<?> afterInvalid = (List<?>) ReflectionTestUtils.getField(kiviatServiceImpl, "questions");
    assertNotNull(afterInvalid);
    assertTrue(afterInvalid.isEmpty(), "Après un chemin invalide, la liste doit être vide");
  }

  @Test
  void chargeLeJsonAvecPrefixeClasspath() {
    injectJsonPath("classpath:" + JSON);
    kiviatServiceImpl.init();

    @SuppressWarnings("unchecked")
    List<?> questions = (List<?>) ReflectionTestUtils.getField(kiviatServiceImpl, "questions");

    assertNotNull(questions);
    assertFalse(questions.isEmpty(), "Le chargement avec 'classpath:' doit aussi fonctionner");
  }

  @Test
  void cheminNullCreeListeVide() {
    injectJsonPath(null);
    kiviatServiceImpl.init();

    @SuppressWarnings("unchecked")
    List<?> questions = (List<?>) ReflectionTestUtils.getField(kiviatServiceImpl, "questions");

    assertNotNull(questions, "Même avec un chemin null, la liste ne doit pas être nulle");
    assertTrue(questions.isEmpty(), "Avec un chemin null, la liste doit être vide");
  }

  // ------------------- getKiviatMethodologyValues -------------------

  @Test
  void getKiviatMethodologyValues_aucunResultat_declencheResultAiNotFoundException() {
    final long projectId = 2L;

    when(resultIaRepository.findByProject_ProjectId(projectId))
      .thenReturn(Optional.empty());

    assertThrows(ResultAiNotFoundException.class,
      () -> kiviatServiceImpl.getKiviatMethodologyValues(projectId));
  }

  // ------------------- getKiviatProjetValues -------------------


  @Test
  void getKiviatProjetValues_aucuneReponse_declencheAiAnswerNotFoundException() {
    final long projectId = 11L;

    when(aiAnswerRepository.findByProject_ProjectId(projectId))
      .thenReturn(Collections.emptyList());

    assertThrows(AiAnswerNotFoundException.class,
      () -> kiviatServiceImpl.getKiviatProjetValues(projectId));
  }

}
