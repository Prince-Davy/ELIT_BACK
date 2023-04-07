
package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.Criteria;
import com.elit.outildedecision.entity.FormQuestion;
import com.elit.outildedecision.entity.FormStep;
import com.elit.outildedecision.exception.QuestionsNotFoundException;
import com.elit.outildedecision.repository.FormQuestionRepository;
import com.elit.outildedecision.repository.FormStepRepo;
import com.elit.outildedecision.service.CriteriaService;
import com.elit.outildedecision.service.FormQuestionService;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * this class is the implementation of the FormQuestionService interface.
 * it's used to implement the business logic of the FormQuestion entity
 */
@Service
public class FormQuestionServiceImpl implements FormQuestionService {
  /**
   * CriteriaService is the service of the Criteria entity.
   */
  private final CriteriaService criteriaService;
  /**
   * FormQuestionRepository is the repository of the FormQuestion entity.
   */
  private final FormQuestionRepository formQuestionRepository;
  /**
   * FormStepRepo is the repository of the FormStep entity.
   * is the repository of the FormStep entity.
   */
  private final FormStepRepo formStepRepo;

  /**
   * FormQuestionServiceImpl is the constructor of the class.
   * it's used to inject the CriteriaService, the FormQuestionRepository and the FormStepRepo
   * @param criteriaService is the service of the Criteria entity
   * @param formQuestionRepository is the repository of the FormQuestion entity
   * @param formStepRepo is the repository of the FormStep entity
   */
  public FormQuestionServiceImpl(
      final CriteriaService criteriaService,
      final FormQuestionRepository formQuestionRepository,
      final FormStepRepo formStepRepo) {
    this.criteriaService = criteriaService;
    this.formQuestionRepository = formQuestionRepository;
    this.formStepRepo = formStepRepo;
  }

  /**
   * createFormQuestions is used to create a set of questions and their criterias
   * for the given step of the multi-criteria analysis.
   * @param formQuestions is the set of formQuestions to create
   * @param criterias is the set of criterias to create
   * @param stepId is the id of the formStep to which the formQuestions are linked
   * @return the set of created formQuestions
   */
  @Override
  public Set<FormQuestion> createFormQuestions(final Set<FormQuestion> formQuestions,
                                               final Set<Criteria> criterias,
                                               final Long stepId) {
    FormStep formStep = formStepRepo.getById(stepId);
    Set<FormQuestion> persistedFormQuestions = new HashSet<>();

    for (FormQuestion question : formQuestions) {
      FormQuestion questionToPersist =
          FormQuestion.builder()
              .questionText(question.getQuestionText())
              .answerText("")
              .formStep(formStep)
              .build();
      FormQuestion formQuestion = formQuestionRepository.save(questionToPersist);
      criteriaService.createCriterias(criterias, formQuestion);
      persistedFormQuestions.add(formQuestion);
    }
    return persistedFormQuestions;
  }
  /**
   * updateFormQuestion is used to update a formQuestion.
   * using the data entered by the user in the front-end.
   * this method is used to ensure that the data entered by the user won't be lost
   *
   * @param formQuestion is the formQuestion is the data entered by the user
   * @return the updated formQuestion
   * @throws Exception if the formQuestion doesn't exist
   */
  @Override
  public FormQuestion updateFormQuestion(final FormQuestion formQuestion) throws QuestionsNotFoundException {
    Optional<FormQuestion> formQuestionFromDB =
        formQuestionRepository.findById(formQuestion.getQuestionId());
    if (!formQuestionFromDB.isPresent()) {
      throw new QuestionsNotFoundException("Question n'existe pas");
    }
    FormQuestion existingFormQuestion = formQuestionFromDB.get();
    if (Objects.nonNull(formQuestion.getAnswerText())
        && !"".equalsIgnoreCase(formQuestion.getAnswerText())) {
      existingFormQuestion.setAnswerText(formQuestion.getAnswerText());
    }
    for (Criteria criteria : formQuestion.getCriterias()) {
      criteriaService.updateCriteria(criteria);
    }
    return formQuestionRepository.save(existingFormQuestion);
  }
}
