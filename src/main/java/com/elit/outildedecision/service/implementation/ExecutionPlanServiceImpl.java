package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.*;
import com.elit.outildedecision.entity.Methodology;
import com.elit.outildedecision.exception.ExecutionPlanNotFoundException;
import com.elit.outildedecision.exception.UserNotAuthorizedException;
import com.elit.outildedecision.model.*;
import com.elit.outildedecision.repository.*;
import com.elit.outildedecision.service.ExecutionPlanService;
import com.elit.outildedecision.util.ProjectPermession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExecutionPlanServiceImpl implements ExecutionPlanService {

    @Autowired
    ExecutionPlanAnswerRepository executionPlanAnswerRepository;

    @Autowired
    ExecutionPlanQuestionRepository executionPlanQuestionRepository;

    @Autowired
    ExecutionPlanRepository executionPlanRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProjectUserPermissionRepository projectUserPermissionRepository;

    @Autowired
    MethodRepository methodRepository;

    @Autowired
    ExecutionPlanVoteRepo executionPlanVoteRepo;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    PredictibilityAnalysisRepository predictibilityAnalysisRepository;

    @Autowired
    MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository;

    @Autowired
    PredictibilityResultsRepository predictibilityResultsRepository;

    @Override
    public ResponseEntity<Long> createExecutionPlan(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Ce projet n'existe pas"));

        MultiCriteriaAnalysis lastMultiCriteriaAnalysis = multiCriteriaAnalysisRepository.findLatestMultiCriteriaAnalysisByProjectId(project.getProjectId());
        PredictibilityAnalysis lastPredictibilityAnalysis = predictibilityAnalysisRepository.findByLastIterationAndMultiCriteriaId(lastMultiCriteriaAnalysis.getMultiCriteriaAnalysisId());
        
        /*if (!lastPredictibilityAnalysis.getIsFinished()) return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(-1L);*/

        ExecutionPlan lastExecutionPlan = executionPlanRepository.findByLastIterationAndPredictibilityId(lastPredictibilityAnalysis.getPredictibilityAnalysisId());

        int nextIteration = 1;

        if (lastExecutionPlan != null) {
            //"Une Analyse de prévisibilité est déjà en cours"
            if (!lastExecutionPlan.getIsFinished())
                return ResponseEntity.ok().body(lastExecutionPlan.getExecutionPlanId());
            nextIteration = lastExecutionPlan.getExecutionIteration() + 1;
        }

        ExecutionPlan executionPlan = executionPlanRepository.save(ExecutionPlan.builder()
                .predictibilityAnalysis(lastPredictibilityAnalysis)
                .isFinished(false)
                .executionIteration(nextIteration)
                .build());

        List<ExecutionPlanQuestion> executionPlanQuestions = executionPlanQuestionRepository.findAll();

        for (ExecutionPlanQuestion executionPlanQuestion : executionPlanQuestions) {
            executionPlanAnswerRepository.save(ExecutionPlanAnswer.builder()
                    .answer(null)
                    .executionPlan(executionPlan)
                    .executionPlanQuestion(executionPlanQuestion)
                    .build());
        }

        return ResponseEntity.ok()
                .body(executionPlan.getExecutionPlanId());
    }

    @Override
    public ResponseEntity<Object> getQuestionsAndAnswers(Long executionPlanId) {
        ExecutionPlan executionPlan = executionPlanRepository.findById(executionPlanId)
                .orElseThrow(() -> new RuntimeException("Execution plan introuvable."));

        List<ExecutionPlanFormModel> executionPlanFormModelList = new ArrayList<>();

        for (ExecutionPlanAnswer executionPlanAnswer : executionPlan.getExecutionPlanAnswers()) {
            executionPlanFormModelList.add(new ExecutionPlanFormModel(
                    executionPlanAnswer.getAnswer(),
                    executionPlanAnswer.getExecutionPlanQuestion().getTitle(),
                    executionPlanAnswer.getExecutionPlanQuestion().getQuestion(),
                    executionPlanAnswer.getExecutionPlanQuestion().getExecutionPlanQuestionId()
            ));
        }

        return ResponseEntity.ok()
                .body(executionPlanFormModelList);
    }

    @Override
    public ResponseEntity<Object> updateAnswers(List<ExecutionPlanAnswerModel> executionPlanAnswerModel, Long executionPlanId) {
        List<Object> result = new ArrayList<>();
        for (ExecutionPlanAnswerModel executionPlanAnswerUnit : executionPlanAnswerModel) {
            ExecutionPlanAnswer executionPlanAnswerFromDB = executionPlanAnswerRepository.findByExecutionPlanIdAndExecutionPlanQuestionId(executionPlanId, executionPlanAnswerUnit.getExecutionPlanQuestionId());

            if (Objects.nonNull(executionPlanAnswerFromDB)) {
                executionPlanAnswerFromDB.setAnswer(executionPlanAnswerUnit.getAnswer());
                executionPlanAnswerRepository.save(executionPlanAnswerFromDB);
                result.add("La réponse avec l'id " + executionPlanAnswerFromDB.getExecutionPlanAnswerId() + " a bien été modifié.");
            }
        }
        return ResponseEntity
                .ok()
                .body(result);
    }

    @Override
    public ResponseEntity<Object> getMethodologies(Long executionPlanId) {
        String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUserEmail(userEmail);
        ExecutionPlan executionPlan = executionPlanRepository.findById(executionPlanId)
                .orElseThrow(() -> new RuntimeException("Plan d'exécution introuvable."));
        PredictibilityAnalysis predictibilityAnalysis = executionPlan.getPredictibilityAnalysis();
        Project project = predictibilityAnalysis.getMultiCriteriaAnalysis().getProject();
        System.out.println("project = " + project);
        ProjectUserPermission projectUserPermission = projectUserPermissionRepository.findByProjectIdUserID(project.getProjectId(), user.getUserId());
        
        Methodology methodology = predictibilityAnalysis.getMethodology();
        List<Method> methodList;
        if (methodology.getName().equals("Hybride"))
            methodList = methodRepository.findAllMethods();
        else methodList = methodology.getMethods();

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
                    ? executionPlanVoteRepo.findAllVotesByMethodIdAndExecutionPlanId(method.getId(), executionPlanId)
                    : executionPlanVoteRepo.findVoteByMethodIdUserIdAndExecutionPlanId(method.getId(), user.getUserId(), executionPlanId);
            List<ExecutionVoteModel> votesList = new ArrayList<>();
            for (ExecutionPlanVote vote : votesModelList) {
                ExecutionVoteModel voteModel = new ExecutionVoteModel();
                voteModel.setId(vote.getId());
                voteModel.setUserId(
                        projectUserPermission.getPermission().equals(ProjectPermession.CDP.name())
                        ? vote.getUser().getUserId()
                        : null
                );
                votesList.add(voteModel);
            }
            model.setVotes(votesList);
            result.add(model);
        }

        return ResponseEntity
                .ok()
                .body(result);
    }

    @Override
    public ResponseEntity<String> postMethodologiesVote(ExecutionVoteRequestModel executionVoteRequestModel) {
        System.out.println("=========================))))))" + executionVoteRequestModel.getMethodId2ForHybrid());
        String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUserEmail(userEmail);

        ExecutionPlan executionPlan = executionPlanRepository.findById(executionVoteRequestModel.getExecutionPlanId())
                .orElseThrow(() -> new RuntimeException("Ce plan d'exécution n'existe pas."));
        PredictibilityAnalysis predictibilityAnalysis = executionPlan.getPredictibilityAnalysis();
        Project project = predictibilityAnalysis.getMultiCriteriaAnalysis().getProject();
        ProjectUserPermission projectUserPermission = projectUserPermissionRepository.findByProjectIdUserID(project.getProjectId(), user.getUserId());

        if (projectUserPermission.getPermission().equals(ProjectPermession.CDP.name()) || projectUserPermission.getPermission().equals(ProjectPermession.CONTRIBUTER.toString())) {
            ExecutionPlanVote executionPlanVoteFromDB = executionPlanVoteRepo.findVoteByUserIdAndExecutionPlanId(user.getUserId(), executionVoteRequestModel.getExecutionPlanId());
            if (Objects.nonNull(executionPlanVoteFromDB)) throw new RuntimeException("Cet utilisateur a déjà soumis un vote sur ce plan d'exécution.");
            ExecutionPlanVote executionPlanVote = new ExecutionPlanVote();
            executionPlanVote.setUser(user);
            executionPlanVote.setMethod(methodRepository.getById(executionVoteRequestModel.getMethodId()));
            executionPlanVote.setExecutionPlan(executionPlanRepository.getById(executionVoteRequestModel.getExecutionPlanId()));
            executionPlanVoteRepo.save(executionPlanVote);

            if(executionVoteRequestModel.getMethodId2ForHybrid() != null){
                ExecutionPlanVote executionPlanVote2 = new ExecutionPlanVote();
                executionPlanVote2.setUser(user);
                executionPlanVote2.setMethod(methodRepository.getById(executionVoteRequestModel.getMethodId2ForHybrid()));
                executionPlanVote2.setExecutionPlan(executionPlanRepository.getById(executionVoteRequestModel.getExecutionPlanId()));
                executionPlanVoteRepo.save(executionPlanVote2);
            }
        }

        return ResponseEntity
                .ok()
                .body("Le vote a bien été enregistré.");
    }

    @Override
    public ExecutionPlanScoreResponseModel getExecutionPlanScore(Long executionPlanId) {
        String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUserEmail(userEmail);
        ExecutionPlan executionPlan = executionPlanRepository.findById(executionPlanId)
                .orElseThrow(() -> new RuntimeException("Plan d'exécution introuvable."));
        PredictibilityAnalysis predictibilityAnalysis = executionPlan.getPredictibilityAnalysis();
        Project project = predictibilityAnalysis.getMultiCriteriaAnalysis().getProject();
        ProjectUserPermission projectUserPermission = projectUserPermissionRepository.findByProjectIdUserID(project.getProjectId(), user.getUserId());

        if(Objects.isNull(projectUserPermission)) throw new RuntimeException("L'utilisateur ne fait pas parti du projet.");

        List<ExecutionPlanVote> votesList = executionPlanVoteRepo.findAllVotesByExecutionPlanId(executionPlanId);

        List<Method> allMethods;
        if (predictibilityAnalysis.getMethodology().getName().equals("Classique") || predictibilityAnalysis.getMethodology().getName().equals("Agile"))
            allMethods = methodRepository.findAllMethodsByMethodologyId(predictibilityAnalysis.getMethodology().getMethodologyId());
        else allMethods = methodRepository.findAllMethods();
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

        int maxVoteValue = allMethodsModel.stream()
                .map(MethodModel::getVotes)
                .mapToInt(v->v)
                .max()
                .orElseThrow(() -> new RuntimeException("Pas de valeur max trouvée pour les votes."));
        
        List<String> chooseMethod = allMethodsModel.stream()
                .filter(m->m.getVotes() == maxVoteValue)
                .map(MethodModel::getName)
                .collect(Collectors.toList());

        List<PredictibilityResults> predictibilityResultsList = predictibilityResultsRepository.findAllPredictibilityResultsByPreAnalysisId(predictibilityAnalysis.getPredictibilityAnalysisId());
        List<PredictibilityResultsResponseModel> predictibilityResults = new ArrayList<>();
        for (PredictibilityResults predictibilityResult : predictibilityResultsList) {
            PredictibilityResultsResponseModel predictibilityResultNew = new PredictibilityResultsResponseModel();
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

    @Override
    public ResponseEntity<Boolean> putIsFinishedExecutionPlan(Long executionPlanId) throws UserNotAuthorizedException, ExecutionPlanNotFoundException {
        ExecutionPlan executionPlan = executionPlanRepository.findById(executionPlanId)
                .orElseThrow(() -> new ExecutionPlanNotFoundException("Ce plan d'exécution n'existe pas."));
        Project project = executionPlan.getPredictibilityAnalysis().getMultiCriteriaAnalysis().getProject();
        if(checkPermissions(project.getProjectId(),ProjectPermession.CDP.name())){
            if (Boolean.FALSE.equals(executionPlan.getIsFinished())){
                executionPlan.setIsFinished(true);
                executionPlanRepository.save(executionPlan);
            } else throw new RuntimeException("Le plan d'exécution est déjà terminé.");
        } else
            throw new UserNotAuthorizedException("Vous n'êtes pas autorisé à changer le statut de ce plan d'exécution.");
        return ResponseEntity
                .ok()
                .body(true);
    }

    private boolean checkPermissions(Long projectId,String permissionName  ) {
        String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUserEmail(userEmail);

        ProjectUserPermission projectUserPermission = projectUserPermissionRepository.findByProjectIdUserID(projectId, user.getUserId());
        return (Objects.nonNull(projectUserPermission) && permissionName.equals(projectUserPermission.getPermission()));
    }
}