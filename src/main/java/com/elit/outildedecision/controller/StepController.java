package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.Criteria;
import com.elit.outildedecision.entity.FormStep;
import com.elit.outildedecision.exception.FormStepAlreadyExistsException;
import com.elit.outildedecision.exception.StepFormNotFoundException;
import com.elit.outildedecision.service.FormStepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/steps")
public class StepController {
    @Autowired
    FormStepService formStepService;

    @GetMapping("/{formId}/{stepName}")
    public FormStep getFormStep(
            @PathVariable Long formId,
            @PathVariable String stepName
    ) throws StepFormNotFoundException {
      return formStepService.findStepByNameAndFormId(formId, stepName);
    }

    @PostMapping(consumes = {"application/json"})
    public ResponseEntity<String> createFormStep(
            @RequestBody FormStep formStep
            ) throws FormStepAlreadyExistsException, StepFormNotFoundException {
        Set<Criteria> criterias = new HashSet<>();
        criterias = formStep.getFormQuestions().stream().findFirst().orElse(null).getCriterias();
        return formStepService.createFormStep(
                formStep.getMultiCriteriaAnalysis().getMultiCriteriaAnalysisId(),
                formStep.getStepName(),
                criterias,
                formStep.getFormQuestions()
        );
    }

    @PutMapping
    public ResponseEntity updateFormStep(@Valid @RequestBody FormStep step) throws Exception {
        return formStepService.updateFormStep(step);
    }

}
