package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.*;
import com.elit.outildedecision.exception.*;
import com.elit.outildedecision.model.BrainstormingModel;
import com.elit.outildedecision.model.CriteriaModel;
import com.elit.outildedecision.model.FormQuestionModel;
import com.elit.outildedecision.model.FormStepModelForBrainstorming;
import com.elit.outildedecision.repository.*;
import com.elit.outildedecision.service.BrainstormingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BrainstormingServiceImpl implements BrainstormingService {

    private final BrainstormingRepository brainstormingRepository;
    private final PredictibilityAnalysisRepository predictibilityAnalysisRepository;

    private final FormQuestionRepository formQuestionRepository;
    private final ProjectUserPermissionRepository projectUserPermissionRepository;

    private final UserRepository userRepository;


    public BrainstormingServiceImpl(BrainstormingRepository brainstormingRepository, FormQuestionRepository formQuestionRepository, UserRepository userRepository, ProjectRepository projectRepository, PredictibilityAnalysisRepository predictibilityAnalysisRepository, MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository, ProjectUserPermissionRepository projectUserPermissionRepository) {
        this.brainstormingRepository = brainstormingRepository;
        this.formQuestionRepository = formQuestionRepository;
        this.userRepository = userRepository;
        this.predictibilityAnalysisRepository = predictibilityAnalysisRepository;
        this.projectUserPermissionRepository = projectUserPermissionRepository;
    }


    @Override
    public ResponseEntity<String> createBrainstorming(BrainstormingModel brainstormingModel) throws Exception {
        String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUserEmail(userEmail);

        PredictibilityAnalysis predictibilityAnalysis = predictibilityAnalysisRepository.findById(brainstormingModel.getPredictibilityAnalysisId()).
                orElseThrow(() -> new PredictibilityAnaliseNotFoundException("Ce analysis n'existe pas"));

        System.out.println("predictibilityAnalysis" + predictibilityAnalysis);

        //TODO  : CHECK ONLY ONE QUESTION BY USER
        Brainstorming savedBrainstorming = brainstormingRepository.findByUserQuestionPredictibilityId(user.getUserId(), brainstormingModel.getQuestionId(), predictibilityAnalysis.getPredictibilityAnalysisId());

        if (!Objects.isNull(savedBrainstorming))
            throw new ExistingBrainstormingException("Cet utilisateur à déjà soumis un commentaire.");

        Brainstorming brainstorming = new Brainstorming();
        brainstorming.setPredictibilityAnalysis(predictibilityAnalysis);
        brainstorming.setBrainstormingText(brainstormingModel.getBrainstormingText());
        brainstorming.setFormQuestion(formQuestionRepository.getById(brainstormingModel.getQuestionId()));
        brainstorming.setIsChecked(false);
        brainstorming.setUser(user);
        brainstorming.setIsChecked(brainstormingModel.getIsChecked());

        brainstormingRepository.save(brainstorming);

        return ResponseEntity
                .ok()
                .body("Le commentaire du brainstorming a bien été enregistré.");
    }

    @Override
    public ResponseEntity<String> updateBrainstorming(BrainstormingModel brainstormingModel) throws Exception {
        Brainstorming brainstorming = brainstormingRepository.findById(brainstormingModel.getBrainstormingId()).
                orElseThrow(() -> new BrainstormingNotFoundException("Cet brainstorming n'existe pas"));

        String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUserEmail(userEmail);

        if (brainstorming.getUser().getUserId() != user.getUserId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("L'utilisateur enregistré ne correspond pas à l'utilisateur du brainstorming");
        }

        brainstorming.setBrainstormingText(brainstormingModel.getBrainstormingText());
        brainstorming.setIsChecked(brainstormingModel.getIsChecked());

        brainstormingRepository.save(brainstorming);

        return ResponseEntity
                .ok()
                .body("Le commentaire du brainstorming a bien été enregistré.");
    }

    @Override
    public ResponseEntity<String> deleteBrainstorming(Long brainstormingId) throws BrainstormingNotFoundException {
        Brainstorming brainstorming = brainstormingRepository.findById(brainstormingId).
                orElseThrow(() -> new BrainstormingNotFoundException("Cet brainstorming n'existe pas"));
        brainstormingRepository.delete(brainstorming);
        return ResponseEntity
                .ok()
                .body("Le commentaire du brainstorming a bien été supprimé.");
    }

    @Override
    public ResponseEntity<String> validateOrRejectBrainstorming(Long brainstormingId, HttpEntity<String> isChecked) throws JsonProcessingException, BrainstormingNotFoundException {
        Brainstorming brainstorming = brainstormingRepository.findById(brainstormingId).
                orElseThrow(() -> new BrainstormingNotFoundException("Cet brainstorming n'existe pas"));

        Map<String, Boolean> result = new ObjectMapper().readValue(isChecked.getBody(), HashMap.class);

        brainstorming.setIsChecked(result.get("isConfirmed"));

        brainstormingRepository.save(brainstorming);
        return ResponseEntity
                .ok()
                .body("Le commentaire a bien été enregistré.");
    }

    @Override
    public ResponseEntity<Object> getBrainstorming(Long predictibilityAnalysisId) throws Exception {

        PredictibilityAnalysis predictibilityAnalysis = predictibilityAnalysisRepository.findById(predictibilityAnalysisId).
                orElseThrow(() -> new PredictibilityAnaliseNotFoundException("Ce analysis n'existe pas"));

        List<FormStep> formStepList = predictibilityAnalysis.getMultiCriteriaAnalysis().getFormSteps().stream().filter(formStep -> Optional.ofNullable(formStep.getEScore()).orElse(0F) < 0.5).collect(Collectors.toList());
        System.out.println("formStepList" + formStepList.size());

        Long projectId = predictibilityAnalysis.getMultiCriteriaAnalysis().getProject().getProjectId();

        List<FormStepModelForBrainstorming> result = new ArrayList<>();
        for (FormStep formStep : formStepList) {
            List<FormQuestionModel> formQuestionList = new ArrayList<>();
            for (FormQuestion formQuestion : formStep.getFormQuestions()) {
                List<CriteriaModel> criteriaList = new ArrayList();
                for (Criteria criteria : formQuestion.getCriterias()) {
                    criteriaList.add(new CriteriaModel(criteria.getCriteriaId(), criteria.getCriteriaName(), criteria.getCriteriaValue(), formQuestion.getQuestionId()));
                }
                List<BrainstormingModel> braistormingList = new ArrayList<>();
                for (Brainstorming brainstorming : formQuestion.getBrainstormings()) {
                    User user = userRepository.getById(brainstorming.getUser().getUserId());
                    ProjectUserPermission userPermission = projectUserPermissionRepository.findByProjectIdUserID(projectId, user.getUserId());

                    if (Objects.isNull(userPermission)) {
                        throw new UserNotFoundException("Cet utilisateur n'existe pas dans le projet !");
                    }
                    braistormingList.add(new BrainstormingModel(brainstorming.getBrainstormingId(),
                            brainstorming.getBrainstormingText(),
                            brainstorming.getIsChecked(),
                            formQuestion.getQuestionId(),
                            brainstorming.getPredictibilityAnalysis().getPredictibilityAnalysisId(),
                            user.getUserId(),
                            user.getUserFirstName(),
                            user.getUserLastName(),
                            userPermission.getPermission()));
                }
                formQuestionList.add(new FormQuestionModel(formQuestion.getQuestionId(), formQuestion.getQuestionText(), formQuestion.getAnswerText(), formQuestion.getQuestionRef(), formStep.getStepId(), criteriaList, braistormingList));
            }

            result.add(new FormStepModelForBrainstorming(formStep.getStepId(), formStep.getStepName(), formStep.getStepRef(), formStep.getEScore(), formStep.getMultiCriteriaAnalysis().getMultiCriteriaAnalysisId(), formQuestionList));
        }

        return ResponseEntity.ok()
                .body(result);

    }
    @Override
    public ResponseEntity<Object> getBrainstormingAbstract(Long predictibilityAnalysisId) throws Exception {

        PredictibilityAnalysis predictibilityAnalysis = predictibilityAnalysisRepository.findById(predictibilityAnalysisId).
                orElseThrow(() -> new PredictibilityAnaliseNotFoundException("Ce analysis n'existe pas"));

        //Get the list of the FormStep with brainstorming
        List<FormStep> formStepList = predictibilityAnalysis.getMultiCriteriaAnalysis().getFormSteps().stream().filter(formStep -> formStep.getFormQuestions().stream().filter(formQuestion -> formQuestion.getBrainstormings().size()>0).count()>0).collect(Collectors.toList());
        System.out.println("ABSTRACT formStepList" + formStepList.size());

        Long projectId = predictibilityAnalysis.getMultiCriteriaAnalysis().getProject().getProjectId();

        List<FormStepModelForBrainstorming> result = new ArrayList<>();
        for (FormStep formStep : formStepList) {
            List<FormQuestionModel> formQuestionList = new ArrayList<>();
            for (FormQuestion formQuestion : formStep.getFormQuestions()) {
                List<CriteriaModel> criteriaList = new ArrayList();
                for (Criteria criteria : formQuestion.getCriterias()) {
                    criteriaList.add(new CriteriaModel(criteria.getCriteriaId(), criteria.getCriteriaName(), criteria.getCriteriaValue(), formQuestion.getQuestionId()));
                }
                List<BrainstormingModel> braistormingList = new ArrayList<>();
                for (Brainstorming brainstorming : formQuestion.getBrainstormings()) {
                    User user = userRepository.getById(brainstorming.getUser().getUserId());
                    String userRole = projectUserPermissionRepository.findByProjectIdUserID(projectId, user.getUserId()).getPermission();
                    braistormingList.add(new BrainstormingModel(brainstorming.getBrainstormingId(),
                            brainstorming.getBrainstormingText(),
                            brainstorming.getIsChecked(),
                            formQuestion.getQuestionId(),
                            brainstorming.getPredictibilityAnalysis().getPredictibilityAnalysisId(),
                            user.getUserId(),
                            user.getUserFirstName(),
                            user.getUserLastName(),
                            userRole));
                }
                formQuestionList.add(new FormQuestionModel(formQuestion.getQuestionId(), formQuestion.getQuestionText(), formQuestion.getAnswerText(), formQuestion.getQuestionRef(), formStep.getStepId(), criteriaList, braistormingList));
            }

            result.add(new FormStepModelForBrainstorming(formStep.getStepId(), formStep.getStepName(), formStep.getStepRef(), formStep.getEScore(), formStep.getMultiCriteriaAnalysis().getMultiCriteriaAnalysisId(), formQuestionList));
        }

        return ResponseEntity.ok()
                .body(result);

    }

}