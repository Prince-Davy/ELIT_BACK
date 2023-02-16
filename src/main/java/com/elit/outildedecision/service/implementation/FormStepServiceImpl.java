package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.*;
import com.elit.outildedecision.exception.FormStepAlreadyExistsException;
import com.elit.outildedecision.exception.StepFormNotFoundException;
import com.elit.outildedecision.repository.FormStepRepo;
import com.elit.outildedecision.repository.MultiCriteriaAnalysisRepository;
import com.elit.outildedecision.service.FormQuestionService;
import com.elit.outildedecision.service.FormStepService;
import com.elit.outildedecision.service.StepWeightService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class FormStepServiceImpl implements FormStepService {

    private final MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository;
    private final FormStepRepo formStepRepo;
    private final StepWeightService stepWeightService;
    private final FormQuestionService formQuestionService;

    public FormStepServiceImpl(MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository,
            FormStepRepo formStepRepo,
            StepWeightService stepWeightService,
            FormQuestionService formQuestionService) {
        this.multiCriteriaAnalysisRepository = multiCriteriaAnalysisRepository;
        this.formStepRepo = formStepRepo;
        this.stepWeightService = stepWeightService;
        this.formQuestionService = formQuestionService;
    }

    @Override
    public FormStep findStepByNameAndFormId(Long formId, String stepName) throws StepFormNotFoundException {
        FormStep formStepFromDB = formStepRepo.findByFormIdAndStepName(formId, stepName);
        if (Objects.isNull(formStepFromDB)) {
            throw new StepFormNotFoundException("This Step Doesn't Exist !");
        }
        return formStepFromDB;
    }

    @Override
    @Transactional
    public ResponseEntity<String> createFormStep(Long formId,
            String stepName,
            Set<Criteria> criterias,
            Set<FormQuestion> formQuestions) throws FormStepAlreadyExistsException, StepFormNotFoundException {
        MultiCriteriaAnalysis form = multiCriteriaAnalysisRepository.findById(formId)
                .orElseThrow(() -> new StepFormNotFoundException("This Form Doesn't Exist !"));
        if (Objects.nonNull(formStepRepo.findByFormIdAndStepName(formId, stepName))) {
            throw new FormStepAlreadyExistsException("This Step Already Exists");
        }
        FormStep formStep = FormStep.builder()
                .multiCriteriaAnalysis(form)
                .stepName(stepName)
                .build();
        Long formStepToPersistId = formStepRepo.save(formStep)
                .getStepId();
        stepWeightService.createWeights(criterias, formStepToPersistId);
        formQuestionService.createFormQuestions(formQuestions, criterias, formStepToPersistId);

        return ResponseEntity
                .ok("FormStep Created Successfully");
    }

    @Override
    public ResponseEntity updateFormStep(FormStep formStep) throws Exception {
        Optional<FormStep> formStepFromDB = formStepRepo.findById(formStep.getStepId());
        if (!formStepFromDB.isPresent()) {
            throw new Exception("Cette étape n'existe pas");
        }
        for (FormQuestion question :formStep.getFormQuestions()) {
            formQuestionService.updateFormQuestion(question);
        }
        for (StepWeight stepWeight : formStep.getStepWeights()) {
            stepWeightService.updateWeights(stepWeight);
        }
        return ResponseEntity.status(HttpStatus.OK).body("Vos Modifications ont été enregistrées !");
    }
}
