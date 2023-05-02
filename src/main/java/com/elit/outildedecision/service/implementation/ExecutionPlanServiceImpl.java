
package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.ExecutionPlan;
import com.elit.outildedecision.entity.ExecutionPlanAnswer;
import com.elit.outildedecision.entity.ExecutionPlanQuestion;
import com.elit.outildedecision.entity.ExecutionPlanVote;
import com.elit.outildedecision.entity.Method;
import com.elit.outildedecision.entity.Methodology;
import com.elit.outildedecision.entity.MultiCriteriaAnalysis;
import com.elit.outildedecision.entity.PredictibilityAnalysis;
import com.elit.outildedecision.entity.PredictibilityResults;
import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.ProjectUserPermission;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.ExecutionPlanException;
import com.elit.outildedecision.exception.ExecutionPlanNotFoundException;
import com.elit.outildedecision.exception.UserNotAuthorizedException;
import com.elit.outildedecision.exception.VoteAlreadyExistException;
import com.elit.outildedecision.model.ExecutionPlanAnswerModel;
import com.elit.outildedecision.model.ExecutionPlanFormModel;
import com.elit.outildedecision.model.ExecutionPlanScoreResponseModel;
import com.elit.outildedecision.model.ExecutionVoteModel;
import com.elit.outildedecision.model.ExecutionVoteRequestModel;
import com.elit.outildedecision.model.ExecutionVoteResponseModel;
import com.elit.outildedecision.model.MethodModel;
import com.elit.outildedecision.model.PredictibilityResultsResponseModel;
import com.elit.outildedecision.repository.ExecutionPlanAnswerRepository;
import com.elit.outildedecision.repository.ExecutionPlanQuestionRepository;
import com.elit.outildedecision.repository.ExecutionPlanRepository;
import com.elit.outildedecision.repository.ExecutionPlanVoteRepo;
import com.elit.outildedecision.repository.MethodRepository;
import com.elit.outildedecision.repository.MultiCriteriaAnalysisRepository;
import com.elit.outildedecision.repository.PredictibilityAnalysisRepository;
import com.elit.outildedecision.repository.PredictibilityResultsRepository;
import com.elit.outildedecision.repository.ProjectRepository;
import com.elit.outildedecision.repository.ProjectUserPermissionRepository;
import com.elit.outildedecision.repository.UserRepository;
import com.elit.outildedecision.service.ExecutionPlanService;
import com.elit.outildedecision.util.ProjectPermession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * this class is the implementation of the ExecutionPlanService interface.
 * it contains all the methods that are used to manage the execution plan
 */
@Service
public class ExecutionPlanServiceImpl implements ExecutionPlanService {
  /**
   * the execution plan answer repository.
   */
  @Autowired
  private ExecutionPlanAnswerRepository executionPlanAnswerRepository;
  /**
   * the execution plan question repository.
   */
  @Autowired
  private ExecutionPlanQuestionRepository executionPlanQuestionRepository;
  /**
   * the execution plan repository.
   */
  @Autowired
  private ExecutionPlanRepository executionPlanRepository;
  /**
   * the user repository.
   */
  @Autowired
  private UserRepository userRepository;
  /**
   * the project user permission repository.
   */
  private final ProjectUserPermissionRepository projectUserPermissionRepository;
  /**
   * the method repository.
   */
  private final MethodRepository methodRepository;
  /**
   * the execution plan vote repository.
   */
  private final ExecutionPlanVoteRepo executionPlanVoteRepo;
  /**
   * the project repository.
   */
  private final ProjectRepository projectRepository;
  /**
   * the predictibility analysis repository.
   */
  private final PredictibilityAnalysisRepository predictibilityAnalysisRepository;
  /**
   * the multi criteria analysis repository.
   */
  private final MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository;
  /**
   * the predictibility results repository.
   */
  private final PredictibilityResultsRepository predictibilityResultsRepository;
  /**
   * the constructor of the class.
   * it's used to inject the dependencies
   * @param projectUserPermissionRepository the project user permission repository to inject
   * @param methodRepository the method repository to inject
   * @param executionPlanVoteRepo the execution plan vote repository to inject
   * @param projectRepository the project repository to inject
   * @param predictibilityAnalysisRepository the predictibility analysis repository to inject
   * @param multiCriteriaAnalysisRepository the multi criteria analysis repository to inject
   * @param predictibilityResultsRepository the predictibility results repository to inject
   */
  public ExecutionPlanServiceImpl(final ProjectUserPermissionRepository projectUserPermissionRepository,
                                  final MethodRepository methodRepository,
                                  final ExecutionPlanVoteRepo executionPlanVoteRepo,
                                  final ProjectRepository projectRepository,
                                  final PredictibilityAnalysisRepository predictibilityAnalysisRepository,
                                  final MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository,
                                  final PredictibilityResultsRepository predictibilityResultsRepository) {
    this.projectUserPermissionRepository = projectUserPermissionRepository;
    this.methodRepository = methodRepository;
    this.executionPlanVoteRepo = executionPlanVoteRepo;
    this.projectRepository = projectRepository;
    this.predictibilityAnalysisRepository = predictibilityAnalysisRepository;
    this.multiCriteriaAnalysisRepository = multiCriteriaAnalysisRepository;
    this.predictibilityResultsRepository = predictibilityResultsRepository;
  }

  private final Logger log = LoggerFactory.getLogger(ExecutionPlanServiceImpl.class);


  /**
   * this method is used to create an execution plan.
   *
   * @param projectId the id of the project to which the execution plan belongs
   * @return the id of the created execution plan
   */
  @Override
  public ResponseEntity<Long> createExecutionPlan(final Long projectId) {
    Project project =
        projectRepository
            .findById(projectId)
            .orElseThrow(() -> new RuntimeException("Ce projet n'existe pas"));

    MultiCriteriaAnalysis lastMultiCriteriaAnalysis =
        multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(
            project.getProjectId());
    PredictibilityAnalysis lastPredictibilityAnalysis =
        predictibilityAnalysisRepository.findByLastIterationAndMultiCriteriaId(
            lastMultiCriteriaAnalysis.getMultiCriteriaAnalysisId());

    ExecutionPlan lastExecutionPlan =
        executionPlanRepository.findByLastIterationAndPredictibilityId(
            lastPredictibilityAnalysis.getPredictibilityAnalysisId());

    int nextIteration = 1;

    if (lastExecutionPlan != null) {
      if (Boolean.FALSE.equals(lastExecutionPlan.getIsFinished())) {
        return ResponseEntity.ok().body(lastExecutionPlan.getExecutionPlanId());
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

    return ResponseEntity.ok().body(executionPlan.getExecutionPlanId());
  }
  /**
   * this method is used to get the execution plan questions and answers.
   * @return the list of the execution plan questions
   */
  @Override
  public ResponseEntity<Object> getQuestionsAndAnswers(final Long executionPlanId) {
    ExecutionPlan executionPlan =
        executionPlanRepository
            .findById(executionPlanId)
            .orElseThrow(() -> new RuntimeException("Execution plan introuvable."));

    List<ExecutionPlanFormModel> executionPlanFormModelList = new ArrayList<>();

    for (ExecutionPlanAnswer executionPlanAnswer : executionPlan.getExecutionPlanAnswers()) {
      executionPlanFormModelList.add(
          new ExecutionPlanFormModel(
              executionPlanAnswer.getAnswer(),
              executionPlanAnswer.getExecutionPlanQuestion().getTitle(),
              executionPlanAnswer.getExecutionPlanQuestion().getQuestion(),
              executionPlanAnswer.getExecutionPlanQuestion().getExecutionPlanQuestionId()));
    }

    return ResponseEntity.ok().body(executionPlanFormModelList);
  }
  /**
   * this method is used to update the execution plan answers.
   *
   * @param executionPlanAnswerModel the list of the execution plan answers
   * @param executionPlanId the id of the execution plan
   * @return response entity
   */
  @Override
  public ResponseEntity<Object> updateAnswers(final List<ExecutionPlanAnswerModel> executionPlanAnswerModel,
                                              final Long executionPlanId) {
    List<Object> result = new ArrayList<>();
    for (ExecutionPlanAnswerModel executionPlanAnswerUnit : executionPlanAnswerModel) {
      ExecutionPlanAnswer executionPlanAnswerFromDB =
          executionPlanAnswerRepository.findByExecutionPlanIdAndExecutionPlanQuestionId(
              executionPlanId, executionPlanAnswerUnit.getExecutionPlanQuestionId());

      if (Objects.nonNull(executionPlanAnswerFromDB)) {
        executionPlanAnswerFromDB.setAnswer(executionPlanAnswerUnit.getAnswer());
        executionPlanAnswerRepository.save(executionPlanAnswerFromDB);
        result.add(
            "La réponse avec l'id "
                + executionPlanAnswerFromDB.getExecutionPlanAnswerId()
                + " a bien été modifié.");
      }
    }
    return ResponseEntity.ok().body(result);
  }
  /**
   * this method is used to get the execution plan answers.
   *
   * @param executionPlanId the id of the execution plan
   * @return response entity
   */
  @Override
  public ResponseEntity<Object> getMethodologies(final Long executionPlanId) {
    String userEmail =
        (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user = userRepository.findByUserEmail(userEmail);
    ExecutionPlan executionPlan =
        executionPlanRepository
            .findById(executionPlanId)
            .orElseThrow(() -> new RuntimeException("Plan d'exécution introuvable."));
    PredictibilityAnalysis predictibilityAnalysis = executionPlan.getPredictibilityAnalysis();
    Project project = predictibilityAnalysis.getMultiCriteriaAnalysis().getProject();
    this.log.info("project = {}", project);
    ProjectUserPermission projectUserPermission =
        projectUserPermissionRepository.findByProjectIdUserID(
            project.getProjectId(), user.getUserId());

    Methodology methodology = predictibilityAnalysis.getMethodology();
    List<Method> methodList;
    if (methodology.getName().equals("Hybride")) {
      methodList = methodRepository.findAll();
    } else {
      methodList = methodology.getMethods();
    }

    List<ExecutionVoteResponseModel> result = new ArrayList<>();
    for (Method method : methodList) {
      ExecutionVoteResponseModel model = new ExecutionVoteResponseModel();
      model.setId(method.getId());
      model.setAdvantages(method.getAdvantages());
      model.setDisadvantages(method.getDisadvantages());
      model.setMethodologyName(method.getMethodology().getName());
      model.setMethodName(method.getName());
      model.setMethodDescription(method.getDescription());
      List<ExecutionPlanVote> votesModelList =
          projectUserPermission.getPermission().equals(ProjectPermession.CDP.name())
              ? executionPlanVoteRepo.findAllVotesByMethodIdAndExecutionPlanId(
                  method.getId(), executionPlanId)
              : executionPlanVoteRepo.findVoteByMethodIdUserIdAndExecutionPlanId(
                  method.getId(), user.getUserId(), executionPlanId);
      List<ExecutionVoteModel> votesList = new ArrayList<>();
      for (ExecutionPlanVote vote : votesModelList) {
        ExecutionVoteModel voteModel = new ExecutionVoteModel();
        voteModel.setId(vote.getId());
        voteModel.setUserId(
            projectUserPermission.getPermission().equals(ProjectPermession.CDP.name())
                ? vote.getUser().getUserId()
                : null);
        votesList.add(voteModel);
      }
      model.setVotes(votesList);
      result.add(model);
    }

    return ResponseEntity.ok().body(result);
  }
  /**
   * this method is used to vote for a method.
   *
   * @param executionVoteRequestModel the execution vote request model
   * @return response entity
   */
  @Override
  public ResponseEntity<String> postMethodologiesVote(
      final ExecutionVoteRequestModel executionVoteRequestModel) throws VoteAlreadyExistException {
    String userEmail =
        (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user = userRepository.findByUserEmail(userEmail);

    ExecutionPlan executionPlan =
        executionPlanRepository
            .findById(executionVoteRequestModel.getExecutionPlanId())
            .orElseThrow(() -> new RuntimeException("Ce plan d'exécution n'existe pas."));
    PredictibilityAnalysis predictibilityAnalysis = executionPlan.getPredictibilityAnalysis();
    Project project = predictibilityAnalysis.getMultiCriteriaAnalysis().getProject();
    ProjectUserPermission projectUserPermission =
        projectUserPermissionRepository.findByProjectIdUserID(
            project.getProjectId(), user.getUserId());

    if (projectUserPermission.getPermission().equals(ProjectPermession.CDP.name())
        || projectUserPermission.getPermission().equals(ProjectPermession.CONTRIBUTER.name())) {
      ExecutionPlanVote executionPlanVoteFromDB =
          executionPlanVoteRepo.findVoteByUserIdAndExecutionPlanId(
              user.getUserId(), executionVoteRequestModel.getExecutionPlanId());
      if (Objects.nonNull(executionPlanVoteFromDB)) {
        throw new VoteAlreadyExistException(
            "Cet utilisateur a déjà soumis un vote sur ce plan d'exécution.");
      }
      ExecutionPlanVote executionPlanVote = new ExecutionPlanVote();
      executionPlanVote.setUser(user);
      executionPlanVote.setMethod(
          methodRepository.getById(executionVoteRequestModel.getMethodId()));
      executionPlanVote.setExecutionPlan(
          executionPlanRepository.getById(executionVoteRequestModel.getExecutionPlanId()));
      executionPlanVoteRepo.save(executionPlanVote);

      if (executionVoteRequestModel.getMethodId2ForHybrid() != null) {
        ExecutionPlanVote executionPlanVote2 = new ExecutionPlanVote();
        executionPlanVote2.setUser(user);
        executionPlanVote2.setMethod(
            methodRepository.getById(executionVoteRequestModel.getMethodId2ForHybrid()));
        executionPlanVote2.setExecutionPlan(
            executionPlanRepository.getById(executionVoteRequestModel.getExecutionPlanId()));
        executionPlanVoteRepo.save(executionPlanVote2);
      }
    }

    return ResponseEntity.ok().body("Le vote a bien été enregistré.");
  }
  /**
   * this method is used to get the execution plan score.
   *
   * @param executionPlanId the id of the execution plan
   * @return response entity
   * @throws UserNotAuthorizedException the user not authorized exception if the user is not part of the project
   * @throws ExecutionPlanNotFoundException the execution plan not found exception if the execution plan is not found
   */
  @Override
  public ExecutionPlanScoreResponseModel getExecutionPlanScore(final Long executionPlanId) throws UserNotAuthorizedException, ExecutionPlanNotFoundException {
    String userEmail =
        (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user = userRepository.findByUserEmail(userEmail);
    ExecutionPlan executionPlan =
        executionPlanRepository
            .findById(executionPlanId)
            .orElseThrow(() -> new ExecutionPlanNotFoundException("Plan d'exécution introuvable."));
    PredictibilityAnalysis predictibilityAnalysis = executionPlan.getPredictibilityAnalysis();
    Project project = predictibilityAnalysis.getMultiCriteriaAnalysis().getProject();
    ProjectUserPermission projectUserPermission =
        projectUserPermissionRepository.findByProjectIdUserID(
            project.getProjectId(), user.getUserId());

    if (Objects.isNull(projectUserPermission)) {
      throw new UserNotAuthorizedException("L'utilisateur ne fait pas parti du projet.");
    }

    List<ExecutionPlanVote> votesList =
        executionPlanVoteRepo.findAllVotesByExecutionPlanId(executionPlanId);

    List<Method> allMethods;
    if (predictibilityAnalysis.getMethodology().getName().equals("Classique")
        || predictibilityAnalysis.getMethodology().getName().equals("Agile")) {
      allMethods =
          methodRepository.findAllMethodsByMethodologyId(
              predictibilityAnalysis.getMethodology().getMethodologyId());
    } else {
      allMethods = methodRepository.findAll();
    }
    List<MethodModel> allMethodsModel = new ArrayList<>();

    Integer countVotes = 0;
    for (Method method : allMethods) {
      MethodModel methodModel = new MethodModel();
      methodModel.setVotes(0);
      methodModel.setName(method.getName());
      for (ExecutionPlanVote vote : votesList) {
        if (methodModel.getName().equals(vote.getMethod().getName())) {
          methodModel.setVotes(methodModel.getVotes() + 1);
          countVotes++;
        }
      }
      allMethodsModel.add(methodModel);
    }

    int maxVoteValue =
        allMethodsModel.stream()
            .map(MethodModel::getVotes)
            .mapToInt(v -> v)
            .max()
            .orElseThrow(() -> new RuntimeException("Pas de valeur max trouvée pour les votes."));

    List<String> chooseMethod =
        allMethodsModel.stream()
            .filter(m -> m.getVotes() == maxVoteValue)
            .map(MethodModel::getName)
            .collect(Collectors.toList());

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

    ExecutionPlanScoreResponseModel result = new ExecutionPlanScoreResponseModel();
    result.setExecutionPlanId(executionPlan.getExecutionPlanId());
    result.setAllMethods(allMethodsModel);
    result.setCountVotes(countVotes);
    result.setExecutionPlanIteration(executionPlan.getExecutionIteration());
    result.setChooseMethod(chooseMethod);
    result.setPredictibilityResults(predictibilityResults);
    result.setIsFinished(executionPlan.getIsFinished());
    return result;
  }
  /**
   * this method is used to get the execution plan score.
   *
   * @param executionPlanId the id of the execution plan
   * @return response entity
   * @throws UserNotAuthorizedException the user not authorized exception if the user is not part of the project
   * @throws ExecutionPlanNotFoundException the execution plan not found exception if the execution plan is not found
   * @throws ExecutionPlanException the execution plan exception if the execution plan is already finished
   */
  @Override
  public ResponseEntity<Boolean> putIsFinishedExecutionPlan(final Long executionPlanId)
    throws UserNotAuthorizedException, ExecutionPlanNotFoundException, ExecutionPlanException {
    ExecutionPlan executionPlan =
        executionPlanRepository
            .findById(executionPlanId)
            .orElseThrow(
                () -> new ExecutionPlanNotFoundException("Ce plan d'exécution n'existe pas."));
    Project project = executionPlan.getPredictibilityAnalysis().getMultiCriteriaAnalysis().getProject();
    if (checkPermissions(project.getProjectId(), ProjectPermession.CDP.name())) {
      if (Boolean.FALSE.equals(executionPlan.getIsFinished())) {
        executionPlan.setIsFinished(true);
        executionPlanRepository.save(executionPlan);
      } else {
        throw new ExecutionPlanException("Le plan d'exécution est déjà terminé.");
      }
    } else {
      throw new UserNotAuthorizedException(
          "Vous n'êtes pas autorisé à changer le statut de ce plan d'exécution.");
    }
    return ResponseEntity.ok().body(true);
  }
  /**
   * this method is used to get the execution plan score.
   *
   * @param projectId the id of the project
   * @param permissionName the name of the permission to check
   * @return boolean true if the user has the permission
   */
  private boolean checkPermissions(final Long projectId,
                                   final String permissionName) {
    String userEmail =
        (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user = userRepository.findByUserEmail(userEmail);

    ProjectUserPermission projectUserPermission =
        projectUserPermissionRepository.findByProjectIdUserID(projectId, user.getUserId());
    return Objects.nonNull(projectUserPermission)
        && permissionName.equals(projectUserPermission.getPermission());
  }
}
