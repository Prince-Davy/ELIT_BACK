package com.elit.outildedecision.collaboration.predictibility.service;

import com.elit.outildedecision.collaboration.multicriteria.entity.FormStep;
import com.elit.outildedecision.collaboration.multicriteria.entity.MultiCriteriaAnalysis;
import com.elit.outildedecision.collaboration.multicriteria.repository.MultiCriteriaAnalysisRepository;
import com.elit.outildedecision.collaboration.predictibility.entity.PredictibilityAnalysis;
import com.elit.outildedecision.collaboration.predictibility.exception.PredictibilityAnalysisAlreadyUpdated;
import com.elit.outildedecision.collaboration.predictibility.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.collaboration.predictibility.calculate.MethodologyScoreCalculator;
import com.elit.outildedecision.collaboration.predictibility.calculate.MethodologyScoreResult;
import com.elit.outildedecision.collaboration.predictibility.dto.PredictibilityScoreResponse;
import com.elit.outildedecision.collaboration.predictibility.mapper.PredictibilityMapper;
import com.elit.outildedecision.collaboration.predictibility.repository.PredictibilityAnalysisRepository;
import com.elit.outildedecision.collaboration.predictibility.repository.PredictibilityResultsRepository;
import com.elit.outildedecision.collaboration.predictibility.service.implementation.PredictibilityAnalysisServiceImpl;
import com.elit.outildedecision.collaboration.vote.repository.VoteRepository;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.collaboration.predictibility.dto.ChosenMethodologyRequest;
import com.elit.outildedecision.shared.exception.ConflictException;
import com.elit.outildedecision.shared.exception.NotFoundException;
import com.elit.outildedecision.shared.methodology.repository.MethodologyRepository;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.project.repository.ProjectRepository;
import com.elit.outildedecision.shared.util.MethodologyType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PredictibilityAnalysisServiceImplTest {
  @Mock
  ProjectRepository projectRepository;
  @Mock
  MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository;
  @Mock
  PredictibilityAnalysisRepository predictibilityAnalysisRepository;
  @Mock
  PredictibilityMapper predictibilityMapper;
  @Mock
  MethodologyScoreCalculator methodologyScoreCalculator;
  @Mock
  VoteRepository voteRepository;
  @Mock
  MethodologyRepository methodologyRepository;
  @Mock
  PredictibilityResultsRepository predictibilityResultsRepository;
  @InjectMocks
  PredictibilityAnalysisServiceImpl predictibilityAnalysisService;

  Project project;
  MultiCriteriaAnalysis multiCriteriaAnalysis;
  PredictibilityAnalysis predictibilityAnalysis;
  FormStep formStep;
  List<FormStep> formSteps = new ArrayList<>();
  User user;
  Long longId = 1L;
  ChosenMethodologyRequest chosenMethodologyRequest;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setUserId(1L);
    user.setUserEmail("test@test.com");
    formStep = new FormStep();
    formStep.setStepId(longId);
    formStep.setEScore(1F);
    project = new Project();
    project.setProjectId(longId);
    multiCriteriaAnalysis = new MultiCriteriaAnalysis();
    multiCriteriaAnalysis.setMultiCriteriaAnalysisId(longId);
    multiCriteriaAnalysis.setProject(project);
    predictibilityAnalysis = new PredictibilityAnalysis();
    predictibilityAnalysis.setPredictibilityAnalysisId(longId);
    predictibilityAnalysis.setMultiCriteriaAnalysis(multiCriteriaAnalysis);
    predictibilityAnalysis.setChosenMethodology("Agile");
    formStep.setMultiCriteriaAnalysis(multiCriteriaAnalysis);
    formSteps.add(formStep);
    multiCriteriaAnalysis.setFormSteps(formSteps);

    chosenMethodologyRequest = ChosenMethodologyRequest.builder().chosenMethodology("Agile").build();
  }

  @Test
  void createPredictibilityForm_RuntimeException() {
    assertThrows(ProjectNotFoundException.class,
        () -> predictibilityAnalysisService.createPredictibilityForm(longId));
  }

  @Test
  void createPredictibilityForm_throwsNotFoundWhenNoMultiCriteriaAnalysis() {
    when(projectRepository.findById(longId)).thenReturn(Optional.of(project));
    when(multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(longId))
        .thenReturn(null);
    assertThrows(NotFoundException.class,
        () -> predictibilityAnalysisService.createPredictibilityForm(longId));
  }

  @Test
  void createPredictibilityForm_throwsConflictWhenMultiCriteriaNotFinished() {
    multiCriteriaAnalysis.setIsFinished(false);
    when(projectRepository.findById(longId)).thenReturn(Optional.of(project));
    when(multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(longId))
        .thenReturn(multiCriteriaAnalysis);
    assertThrows(ConflictException.class,
        () -> predictibilityAnalysisService.createPredictibilityForm(longId));
  }

  @Test
  void createPredictibilityForm_ReturnsExisting() throws ProjectNotFoundException {
    multiCriteriaAnalysis.setIsFinished(true);
    when(projectRepository.findById(longId)).thenReturn(Optional.of(project));
    when(multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(longId))
        .thenReturn(multiCriteriaAnalysis);
    when(predictibilityAnalysisRepository.findByLastIterationAndMultiCriteriaId(longId))
        .thenReturn(predictibilityAnalysis);
    assertEquals(longId, predictibilityAnalysisService.createPredictibilityForm(longId));
  }

  @Test
  void createPredictibilityForm_CreatesNew() throws ProjectNotFoundException {
    multiCriteriaAnalysis.setIsFinished(true);
    when(projectRepository.findById(longId)).thenReturn(Optional.of(project));
    when(multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(longId))
        .thenReturn(multiCriteriaAnalysis);
    when(predictibilityAnalysisRepository.findByLastIterationAndMultiCriteriaId(longId))
        .thenReturn(null);
    when(predictibilityAnalysisRepository.save(PredictibilityAnalysis.builder()
        .multiCriteriaAnalysis(multiCriteriaAnalysis)
        .isFinished(false)
        .predictibilityAnalysisIteration(1)
        .build())).thenReturn(PredictibilityAnalysis.builder()
        .predictibilityAnalysisId(2L)
        .multiCriteriaAnalysis(multiCriteriaAnalysis)
        .isFinished(false)
        .predictibilityAnalysisIteration(1)
        .build());
    assertEquals(2L, predictibilityAnalysisService.createPredictibilityForm(longId));
  }

  @Test
  void addFinalChosenMethodology() throws ProjectNotFoundException, PredictibilityAnalysisNotFoundException {
    when(projectRepository.findById(longId)).thenReturn(Optional.of(project));
    when(predictibilityAnalysisRepository.findById(longId))
        .thenReturn(Optional.of(predictibilityAnalysis));
    when(predictibilityAnalysisRepository.save(predictibilityAnalysis))
        .thenReturn(predictibilityAnalysis);
    assertEquals("La méthodologie finale choisie est : Agile",
        predictibilityAnalysisService.addFinalChosenMethodology(chosenMethodologyRequest, longId, longId));
  }

  @Test
  void addFinalChosenMethodology_whenProjectDosntExist() {
    when(projectRepository.findById(longId)).thenReturn(Optional.empty());
    ProjectNotFoundException exception = Assertions.assertThrows(ProjectNotFoundException.class,
        () -> predictibilityAnalysisService.addFinalChosenMethodology(chosenMethodologyRequest, longId, longId));
    Assertions.assertEquals("Ce projet n'existe pas", exception.getMessage());
  }

  @Test
  void addFinalChosenMethodology_whenPredictibilityAnalysisDosntExist() {
    when(projectRepository.findById(longId)).thenReturn(Optional.of(project));
    when(predictibilityAnalysisRepository.findById(longId)).thenReturn(Optional.empty());
    PredictibilityAnalysisNotFoundException exception = Assertions.assertThrows(
        PredictibilityAnalysisNotFoundException.class,
        () -> predictibilityAnalysisService.addFinalChosenMethodology(chosenMethodologyRequest, longId, longId));
    Assertions.assertEquals("Cette analyse n'existe pas", exception.getMessage());
  }

  @Test
  void putIsFinishedPredictibilityAnalysis_NotFound() {
    when(predictibilityAnalysisRepository.findById(longId)).thenReturn(Optional.empty());
    assertThrows(PredictibilityAnalysisNotFoundException.class,
        () -> predictibilityAnalysisService.putIsFinishedPredictibilityAnalysis(longId, longId));
  }

  @Test
  void putIsFinishedPredictibilityAnalysis_ProjectMismatch() {
    when(predictibilityAnalysisRepository.findById(longId)).thenReturn(Optional.of(predictibilityAnalysis));
    assertThrows(ProjectNotFoundException.class,
        () -> predictibilityAnalysisService.putIsFinishedPredictibilityAnalysis(2L, longId));
  }

  @Test
  void putIsFinishedPredictibilityAnalysis_AlreadyUpdated() {
    predictibilityAnalysis.setIsFinished(true);
    when(predictibilityAnalysisRepository.findById(longId)).thenReturn(Optional.of(predictibilityAnalysis));
    assertThrows(PredictibilityAnalysisAlreadyUpdated.class,
        () -> predictibilityAnalysisService.putIsFinishedPredictibilityAnalysis(longId, longId));
  }

  @Test
  void putIsFinishedPredictibilityAnalysis_Success() throws Exception {
    predictibilityAnalysis.setIsFinished(false);
    when(predictibilityAnalysisRepository.findById(longId)).thenReturn(Optional.of(predictibilityAnalysis));
    assertEquals("L'analyse de prévisibilité a bien été enregistré comme terminée.",
        predictibilityAnalysisService.putIsFinishedPredictibilityAnalysis(longId, longId));
  }

  @Test
  void increasePredictAnalysisIterationNum_Success() throws Exception {
    predictibilityAnalysis.setPredictibilityAnalysisIteration(1);
    when(predictibilityAnalysisRepository.findById(longId)).thenReturn(Optional.of(predictibilityAnalysis));
    assertEquals("L'analyse de prévisibilité a rentre dans une nouvelle iteration.",
        predictibilityAnalysisService.increasePredictAnalysisIterationNum(longId, longId));
    assertEquals(2, predictibilityAnalysis.getPredictibilityAnalysisIteration());
  }

  @Test
  void increasePredictAnalysisIterationNum_ProjectMismatch() {
    when(predictibilityAnalysisRepository.findById(longId)).thenReturn(Optional.of(predictibilityAnalysis));
    assertThrows(ProjectNotFoundException.class,
        () -> predictibilityAnalysisService.increasePredictAnalysisIterationNum(2L, longId));
  }

  @Test
  void calculateScore_flagsTieBreakRequiredWhenTieAndNoChosenMethodology() throws Exception {
    predictibilityAnalysis.setChosenMethodology(null);
    stubScoreComputation(true);

    PredictibilityScoreResponse response =
        predictibilityAnalysisService.calculatePredictibilityAnalysisScore(longId, longId);

    assertThat(response.getTieBreakRequired()).isTrue();
  }

  @Test
  void calculateScore_doesNotFlagTieBreakWhenTieAlreadyResolved() throws Exception {
    predictibilityAnalysis.setChosenMethodology("Agile");
    stubScoreComputation(true);

    PredictibilityScoreResponse response =
        predictibilityAnalysisService.calculatePredictibilityAnalysisScore(longId, longId);

    assertThat(response.getTieBreakRequired()).isFalse();
  }

  @Test
  void calculateScore_doesNotFlagTieBreakWhenNoTie() throws Exception {
    predictibilityAnalysis.setChosenMethodology(null);
    stubScoreComputation(false);

    PredictibilityScoreResponse response =
        predictibilityAnalysisService.calculatePredictibilityAnalysisScore(longId, longId);

    assertThat(response.getTieBreakRequired()).isFalse();
  }

  @Test
  void calculateScore_treatsNullEScoreAsZero() throws Exception {
    formStep.setEScore(null);
    predictibilityAnalysis.setChosenMethodology(null);
    stubScoreComputation(false);

    PredictibilityScoreResponse response =
        predictibilityAnalysisService.calculatePredictibilityAnalysisScore(longId, longId);

    // a form step without a computed eScore counts as 0 instead of raising a NullPointerException
    assertThat(response.getAverage()).isZero();
    assertThat(response.getFormSteps())
        .singleElement()
        .satisfies(step -> assertThat(step.getEScore()).isZero());
  }

  @Test
  void calculateScore_persistsChiefChoiceAsMethodologyWhenTieResolved() throws Exception {
    // Égalité tranchée par le chef sur une méthodologie différente de l'auto-pick (AGILE) :
    // l'association methodology doit suivre le choix du chef, pas l'auto-pick.
    predictibilityAnalysis.setChosenMethodology("Classique");
    stubScoreComputation(true);

    predictibilityAnalysisService.calculatePredictibilityAnalysisScore(longId, longId);

    verify(methodologyRepository).findByName("Classique");
    verify(methodologyRepository, never()).findByName("Agile");
  }

  @Test
  void calculateScore_persistsAutoPickAsMethodologyWhenNoChoice() throws Exception {
    // Aucun choix manuel : l'association methodology reflète l'auto-pick (AGILE).
    predictibilityAnalysis.setChosenMethodology(null);
    stubScoreComputation(false);

    predictibilityAnalysisService.calculatePredictibilityAnalysisScore(longId, longId);

    verify(methodologyRepository).findByName("Agile");
  }

  private void stubScoreComputation(final boolean tie) {
    when(predictibilityAnalysisRepository.findById(longId)).thenReturn(Optional.of(predictibilityAnalysis));
    when(voteRepository.findAllByPredictibilityAnalysisId(longId)).thenReturn(List.of());
    when(methodologyScoreCalculator.computeScores(anyList()))
        .thenReturn(new MethodologyScoreResult(List.of(), MethodologyType.AGILE, tie));
    when(predictibilityResultsRepository.findAllPredictibilityResultsByPreAnalysisId(longId))
        .thenReturn(List.of());
    when(predictibilityMapper.toScoreResponse(predictibilityAnalysis))
        .thenReturn(new PredictibilityScoreResponse());
  }
}
