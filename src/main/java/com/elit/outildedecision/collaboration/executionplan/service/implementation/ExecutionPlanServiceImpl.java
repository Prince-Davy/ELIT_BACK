package com.elit.outildedecision.collaboration.executionplan.service.implementation;

import com.elit.outildedecision.collaboration.executionplan.dto.ExecutionPlanAnswerRequest;
import com.elit.outildedecision.collaboration.executionplan.dto.ExecutionPlanFormResponse;
import com.elit.outildedecision.collaboration.executionplan.dto.ExecutionPlanScoreResponse;
import com.elit.outildedecision.collaboration.executionplan.dto.ExecutionVoteRequest;
import com.elit.outildedecision.collaboration.executionplan.dto.ExecutionVoteResponse;
import com.elit.outildedecision.collaboration.executionplan.dto.ExecutionVoteUserResponse;
import com.elit.outildedecision.collaboration.executionplan.calculate.ExecutionPlanScoreCalculator;
import com.elit.outildedecision.collaboration.executionplan.calculate.ExecutionPlanVoteScore;
import com.elit.outildedecision.collaboration.executionplan.entity.ExecutionPlan;
import com.elit.outildedecision.collaboration.executionplan.entity.ExecutionPlanAnswer;
import com.elit.outildedecision.collaboration.executionplan.entity.ExecutionPlanQuestion;
import com.elit.outildedecision.collaboration.executionplan.entity.ExecutionPlanVote;
import com.elit.outildedecision.collaboration.executionplan.exception.ExecutionPlanException;
import com.elit.outildedecision.collaboration.executionplan.exception.VoteAlreadyExistException;
import com.elit.outildedecision.collaboration.executionplan.mapper.ExecutionPlanMapper;
import com.elit.outildedecision.collaboration.executionplan.repository.ExecutionPlanAnswerRepository;
import com.elit.outildedecision.collaboration.executionplan.repository.ExecutionPlanQuestionRepository;
import com.elit.outildedecision.collaboration.executionplan.repository.ExecutionPlanRepository;
import com.elit.outildedecision.collaboration.executionplan.repository.ExecutionPlanVoteRepository;
import com.elit.outildedecision.collaboration.executionplan.service.ExecutionPlanService;
import com.elit.outildedecision.collaboration.multicriteria.entity.MultiCriteriaAnalysis;
import com.elit.outildedecision.collaboration.multicriteria.repository.MultiCriteriaAnalysisRepository;
import com.elit.outildedecision.shared.methodology.entity.Method;
import com.elit.outildedecision.shared.methodology.entity.Methodology;
import com.elit.outildedecision.collaboration.predictibility.entity.PredictibilityAnalysis;
import com.elit.outildedecision.collaboration.predictibility.entity.PredictibilityResults;
import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.project.entity.ProjectUserPermission;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.collaboration.executionplan.dto.PredictibilityResultsResponseModel;
import com.elit.outildedecision.shared.methodology.repository.MethodRepository;
import com.elit.outildedecision.shared.methodology.repository.MethodologyRepository;
import com.elit.outildedecision.collaboration.predictibility.repository.PredictibilityAnalysisRepository;
import com.elit.outildedecision.collaboration.predictibility.repository.PredictibilityResultsRepository;
import com.elit.outildedecision.shared.exception.ConflictException;
import com.elit.outildedecision.shared.exception.ExecutionPlanNotFoundException;
import com.elit.outildedecision.shared.exception.NotFoundException;
import com.elit.outildedecision.shared.exception.UserNotAuthorizedException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.project.repository.ProjectRepository;
import com.elit.outildedecision.shared.project.repository.ProjectUserPermissionRepository;
import com.elit.outildedecision.shared.user.repository.UserRepository;
import com.elit.outildedecision.shared.util.ProjectState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of {@link ExecutionPlanService}: manages the execution plan lifecycle (creation,
 * form constraints, methodology votes, scoring and completion).
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ExecutionPlanServiceImpl implements ExecutionPlanService {

  private static final String PLAN_EXECUTION_NOT_FOUND_MESSAGE = "Plan d'exécution introuvable.";

  private final ExecutionPlanAnswerRepository executionPlanAnswerRepository;
  private final ExecutionPlanQuestionRepository executionPlanQuestionRepository;
  private final ExecutionPlanRepository executionPlanRepository;
  private final ExecutionPlanVoteRepository executionPlanVoteRepository;
  private final UserRepository userRepository;
  private final ProjectUserPermissionRepository projectUserPermissionRepository;
  private final MethodRepository methodRepository;
  private final MethodologyRepository methodologyRepository;
  private final ProjectRepository projectRepository;
  private final PredictibilityAnalysisRepository predictibilityAnalysisRepository;
  private final MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository;
  private final PredictibilityResultsRepository predictibilityResultsRepository;
  private final ExecutionPlanMapper executionPlanMapper;
  private final ExecutionPlanScoreCalculator executionPlanScoreCalculator;

  /** {@inheritDoc} */
  @Override
  @Transactional
  public Long createExecutionPlan(final Long projectId) {
    log.info("Creating execution plan for projectId={}", projectId);
    Project project =
        projectRepository
            .findById(projectId)
            .orElseThrow(() -> new ProjectNotFoundException("Ce projet n'existe pas"));

    MultiCriteriaAnalysis lastMultiCriteriaAnalysis =
        multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(
            project.getProjectId());
    if (lastMultiCriteriaAnalysis == null) {
      throw new NotFoundException("Aucune analyse multicritère pour ce projet.");
    }
    PredictibilityAnalysis lastPredictibilityAnalysis =
        predictibilityAnalysisRepository.findByLastIterationAndMultiCriteriaId(
            lastMultiCriteriaAnalysis.getMultiCriteriaAnalysisId());
    if (lastPredictibilityAnalysis == null
        || !Boolean.TRUE.equals(lastPredictibilityAnalysis.getIsFinished())) {
      throw new ConflictException(
          "La prévisibilité doit être finalisée avant de créer le plan d'exécution.");
    }

    ExecutionPlan lastExecutionPlan =
        executionPlanRepository.findByLastIterationAndPredictibilityId(
            lastPredictibilityAnalysis.getPredictibilityAnalysisId());

    int nextIteration = 1;

    if (lastExecutionPlan != null) {
      if (Boolean.FALSE.equals(lastExecutionPlan.getIsFinished())) {
        log.info("Returning in-progress execution plan id={} for projectId={}",
            lastExecutionPlan.getExecutionPlanId(), projectId);
        return lastExecutionPlan.getExecutionPlanId();
      }
      nextIteration = lastExecutionPlan.getExecutionIteration() + 1;
    }

    ExecutionPlan executionPlan =
        executionPlanRepository.save(
            ExecutionPlan.builder()
                .predictibilityAnalysis(lastPredictibilityAnalysis)
                .isFinished(false)
                .executionIteration(nextIteration)
                .build());

    List<ExecutionPlanQuestion> executionPlanQuestions = executionPlanQuestionRepository.findAll();
    for (ExecutionPlanQuestion executionPlanQuestion : executionPlanQuestions) {
      executionPlanAnswerRepository.save(
          ExecutionPlanAnswer.builder()
              .answer(null)
              .executionPlan(executionPlan)
              .executionPlanQuestion(executionPlanQuestion)
              .build());
    }

    log.info("Created execution plan id={} (iteration={}) for projectId={}",
        executionPlan.getExecutionPlanId(), nextIteration, projectId);
    return executionPlan.getExecutionPlanId();
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public List<ExecutionPlanFormResponse> getQuestionsAndAnswers(final Long projectId, final Long executionPlanId)
      throws ProjectNotFoundException, ExecutionPlanNotFoundException {
    ExecutionPlan executionPlan = checkAssociationProjectAndExecutionPlan(projectId, executionPlanId);
    return executionPlanMapper.toFormResponses(executionPlan.getExecutionPlanAnswers());
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public List<String> updateAnswers(final Long projectId,
                                    final Long executionPlanId,
                                    final List<ExecutionPlanAnswerRequest> answers)
      throws ProjectNotFoundException, ExecutionPlanNotFoundException {
    checkAssociationProjectAndExecutionPlan(projectId, executionPlanId);
    List<String> result = new ArrayList<>();
    for (ExecutionPlanAnswerRequest answerRequest : answers) {
      ExecutionPlanAnswer executionPlanAnswerFromDb =
          executionPlanAnswerRepository.findByExecutionPlanIdAndExecutionPlanQuestionId(
              executionPlanId, answerRequest.getExecutionPlanQuestionId());
      if (Objects.nonNull(executionPlanAnswerFromDb)) {
        executionPlanAnswerFromDb.setAnswer(answerRequest.getAnswer());
        executionPlanAnswerRepository.save(executionPlanAnswerFromDb);
        result.add(
            "La réponse avec l'id "
                + executionPlanAnswerFromDb.getExecutionPlanAnswerId()
                + " a bien été modifié.");
      }
    }
    log.info("Updated {} answer(s) for executionPlanId={} (projectId={})",
        result.size(), executionPlanId, projectId);
    return result;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public List<ExecutionVoteResponse> getMethodologies(final Long projectId, final Long executionPlanId)
      throws ProjectNotFoundException, ExecutionPlanNotFoundException {
    User user = getAuthenticatedUser();
    ExecutionPlan executionPlan = checkAssociationProjectAndExecutionPlan(projectId, executionPlanId);
    PredictibilityAnalysis predictibilityAnalysis = executionPlan.getPredictibilityAnalysis();
    Project project = predictibilityAnalysis.getMultiCriteriaAnalysis().getProject();
    ProjectUserPermission projectUserPermission =
        projectUserPermissionRepository.findByProjectIdUserID(
            project.getProjectId(), user.getUserId());

    String choosenMethodology = predictibilityAnalysis.getChosenMethodology();
    List<Method> methodList;
    if (choosenMethodology != null
        && (choosenMethodology.equalsIgnoreCase("Hybride") || choosenMethodology.equalsIgnoreCase("Hybrid"))) {
      methodList = methodRepository.findAll();
    } else if (choosenMethodology != null) {
      Methodology chosenMethodologyEntity = methodologyRepository.findByName(choosenMethodology);
      methodList = Objects.nonNull(chosenMethodologyEntity)
          ? chosenMethodologyEntity.getMethods()
          : predictibilityAnalysis.getMethodology().getMethods();
    } else {
      methodList = predictibilityAnalysis.getMethodology().getMethods();
    }

    List<ExecutionVoteResponse> result = new ArrayList<>();
    for (Method method : methodList) {
      ExecutionVoteResponse model = new ExecutionVoteResponse();
      model.setId(method.getId());
      model.setAdvantages(method.getAdvantages());
      model.setDisadvantages(method.getDisadvantages());
      model.setMethodologyName(method.getMethodology().getName());
      model.setMethodName(method.getName());
      model.setMethodDescription(method.getDescription());

      List<ExecutionPlanVote> votesEntities =
          executionPlanVoteRepository.findAllVotesByMethodIdAndExecutionPlanId(
              method.getId(), executionPlanId);
      List<ExecutionVoteUserResponse> votesList = new ArrayList<>();
      for (ExecutionPlanVote vote : votesEntities) {
        ExecutionVoteUserResponse voteModel = new ExecutionVoteUserResponse();
        voteModel.setId(vote.getId());
        voteModel.setUserId(vote.getUser().getUserId());
        votesList.add(voteModel);
      }
      model.setVotes(votesList);
      result.add(model);
    }
    return result;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public String postMethodologiesVote(final Long projectId, final ExecutionVoteRequest request)
      throws VoteAlreadyExistException, ProjectNotFoundException, ExecutionPlanNotFoundException {
    User user = getAuthenticatedUser();
    checkAssociationProjectAndExecutionPlan(projectId, request.getExecutionPlanId());
    ExecutionPlanVote executionPlanVoteFromDb =
        executionPlanVoteRepository.findVoteByUserIdAndExecutionPlanId(
            user.getUserId(), request.getExecutionPlanId());
    if (Objects.nonNull(executionPlanVoteFromDb)) {
      throw new VoteAlreadyExistException(
          "Cet utilisateur a déjà soumis un vote sur ce plan d'exécution.");
    }
    saveVote(user, request.getMethodId(), request.getExecutionPlanId());
    if (request.getMethodId2ForHybrid() != null) {
      saveVote(user, request.getMethodId2ForHybrid(), request.getExecutionPlanId());
    }
    log.info("Registered methodology vote(s) for userId={} on executionPlanId={} (projectId={})",
        user.getUserId(), request.getExecutionPlanId(), projectId);
    return "Le vote a bien été enregistré.";
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public ExecutionPlanScoreResponse getExecutionPlanScore(final Long projectId, final Long executionPlanId)
      throws ExecutionPlanNotFoundException, ProjectNotFoundException {
    ExecutionPlan executionPlan = checkAssociationProjectAndExecutionPlan(projectId, executionPlanId);
    return buildScore(executionPlan);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public ExecutionPlanScoreResponse getExecutionPlanScore(final Long executionPlanId)
      throws UserNotAuthorizedException, ExecutionPlanNotFoundException {
    User user = getAuthenticatedUser();
    ExecutionPlan executionPlan =
        executionPlanRepository
            .findById(executionPlanId)
            .orElseThrow(() -> new ExecutionPlanNotFoundException(PLAN_EXECUTION_NOT_FOUND_MESSAGE));
    Project project = executionPlan.getPredictibilityAnalysis().getMultiCriteriaAnalysis().getProject();
    ProjectUserPermission projectUserPermission =
        projectUserPermissionRepository.findByProjectIdUserID(
            project.getProjectId(), user.getUserId());
    if (Objects.isNull(projectUserPermission)) {
      throw new UserNotAuthorizedException("L'utilisateur ne fait pas parti du projet.");
    }
    return buildScore(executionPlan);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public boolean putIsFinishedExecutionPlan(final Long projectId, final Long executionPlanId)
      throws ExecutionPlanNotFoundException, ExecutionPlanException, ProjectNotFoundException {
    ExecutionPlan executionPlan = checkAssociationProjectAndExecutionPlan(projectId, executionPlanId);

    if (Boolean.FALSE.equals(executionPlan.getIsFinished())) {
      executionPlan.setIsFinished(true);
      executionPlanRepository.save(executionPlan);

      Project project = projectRepository.findById(projectId)
          .orElseThrow(() -> new ProjectNotFoundException("Projet introuvable avec l'ID : " + projectId));
      project.setProjectState(ProjectState.COMPLETED);
      projectRepository.save(project);
      log.info("Execution plan id={} marked as finished, projectId={} set to COMPLETED",
          executionPlanId, projectId);
    } else {
      throw new ExecutionPlanException("Le plan d'exécution est déjà terminé.");
    }
    return true;
  }

  /**
   * Builds the score response of an execution plan from its votes and predictibility results.
   *
   * @param executionPlan the execution plan
   * @return the execution plan score
   */
  private ExecutionPlanScoreResponse buildScore(final ExecutionPlan executionPlan) {
    PredictibilityAnalysis predictibilityAnalysis = executionPlan.getPredictibilityAnalysis();

    List<ExecutionPlanVote> votesList =
        executionPlanVoteRepository.findAllVotesByExecutionPlanId(executionPlan.getExecutionPlanId());
    List<Method> allMethods = loadScorableMethods(predictibilityAnalysis);
    ExecutionPlanVoteScore voteScore = executionPlanScoreCalculator.computeVoteScore(allMethods, votesList);

    List<PredictibilityResults> predictibilityResultsList =
        predictibilityResultsRepository.findAllPredictibilityResultsByPreAnalysisId(
            predictibilityAnalysis.getPredictibilityAnalysisId());
    List<PredictibilityResultsResponseModel> predictibilityResults = new ArrayList<>();
    for (PredictibilityResults predictibilityResult : predictibilityResultsList) {
      PredictibilityResultsResponseModel predictibilityResultNew =
          new PredictibilityResultsResponseModel();
      predictibilityResultNew.setName(predictibilityResult.getName());
      predictibilityResultNew.setValue(predictibilityResult.getValue());
      predictibilityResults.add(predictibilityResultNew);
    }

    ExecutionPlanScoreResponse result = new ExecutionPlanScoreResponse();
    result.setExecutionPlanId(executionPlan.getExecutionPlanId());
    result.setAllMethods(voteScore.methodScores());
    result.setCountVotes(voteScore.totalVotes());
    result.setExecutionPlanIteration(executionPlan.getExecutionIteration());
    result.setChooseMethod(voteScore.chosenMethods());
    result.setPredictibilityResults(predictibilityResults);
    result.setIsFinished(executionPlan.getIsFinished());
    return result;
  }

  /**
   * Loads the methods that can be scored for the given predictibility analysis: the methods of the
   * chosen methodology for {@code Classique} and {@code Agile}, all methods otherwise (hybrid).
   *
   * @param predictibilityAnalysis the predictibility analysis holding the chosen methodology
   * @return the scorable methods
   */
  private List<Method> loadScorableMethods(final PredictibilityAnalysis predictibilityAnalysis) {
    String methodologyName = predictibilityAnalysis.getMethodology().getName();
    if (methodologyName.equals("Classique") || methodologyName.equals("Agile")) {
      return methodRepository.findAllMethodsByMethodologyId(
          predictibilityAnalysis.getMethodology().getMethodologyId());
    }
    return methodRepository.findAll();
  }

  /**
   * Persists a single methodology vote for the given user, method and execution plan.
   *
   * @param user the voting user
   * @param methodId the voted method id
   * @param executionPlanId the execution plan id
   */
  private void saveVote(final User user, final Long methodId, final Long executionPlanId) {
    ExecutionPlanVote executionPlanVote = new ExecutionPlanVote();
    executionPlanVote.setUser(user);
    executionPlanVote.setMethod(methodRepository.getById(methodId));
    executionPlanVote.setExecutionPlan(executionPlanRepository.getById(executionPlanId));
    executionPlanVoteRepository.save(executionPlanVote);
  }

  /**
   * Resolves the currently authenticated user from the security context.
   *
   * @return the authenticated user
   */
  private User getAuthenticatedUser() {
    UserDetails userDetails =
        (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return userRepository.findByUserEmail(userDetails.getUsername());
  }

  /**
   * Loads the execution plan and verifies it belongs to the given project.
   *
   * @param projectId the project id from the request path
   * @param executionPlanId the execution plan id
   * @return the matching execution plan
   * @throws ExecutionPlanNotFoundException when the execution plan is not found
   * @throws ProjectNotFoundException when the project does not match the execution plan
   */
  private ExecutionPlan checkAssociationProjectAndExecutionPlan(final Long projectId, final Long executionPlanId)
      throws ExecutionPlanNotFoundException, ProjectNotFoundException {
    ExecutionPlan executionPlan =
        executionPlanRepository
            .findById(executionPlanId)
            .orElseThrow(() -> new ExecutionPlanNotFoundException(PLAN_EXECUTION_NOT_FOUND_MESSAGE));
    if (!executionPlan.getPredictibilityAnalysis().getMultiCriteriaAnalysis().getProject().getProjectId().equals(projectId)) {
      throw new ProjectNotFoundException("Le project dans le path et celui de Plan d'exécution ne correspondent pas");
    }
    return executionPlan;
  }
}
