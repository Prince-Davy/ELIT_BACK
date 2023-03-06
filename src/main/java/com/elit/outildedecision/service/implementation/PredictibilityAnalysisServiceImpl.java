package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.FormStep;
import com.elit.outildedecision.entity.MultiCriteriaAnalysis;
import com.elit.outildedecision.entity.PredictibilityAnalysis;
import com.elit.outildedecision.entity.PredictibilityResults;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.ProjectUserPermission;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.entity.Vote;
import com.elit.outildedecision.exception.PredictibilityAnaliseNotFoundException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class PredictibilityAnalysisServiceImpl implements PredictibilityAnalysisService {
  private final ProjectRepository projectRepository;
  private final MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository;
  private final PredictibilityAnalysisRepository predictibilityAnalysisRepository;
  private final VoteRepository voteRepository;
  private final UserRepository userRepository;
  private final ProjectUserPermissionRepository projectUserPermissionRepository;
  private final MethodologyRepository methodologyRepository;
  private final PredictibilityResultsRepository predictibilityResultsRepository;

  private Logger logger;

  public PredictibilityAnalysisServiceImpl(
      ProjectRepository projectRepository,
      MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository,
      PredictibilityAnalysisRepository predictibilityAnalysisRepository,
      VoteRepository voteRepository,
      UserRepository userRepository,
      ProjectUserPermissionRepository projectUserPermissionRepository,
      MethodologyRepository methodologyRepository,
      PredictibilityResultsRepository predictibilityResultsRepository) {
    this.projectRepository = projectRepository;
    this.multiCriteriaAnalysisRepository = multiCriteriaAnalysisRepository;
    this.predictibilityAnalysisRepository = predictibilityAnalysisRepository;
    this.voteRepository = voteRepository;
    this.userRepository = userRepository;
    this.projectUserPermissionRepository = projectUserPermissionRepository;
    this.methodologyRepository = methodologyRepository;
    this.predictibilityResultsRepository = predictibilityResultsRepository;
  }

  /**
   * @param projectId
   * @return ResponseEntity<Long>
   */
  @Override
  // @PreAuthorize("isProjectOwner(#projectId)")
  public ResponseEntity<Long> createPredictibilityForm(Long projectId) {
    Project project =
        projectRepository
            .findById(projectId)
            .orElseThrow(() -> new RuntimeException("Ce projet n'existe pas"));
    MultiCriteriaAnalysis lastMultiCriteriaAnalysis =
        multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(
            project.getProjectId());

    // "Vous n'avez pas encore terminé(e) l'analyse multicritère"
    if (!lastMultiCriteriaAnalysis.getIsFinished()) {
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

  @Override
  public ResponseEntity<PredictibilityScoreModel> calculatePredictibilityAnalysisScore(
      Long analyseId) {
    PredictibilityAnalysis predictibilityAnalysis =
        predictibilityAnalysisRepository
            .findById(analyseId)
            .orElseThrow(() -> new RuntimeException("Cet analyse n'existe pas"));

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

      float result = (float) (voteSum * 0.2) / usersCount;
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
            methodologyRepository.findMethodologyByName("Classique"));
        break;
      case "AGILE":
        predictibilityAnalysis.setMethodology(methodologyRepository.findMethodologyByName("Agile"));
        break;
      case "HYBRID":
        predictibilityAnalysis.setMethodology(
            methodologyRepository.findMethodologyByName("Hybride"));
        break;
      default:
        logger.error("La méthodologie n'est pas reconnue.");
        break;
    }


    predictibilityAnalysisRepository.save(predictibilityAnalysis);

    return ResponseEntity.ok().body(predictibilityResult);
  }

  @Override
  public ResponseEntity<String> putIsFinishedPredictibilityAnalysis(Long analyseId)
    throws UserNotAuthorizedException, PredictibilityAnaliseNotFoundException, PredictibilityAnalysisAlreadyUpdated {
    PredictibilityAnalysis predictibilityAnalysis =
        predictibilityAnalysisRepository
            .findById(analyseId)
            .orElseThrow(
                () ->
                    new PredictibilityAnaliseNotFoundException(
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

  private boolean checkPermissions(Long projectId, String permissionName) {
    String userEmail =
        (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user = userRepository.findByUserEmail(userEmail);

    ProjectUserPermission projectUserPermission =
        projectUserPermissionRepository.findByProjectIdUserID(projectId, user.getUserId());
    return Objects.nonNull(projectUserPermission)
        && permissionName.equals(projectUserPermission.getPermission());
  }

  @Override
  public ResponseEntity<String> increasePredictAnalysisIterationNum(Long analyseId)
      throws PredictibilityAnaliseNotFoundException, UserNotAuthorizedException {
    PredictibilityAnalysis predictibilityAnalysis =
        predictibilityAnalysisRepository
            .findById(analyseId)
            .orElseThrow(
                () ->
                    new PredictibilityAnaliseNotFoundException(
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
