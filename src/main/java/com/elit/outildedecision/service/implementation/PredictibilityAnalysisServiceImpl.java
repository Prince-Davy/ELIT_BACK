
package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.FormStep;
import com.elit.outildedecision.entity.MultiCriteriaAnalysis;
import com.elit.outildedecision.entity.PredictibilityAnalysis;
import com.elit.outildedecision.entity.PredictibilityResults;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.ProjectUserPermission;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.entity.Vote;
import com.elit.outildedecision.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.exception.PredictibilityAnalysisAlreadyUpdated;
import com.elit.outildedecision.exception.UserNotAuthorizedException;
import com.elit.outildedecision.model.FormStepModel;
import com.elit.outildedecision.model.MethodologyScoreModel;
import com.elit.outildedecision.model.PredictibilityScoreModel;
import com.elit.outildedecision.repository.MethodologyRepository;
import com.elit.outildedecision.repository.MultiCriteriaAnalysisRepository;
import com.elit.outildedecision.repository.PredictibilityAnalysisRepository;
import com.elit.outildedecision.repository.PredictibilityResultsRepository;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.repository.ProjectUserPermissionRepository;
import com.elit.outildedecision.repository.UserRepository;
import com.elit.outildedecision.repository.VoteRepository;
import com.elit.outildedecision.service.PredictibilityAnalysisService;
import com.elit.outildedecision.util.MethodologyTypes;
import com.elit.outildedecision.util.ProjectPermession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 * The PredictibilityAnalysisServiceImpl class implements the PredictibilityAnalysisService interface
 * and provides the implementation of the service methods for managing Predictibility Analysis.
 */
@Service
public class PredictibilityAnalysisServiceImpl implements PredictibilityAnalysisService {
  /**
   * A repository for Projects.
   */
  @Autowired
  private final ProjectRepository projectRepository;

  /**
   * A repository for Multi-Criteria Analysis.
   */
  private final MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository;

  /**
   * A repository for Predictibility Analysis.
   */
  private final PredictibilityAnalysisRepository predictibilityAnalysisRepository;

  /**
   * A repository for Votes.
   */
  private final VoteRepository voteRepository;

  /**
   * A repository for Users.
   */
  private final UserRepository userRepository;

  /**
   * A repository for Project User Permissions.
   */
  private final ProjectUserPermissionRepository projectUserPermissionRepository;

  /**
   * A repository for Methodologies.
   */
  private final MethodologyRepository methodologyRepository;

  /**
   * A repository for Predictibility Results.
   */
  private final PredictibilityResultsRepository predictibilityResultsRepository;
  /**
   * A constant for the sum formula.
   */
  private static final float SUM_CONSTANT = 0.2f;

  /**
   * A logger for logging application events and errors.
   */
  private final Logger logger;
  /**
   * Constructs a PredictibilityAnalysisServiceImpl object.
   * uses the constructor-based dependency injection to inject the repositories.
   *
   * @param multiCriteriaAnalysisRepository  A repository for Multi-Criteria Analysis.
   * @param predictibilityAnalysisRepository A repository for Predictibility Analysis.
   * @param voteRepository                   A repository for Votes.
   * @param userRepository                   A repository for Users.
   * @param projectUserPermissionRepository  A repository for Project User Permissions.
   * @param methodologyRepository            A repository for Methodologies.
   * @param predictibilityResultsRepository  A repository for Predictibility Results.
   * @param projectRepository                A repository for Project
   */
  public PredictibilityAnalysisServiceImpl(
    final MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository,
    final PredictibilityAnalysisRepository predictibilityAnalysisRepository,
    final VoteRepository voteRepository,
    final UserRepository userRepository,
    final ProjectUserPermissionRepository projectUserPermissionRepository,
    final MethodologyRepository methodologyRepository,
    final PredictibilityResultsRepository predictibilityResultsRepository,
    final ProjectRepository projectRepository) {
    this.multiCriteriaAnalysisRepository = multiCriteriaAnalysisRepository;
    this.predictibilityAnalysisRepository = predictibilityAnalysisRepository;
    this.voteRepository = voteRepository;
    this.userRepository = userRepository;
    this.projectUserPermissionRepository = projectUserPermissionRepository;
    this.methodologyRepository = methodologyRepository;
    this.predictibilityResultsRepository = predictibilityResultsRepository;
    this.projectRepository = projectRepository;
    this.logger = LoggerFactory.getLogger(PredictibilityAnalysisServiceImpl.class);
  }

  /**
   * Creates a Predictibility Analysis for a Project.
   *
   * @param projectId The id of the Project.
   * @return The id of the created Predictibility Analysis.
   */
  @Override
  public ResponseEntity<Long> createPredictibilityForm(final Long projectId) {
    Project project =
        projectRepository
            .findById(projectId)
            .orElseThrow(() -> new RuntimeException("Ce projet n'existe pas"));
    MultiCriteriaAnalysis lastMultiCriteriaAnalysis =
        multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(
            project.getProjectId());

    // "Vous n'avez pas encore terminé(e) l'analyse multicritère"
    if (Boolean.FALSE.equals(lastMultiCriteriaAnalysis.getIsFinished())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(-1L);
    }

    PredictibilityAnalysis lastPredictibilityAnalysis =
        predictibilityAnalysisRepository.findByLastIterationAndMultiCriteriaId(
            lastMultiCriteriaAnalysis.getMultiCriteriaAnalysisId());

    int nextIteration = 1;

    if (lastPredictibilityAnalysis != null) {
      return ResponseEntity.ok().body(lastPredictibilityAnalysis.getPredictibilityAnalysisId());
    }

    PredictibilityAnalysis predictibilityAnalysis =
        predictibilityAnalysisRepository.save(
            PredictibilityAnalysis.builder()
                .multiCriteriaAnalysis(lastMultiCriteriaAnalysis)
                .isFinished(false)
                .predictibilityAnalysisIteration(nextIteration)
                .build());
    return ResponseEntity.ok().body(predictibilityAnalysis.getPredictibilityAnalysisId());
  }

  /**
   * Calculates the predictibility analysis score for a given analysis ID.
   *
   * @param analyseId The ID of the predictibility analysis to calculate the score for.
   * @return A ResponseEntity containing the predictibility score model.
   * @throws PredictibilityAnalysisNotFoundException If the given analysis ID is invalid.
   */
  @Override
  public ResponseEntity<PredictibilityScoreModel> calculatePredictibilityAnalysisScore(final Long analyseId) throws PredictibilityAnalysisNotFoundException {
    PredictibilityAnalysis predictibilityAnalysis =
        predictibilityAnalysisRepository
            .findById(analyseId)
            .orElseThrow(() -> new PredictibilityAnalysisNotFoundException("Cet analyse n'existe pas"));

    List<FormStepModel> formStepModels = new ArrayList<>();

    Float average = (float) 0;
    List<FormStep> formSteps = predictibilityAnalysis.getMultiCriteriaAnalysis().getFormSteps();
    for (FormStep formStep : formSteps) {
      average += formStep.getEScore();
      formStepModels.add(
          new FormStepModel(
              formStep.getStepId(),
              formStep.getStepName(),
              formStep.getStepRef(),
              formStep.getEScore(),
              formStep.getMultiCriteriaAnalysis().getMultiCriteriaAnalysisId()));
    }
    average = average / formSteps.size();

    PredictibilityScoreModel predictibilityResult = new PredictibilityScoreModel();
    List<Vote> votesList =
        voteRepository.findAllByPredictibilityAnalysisId(
            predictibilityAnalysis.getPredictibilityAnalysisId());
    ArrayList<MethodologyScoreModel> methodologyScoreList = new ArrayList<>();
    ArrayList<Integer> intVotesList = new ArrayList<>();
    List<PredictibilityResults> predictibilityResultsList =
        predictibilityResultsRepository.findAllPredictibilityResultsByPreAnalysisId(
            predictibilityAnalysis.getPredictibilityAnalysisId());
    for (MethodologyTypes methodologyName : MethodologyTypes.values()) {
      // Count nbs of votesList
      long usersCount =
          votesList.stream()
              .filter(
                  vote ->
                      vote.getChosenPMM().equals(methodologyName.name()) && vote.getIsConfirmed())
              .count();

      // Count nbs of votesList per methodology
      int voteSum =
          votesList.stream()
              .filter(
                  vote ->
                      vote.getChosenPMM().equals(methodologyName.name()) && vote.getIsConfirmed())
              .map(Vote::getPredictibilityValue)
              .mapToInt(v -> v)
              .sum();
      // Add in array votes per methodology
      intVotesList.add(voteSum);

      float result = (voteSum * SUM_CONSTANT) / usersCount;
      MethodologyScoreModel methodologyScoreModel =
          MethodologyScoreModel.builder()
              .name(methodologyName.name())
              .value(Float.isNaN(result) ? 0 : result)
              .valueVoteSum(voteSum)
              .nbVotes((int) usersCount)
              .build();

      methodologyScoreList.add(methodologyScoreModel);

      if (predictibilityResultsList.isEmpty()) {
        PredictibilityResults predictibilityResults = new PredictibilityResults();
        predictibilityResults.setPredictibilityAnalysis(predictibilityAnalysis);
        predictibilityResults.setName(methodologyScoreModel.getName());
        predictibilityResults.setValue(methodologyScoreModel.getValue());
        predictibilityResultsRepository.save(predictibilityResults);
      } else {
        logger.error("Les résultats de l'analyse de prévisibilité sont déjà inscris en base de données.");
      }
    }
    predictibilityResult.setMethodologies(methodologyScoreList);
    predictibilityResult.setAverage(average);
    predictibilityResult.setFormSteps(formStepModels);
    predictibilityResult.setPredictibilityIteration(
        predictibilityAnalysis.getPredictibilityAnalysisIteration());

    // Search methodology with max votesList nbs
    int maxVoteMethodology =
        intVotesList.stream()
            .mapToInt(s -> s)
            .max()
            .orElseThrow(
                () -> new RuntimeException("Pas de valeur max trouvée pour les méthodologies."));

    // Compare all methodologies vote to get chooseMethodology
    MethodologyScoreModel chooseMethodology =
        methodologyScoreList.stream()
            .filter(m -> m.getValueVoteSum() == maxVoteMethodology)
            .findAny()
            .orElseThrow(() -> new RuntimeException("Aucune méthodologie trouvée."));

    // Convert name of methodology for valid name for entity
    switch (chooseMethodology.getName()) {
      case "CLASSIC":
        predictibilityAnalysis.setMethodology(
            methodologyRepository.findByName("Classique"));
        break;
      case "AGILE":
        predictibilityAnalysis.setMethodology(methodologyRepository.findByName("Agile"));
        break;
      case "HYBRID":
        predictibilityAnalysis.setMethodology(
            methodologyRepository.findByName("Hybride"));
        break;
      default:
        logger.error("La méthodologie n'est pas reconnue.");
        break;
    }


    predictibilityAnalysisRepository.save(predictibilityAnalysis);

    return ResponseEntity.ok().body(predictibilityResult);
  }
  /**
   * Marks a predictibility analysis as finished and saves the changes to the database.
   *
   * @param analyseId the ID of the predictibility analysis to update
   * @return a ResponseEntity containing a success message if the update was successful
   * @throws UserNotAuthorizedException if the user does not have permission to update the analysis
   * @throws PredictibilityAnalysisNotFoundException if the specified analysis does not exist
   * @throws PredictibilityAnalysisAlreadyUpdated if the analysis has already been marked as finished
   */
  @Override
  public ResponseEntity<String> putIsFinishedPredictibilityAnalysis(final Long analyseId)
    throws UserNotAuthorizedException, PredictibilityAnalysisNotFoundException, PredictibilityAnalysisAlreadyUpdated {
    PredictibilityAnalysis predictibilityAnalysis =
        predictibilityAnalysisRepository
            .findById(analyseId)
            .orElseThrow(
                () ->
                    new PredictibilityAnalysisNotFoundException(
                        "Cette analyse de prévisibilité n'existe pas."));
    Project project = predictibilityAnalysis.getMultiCriteriaAnalysis().getProject();
    if (checkPermissions(project.getProjectId(), ProjectPermession.CDP.name())) {
      if (Boolean.FALSE.equals(predictibilityAnalysis.getIsFinished())) {
        predictibilityAnalysis.setIsFinished(true);
        predictibilityAnalysisRepository.save(predictibilityAnalysis);
      } else {
        throw new PredictibilityAnalysisAlreadyUpdated("L'analyse de prévisibilité est déjà terminée.");
      }
    } else {
      throw new UserNotAuthorizedException(
          "Vous n'êtes pas autorisé à changer le statut de cette analyse de prévisibilité.");
    }
    return ResponseEntity.ok()
        .body("L'analyse de prévisibilité a bien été enregistré comme terminée.");
  }
  /**
   * Checks if the user has the specified permission on the specified project.
   *
   * @param projectId the ID of the project to check
   * @param permissionName the name of the permission to check
   * @return true if the user has the specified permission on the specified project, false otherwise
   */
  private boolean checkPermissions(final Long projectId, final String permissionName) {
    String userEmail =
        (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user = userRepository.findByUserEmail(userEmail);

    ProjectUserPermission projectUserPermission =
        projectUserPermissionRepository.findByProjectIdUserID(projectId, user.getUserId());
    return Objects.nonNull(projectUserPermission)
        && permissionName.equals(projectUserPermission.getPermission());
  }
  /**
   * Increases the iteration number of the specified predictibility analysis.
   *
   * @param analyseId the ID of the predictibility analysis to update
   * @return a ResponseEntity containing a success message if the update was successful
   * @throws UserNotAuthorizedException if the user does not have permission to update the analysis
   * @throws PredictibilityAnalysisNotFoundException if the specified analysis does not exist
   */
  @Override
  public ResponseEntity<String> increasePredictAnalysisIterationNum(final Long analyseId)
      throws PredictibilityAnalysisNotFoundException, UserNotAuthorizedException {
    PredictibilityAnalysis predictibilityAnalysis =
        predictibilityAnalysisRepository
            .findById(analyseId)
            .orElseThrow(
                () ->
                    new PredictibilityAnalysisNotFoundException(
                        "Cette analyse de prévisibilité n'existe pas."));
    Project project = predictibilityAnalysis.getMultiCriteriaAnalysis().getProject();
    if (checkPermissions(project.getProjectId(), ProjectPermession.CDP.name())) {
      predictibilityAnalysis.setPredictibilityAnalysisIteration(
          predictibilityAnalysis.getPredictibilityAnalysisIteration() + 1);
      predictibilityAnalysisRepository.save(predictibilityAnalysis);
    } else {
      throw new UserNotAuthorizedException(
          "Vous n'êtes pas autorisé à faire de changement dans cette analyse de prévisibilité.");
    }
    return ResponseEntity.ok()
        .body("L'analyse de prévisibilité a rentre dans une nouvelle iteration.");
  }
}
