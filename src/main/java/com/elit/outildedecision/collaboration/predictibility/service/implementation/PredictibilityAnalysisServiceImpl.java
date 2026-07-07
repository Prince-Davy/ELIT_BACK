package com.elit.outildedecision.collaboration.predictibility.service.implementation;

import com.elit.outildedecision.collaboration.multicriteria.entity.FormStep;
import com.elit.outildedecision.collaboration.multicriteria.entity.MultiCriteriaAnalysis;
import com.elit.outildedecision.collaboration.multicriteria.repository.MultiCriteriaAnalysisRepository;
import com.elit.outildedecision.collaboration.predictibility.calculate.MethodologyScoreCalculator;
import com.elit.outildedecision.collaboration.predictibility.calculate.MethodologyScoreResult;
import com.elit.outildedecision.collaboration.predictibility.dto.ChosenMethodologyRequest;
import com.elit.outildedecision.collaboration.predictibility.dto.PredictibilityScoreResponse;
import com.elit.outildedecision.collaboration.predictibility.entity.PredictibilityAnalysis;
import com.elit.outildedecision.collaboration.predictibility.entity.PredictibilityResults;
import com.elit.outildedecision.collaboration.predictibility.exception.PredictibilityAnalysisAlreadyUpdated;
import com.elit.outildedecision.collaboration.predictibility.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.collaboration.predictibility.mapper.PredictibilityMapper;
import com.elit.outildedecision.collaboration.predictibility.repository.PredictibilityAnalysisRepository;
import com.elit.outildedecision.collaboration.predictibility.repository.PredictibilityResultsRepository;
import com.elit.outildedecision.collaboration.predictibility.service.PredictibilityAnalysisService;
import com.elit.outildedecision.collaboration.vote.entity.Vote;
import com.elit.outildedecision.collaboration.vote.repository.VoteRepository;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.collaboration.predictibility.dto.FormStepModel;
import com.elit.outildedecision.collaboration.predictibility.dto.MethodologyScoreModel;
import com.elit.outildedecision.shared.methodology.repository.MethodologyRepository;
import com.elit.outildedecision.shared.exception.ConflictException;
import com.elit.outildedecision.shared.exception.NotFoundException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.project.repository.ProjectRepository;
import com.elit.outildedecision.shared.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Default implementation of {@link PredictibilityAnalysisService}.
 */
@Service
@Slf4j
public class PredictibilityAnalysisServiceImpl implements PredictibilityAnalysisService {


  /**
   * Message used when a predictibility analysis cannot be found.
   */
  private static final String ANALYSE_NOT_FOUND_MESSAGE =
      "Cette analyse de prévisibilité n'existe pas.";

  /**
   * Repository for projects.
   */
  private final ProjectRepository projectRepository;

  /**
   * Repository for multi-criteria analyses.
   */
  private final MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository;

  /**
   * Repository for predictibility analyses.
   */
  private final PredictibilityAnalysisRepository predictibilityAnalysisRepository;

  /**
   * Repository for votes.
   */
  private final VoteRepository voteRepository;

  /**
   * Repository for methodologies.
   */
  private final MethodologyRepository methodologyRepository;

  /**
   * Repository for predictibility results.
   */
  private final PredictibilityResultsRepository predictibilityResultsRepository;

  /**
   * Mapper between predictibility entities and DTOs.
   */
  private final PredictibilityMapper predictibilityMapper;

  /**
   * Pure calculator of the methodology scores and the selected methodology.
   */
  private final MethodologyScoreCalculator methodologyScoreCalculator;

  /**
   * Constructor injection of the collaborators.
   *
   * @param multiCriteriaAnalysisRepository repository for multi-criteria analyses
   * @param predictibilityAnalysisRepository repository for predictibility analyses
   * @param voteRepository repository for votes
   * @param methodologyRepository repository for methodologies
   * @param predictibilityResultsRepository repository for predictibility results
   * @param projectRepository repository for projects
   * @param predictibilityMapper predictibility mapper
   * @param methodologyScoreCalculator methodology score calculator
   */
  public PredictibilityAnalysisServiceImpl(
      final MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository,
      final PredictibilityAnalysisRepository predictibilityAnalysisRepository,
      final VoteRepository voteRepository,
      final MethodologyRepository methodologyRepository,
      final PredictibilityResultsRepository predictibilityResultsRepository,
      final ProjectRepository projectRepository,
      final PredictibilityMapper predictibilityMapper,
      final MethodologyScoreCalculator methodologyScoreCalculator) {
    this.projectRepository = projectRepository;
    this.multiCriteriaAnalysisRepository = multiCriteriaAnalysisRepository;
    this.predictibilityAnalysisRepository = predictibilityAnalysisRepository;
    this.voteRepository = voteRepository;
    this.methodologyRepository = methodologyRepository;
    this.predictibilityResultsRepository = predictibilityResultsRepository;
    this.predictibilityMapper = predictibilityMapper;
    this.methodologyScoreCalculator = methodologyScoreCalculator;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public Long createPredictibilityForm(final Long projectId) throws ProjectNotFoundException {
    Project project = projectRepository.findById(projectId)
        .orElseThrow(() -> new ProjectNotFoundException("Ce projet n'existe pas"));
    MultiCriteriaAnalysis lastMultiCriteriaAnalysis =
        multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(
            project.getProjectId());
    if (lastMultiCriteriaAnalysis == null) {
      throw new NotFoundException("Aucune analyse multicritère pour ce projet.");
    }

    if (Boolean.FALSE.equals(lastMultiCriteriaAnalysis.getIsFinished())) {
      log.warn("Multi-criteria analysis {} for project {} is not finished; "
              + "predictibility form not created",
          lastMultiCriteriaAnalysis.getMultiCriteriaAnalysisId(), projectId);
      throw new ConflictException(
          "L'analyse multicritère doit être finalisée avant de créer la prévisibilité.");
    }

    PredictibilityAnalysis lastPredictibilityAnalysis =
        predictibilityAnalysisRepository.findByLastIterationAndMultiCriteriaId(
            lastMultiCriteriaAnalysis.getMultiCriteriaAnalysisId());
    if (lastPredictibilityAnalysis != null) {
      return lastPredictibilityAnalysis.getPredictibilityAnalysisId();
    }

    PredictibilityAnalysis predictibilityAnalysis = predictibilityAnalysisRepository.save(
        PredictibilityAnalysis.builder()
            .multiCriteriaAnalysis(lastMultiCriteriaAnalysis)
            .isFinished(false)
            .predictibilityAnalysisIteration(1)
            .build());
    log.info("Created predictibility analysis {} for project {}",
        predictibilityAnalysis.getPredictibilityAnalysisId(), projectId);
    return predictibilityAnalysis.getPredictibilityAnalysisId();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public String addFinalChosenMethodology(final ChosenMethodologyRequest request, final Long projectId,
      final Long analyseId) throws ProjectNotFoundException, PredictibilityAnalysisNotFoundException {
    projectRepository.findById(projectId)
        .orElseThrow(() -> new ProjectNotFoundException("Ce projet n'existe pas"));
    PredictibilityAnalysis predictibilityAnalysis = predictibilityAnalysisRepository.findById(analyseId)
        .orElseThrow(() -> new PredictibilityAnalysisNotFoundException("Cette analyse n'existe pas"));

    if (!Objects.equals(
        predictibilityAnalysis.getMultiCriteriaAnalysis().getProject().getProjectId(), projectId)) {
      throw new ProjectNotFoundException(Constants.PROJECT_DOESNT_EXIST);
    }

    String chosenMethodologyToSave = request.getChosenMethodology();
    predictibilityAnalysis.setChosenMethodology(chosenMethodologyToSave);
    predictibilityAnalysisRepository.save(predictibilityAnalysis);
    log.info("Saved chosen methodology '{}' for predictibility analysis {} (project {})",
        chosenMethodologyToSave, analyseId, projectId);
    return "La méthodologie finale choisie est : " + chosenMethodologyToSave;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public PredictibilityScoreResponse calculatePredictibilityAnalysisScore(final Long projectId,
      final Long analyseId) throws PredictibilityAnalysisNotFoundException, ProjectNotFoundException {
    PredictibilityAnalysis predictibilityAnalysis = predictibilityAnalysisRepository.findById(analyseId)
        .orElseThrow(() -> new PredictibilityAnalysisNotFoundException("Cette analyse n'existe pas"));

    if (!Objects.equals(
        predictibilityAnalysis.getMultiCriteriaAnalysis().getProject().getProjectId(), projectId)) {
      throw new ProjectNotFoundException(Constants.PROJECT_DOESNT_EXIST);
    }

    List<FormStepModel> formStepModels = new ArrayList<>();
    float average = 0f;
    List<FormStep> formSteps = predictibilityAnalysis.getMultiCriteriaAnalysis().getFormSteps();
    for (FormStep formStep : formSteps) {
      float eScore = formStep.getEScore() == null ? 0f : formStep.getEScore();
      average += eScore;
      formStepModels.add(new FormStepModel(
          formStep.getStepId(),
          formStep.getStepName(),
          formStep.getStepRef(),
          eScore,
          formStep.getMultiCriteriaAnalysis().getMultiCriteriaAnalysisId()));
    }
    average = average / formSteps.size();

    List<Vote> votesList = voteRepository.findAllByPredictibilityAnalysisId(
        predictibilityAnalysis.getPredictibilityAnalysisId());
    MethodologyScoreResult scoreResult = methodologyScoreCalculator.computeScores(votesList);
    List<MethodologyScoreModel> methodologyScoreList = scoreResult.methodologyScores();

    persistPredictibilityResults(predictibilityAnalysis, methodologyScoreList, analyseId);

    // En cas d'égalité tranchée par le chef, l'association methodology doit refléter son choix
    // manuel (chosenMethodology) et non l'auto-pick, sinon le score de l'étape 3 (loadScorableMethods)
    // serait calculé sur une autre méthodologie que celle choisie.
    String effectiveMethodology = predictibilityAnalysis.getChosenMethodology() != null
        ? predictibilityAnalysis.getChosenMethodology()
        : scoreResult.chosenMethodology().getFrenchLabel();
    predictibilityAnalysis.setMethodology(methodologyRepository.findByName(effectiveMethodology));
    predictibilityAnalysisRepository.save(predictibilityAnalysis);

    PredictibilityScoreResponse response = predictibilityMapper.toScoreResponse(predictibilityAnalysis);
    response.setAverage(average);
    response.setMethodologies(new ArrayList<>(methodologyScoreList));
    response.setFormSteps(formStepModels);
    response.setTieBreakRequired(
        scoreResult.tie() && predictibilityAnalysis.getChosenMethodology() == null);
    log.info("Computed predictibility score for analysis {} (project {})", analyseId, projectId);
    return response;
  }

  /**
   * Persists the predictibility results for each methodology score, unless results already exist
   * for the analysis.
   *
   * @param predictibilityAnalysis the analysis the results belong to
   * @param methodologyScores      the methodology scores to persist
   * @param analyseId              the analysis id, used for logging
   */
  private void persistPredictibilityResults(final PredictibilityAnalysis predictibilityAnalysis,
      final List<MethodologyScoreModel> methodologyScores, final Long analyseId) {
    List<PredictibilityResults> existingResults =
        predictibilityResultsRepository.findAllPredictibilityResultsByPreAnalysisId(
            predictibilityAnalysis.getPredictibilityAnalysisId());
    if (!existingResults.isEmpty()) {
      log.warn("Predictibility results already persisted for analysis {}", analyseId);
      return;
    }
    for (MethodologyScoreModel methodologyScore : methodologyScores) {
      PredictibilityResults predictibilityResults = new PredictibilityResults();
      predictibilityResults.setPredictibilityAnalysis(predictibilityAnalysis);
      predictibilityResults.setName(methodologyScore.getName());
      predictibilityResults.setValue(methodologyScore.getValue());
      predictibilityResultsRepository.save(predictibilityResults);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public String putIsFinishedPredictibilityAnalysis(final Long projectId, final Long analyseId)
      throws PredictibilityAnalysisNotFoundException, PredictibilityAnalysisAlreadyUpdated,
      ProjectNotFoundException {
    PredictibilityAnalysis predictibilityAnalysis = predictibilityAnalysisRepository.findById(analyseId)
        .orElseThrow(() -> new PredictibilityAnalysisNotFoundException(ANALYSE_NOT_FOUND_MESSAGE));
    if (!Objects.equals(
        predictibilityAnalysis.getMultiCriteriaAnalysis().getProject().getProjectId(), projectId)) {
      throw new ProjectNotFoundException(Constants.PROJECT_DOESNT_EXIST);
    }

    if (Boolean.FALSE.equals(predictibilityAnalysis.getIsFinished())) {
      predictibilityAnalysis.setIsFinished(true);
      predictibilityAnalysisRepository.save(predictibilityAnalysis);
      log.info("Predictibility analysis {} (project {}) marked as finished", analyseId, projectId);
    } else {
      throw new PredictibilityAnalysisAlreadyUpdated("L'analyse de prévisibilité est déjà terminée.");
    }
    return "L'analyse de prévisibilité a bien été enregistré comme terminée.";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional
  public String increasePredictAnalysisIterationNum(final Long projectId, final Long analyseId)
      throws PredictibilityAnalysisNotFoundException, ProjectNotFoundException {
    PredictibilityAnalysis predictibilityAnalysis = predictibilityAnalysisRepository.findById(analyseId)
        .orElseThrow(() -> new PredictibilityAnalysisNotFoundException(ANALYSE_NOT_FOUND_MESSAGE));

    if (!Objects.equals(
        predictibilityAnalysis.getMultiCriteriaAnalysis().getProject().getProjectId(), projectId)) {
      throw new ProjectNotFoundException(
          "Le project dans le path et celui de l'analyse ne correspondent pas");
    }

    predictibilityAnalysis.setPredictibilityAnalysisIteration(
        predictibilityAnalysis.getPredictibilityAnalysisIteration() + 1);
    predictibilityAnalysisRepository.save(predictibilityAnalysis);
    log.info("Predictibility analysis {} (project {}) moved to iteration {}",
        analyseId, projectId, predictibilityAnalysis.getPredictibilityAnalysisIteration());
    return "L'analyse de prévisibilité a rentre dans une nouvelle iteration.";
  }
}
