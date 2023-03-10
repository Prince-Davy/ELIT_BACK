package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.Criteria;
import com.elit.outildedecision.entity.FormQuestion;
import com.elit.outildedecision.entity.FormStep;
import com.elit.outildedecision.entity.MultiCriteriaAnalysis;
import com.elit.outildedecision.entity.StepWeight;
import com.elit.outildedecision.exception.FormStepAlreadyExistsException;
import com.elit.outildedecision.exception.QuestionsNotFoundException;
import com.elit.outildedecision.exception.StepFormNotFoundException;
import com.elit.outildedecision.exception.StepWeightNotFoundException;
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

/**
 * this class is the implementation of the FormStepService interface.
 * it's used to implement the business logic of the FormStep entity
 * and it contains the methods to create a FormStep, to find a FormStep by its name and its formId
 * and a method to update the data if a formStep already exists
 */
@Service
public class FormStepServiceImpl implements FormStepService {

  /**
   * Repository for accessing MultiCriteriaAnalysis objects in the database.
   */
  private final MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository;

  /**
   * Repository for accessing FormStep objects in the database.
   */
  private final FormStepRepo formStepRepo;

  /**
   * Service for managing weights associated with FormStep objects.
   */
  private final StepWeightService stepWeightService;

  /**
   * Service for managing questions associated with FormStep objects.
   */
  private final FormQuestionService formQuestionService;

  /**
   * Constructs a new instance of {@code FormStepServiceImpl} with the given dependencies.
   *
   * @param multiCriteriaAnalysisRepository the repository for multi-criteria analysis
   * @param formStepRepo                    the repository for form steps
   * @param stepWeightService               the service for step weights
   * @param formQuestionService             the service for form questions
   */
  public FormStepServiceImpl(
    final MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository,
    final FormStepRepo formStepRepo,
    final StepWeightService stepWeightService,
    final FormQuestionService formQuestionService) {
    this.multiCriteriaAnalysisRepository = multiCriteriaAnalysisRepository;
    this.formStepRepo = formStepRepo;
    this.stepWeightService = stepWeightService;
    this.formQuestionService = formQuestionService;
  }

  /**
   * Find a FormStep by its formId and stepName.
   *
   * @param formId   the id of the form.
   * @param stepName the name of the step.
   * @return FormStep the form step found.
   * @throws StepFormNotFoundException if the form step is not found.
   */
  @Override
  public FormStep findStepByNameAndFormId(final Long formId, final String stepName)
    throws StepFormNotFoundException {
    final FormStep formStepFromDB = formStepRepo.findByFormIdAndStepName(formId, stepName);
    if (Objects.isNull(formStepFromDB)) {
      throw new StepFormNotFoundException("This Step Doesn't Exist!");
    }
    return formStepFromDB;
  }

  /**
   * Create a FormStep and persist it in the database.
   * If the formStep already exists, it will update the data of the formStep and persist it.
   *
   * @param formId        the id of the form.
   * @param stepName      the name of the step.
   * @param criterias     the criteria to use for the form step.
   * @param formQuestions the questions to use for the form step.
   * @return ResponseEntity<String> the response entity indicating success or failure.
   * @throws FormStepAlreadyExistsException if the form step already exists.
   * @throws StepFormNotFoundException      if the form step is not found.
   */
  @Override
  @Transactional
  public ResponseEntity<String> createFormStep(final Long formId,
                                               final String stepName,
                                               final Set<Criteria> criterias,
                                               final Set<FormQuestion> formQuestions)
    throws FormStepAlreadyExistsException, StepFormNotFoundException {
    MultiCriteriaAnalysis form =
      multiCriteriaAnalysisRepository
        .findById(formId)
        .orElseThrow(() -> new StepFormNotFoundException("This Form Doesn't Exist !"));
    if (Objects.nonNull(formStepRepo.findByFormIdAndStepName(formId, stepName))) {
      throw new FormStepAlreadyExistsException("This Step Already Exists");
    }
    FormStep formStep = FormStep.builder().multiCriteriaAnalysis(form).stepName(stepName).build();
    Long formStepToPersistId = formStepRepo.save(formStep).getStepId();
    stepWeightService.createWeights(criterias, formStepToPersistId);
    formQuestionService.createFormQuestions(formQuestions, criterias, formStepToPersistId);

    return ResponseEntity.ok("FormStep Created Successfully");
  }

  /**
   * Updates a FormStep in the database.
   *
   * @param formStep the FormStep object to update
   * @return a ResponseEntity object indicating the status of the operation
   * @throws Exception if the FormStep does not exist in the database
   */
  @Override
  public ResponseEntity<String> updateFormStep(final FormStep formStep)
    throws StepFormNotFoundException, QuestionsNotFoundException, StepWeightNotFoundException {
    Optional<FormStep> formStepFromDB = formStepRepo.findById(formStep.getStepId());
    if (!formStepFromDB.isPresent()) {
      throw new StepFormNotFoundException("Cette étape n'existe pas");
    }
    for (FormQuestion question : formStep.getFormQuestions()) {
      formQuestionService.updateFormQuestion(question);
    }
    for (StepWeight stepWeight : formStep.getStepWeights()) {
      stepWeightService.updateWeights(stepWeight);
    }
    return ResponseEntity.status(HttpStatus.OK).body("Vos Modifications ont été enregistrées !");
  }
}
