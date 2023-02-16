package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.Criteria;
import com.elit.outildedecision.entity.FormQuestion;
import com.elit.outildedecision.entity.FormStep;
import com.elit.outildedecision.repository.FormQuestionRepository;
import com.elit.outildedecision.repository.FormStepRepo;
import com.elit.outildedecision.service.CriteriaService;
import com.elit.outildedecision.service.FormQuestionService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FormQuestionServiceImpl implements FormQuestionService {
    private final CriteriaService criteriaService;


    private final FormQuestionRepository formQuestionRepository;

    private final FormStepRepo formStepRepo;

    public FormQuestionServiceImpl(CriteriaService criteriaService,
            FormQuestionRepository formQuestionRepository,
            FormStepRepo formStepRepo) {
        this.criteriaService = criteriaService;
        this.formQuestionRepository = formQuestionRepository;
        this.formStepRepo = formStepRepo;
    }

    @Override
    public Set<FormQuestion> createFormQuestions(Set<FormQuestion> formQuestions, Set<Criteria> criterias, Long stepId) {
        FormStep formStep = formStepRepo.getById(stepId);
        Set<FormQuestion> persistedFormQuestions = new HashSet<>();

        for (FormQuestion question : formQuestions) {
            FormQuestion questionToPersist = FormQuestion.builder()
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

    @Override
    public FormQuestion updateFormQuestion(FormQuestion formQuestion) throws Exception {
        Optional<FormQuestion> formQuestionFromDB = formQuestionRepository.findById(formQuestion.getQuestionId());
        if (!formQuestionFromDB.isPresent()) {
            throw new Exception("Question n'existe pas");
        }
        FormQuestion existingFormQuestion = formQuestionFromDB.get();
        if (Objects.nonNull(formQuestion.getAnswerText()) && !"".equalsIgnoreCase(formQuestion.getAnswerText())) {
            existingFormQuestion.setAnswerText(formQuestion.getAnswerText());
        }
        for (Criteria criteria : formQuestion.getCriterias()) {
            criteriaService.updateCriteria(criteria);
        }
        return formQuestionRepository.save(existingFormQuestion);
    }
}
