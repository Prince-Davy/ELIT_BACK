package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.Criteria;
import com.elit.outildedecision.entity.FormQuestion;
import com.elit.outildedecision.entity.FormStep;
import com.elit.outildedecision.exception.QuestionsNotFoundException;
import com.elit.outildedecision.repository.FormQuestionRepository;
import com.elit.outildedecision.repository.FormStepRepo;
import com.elit.outildedecision.service.implementation.FormQuestionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FormQuestionServiceImplTests {

  @Mock
  FormQuestionRepository formQuestionRepository;

  @Mock
  FormStepRepo formStepRepo;
  @Mock
  CriteriaService criteriaService;

  @InjectMocks
  FormQuestionServiceImpl formQuestionService;

  Long stepId;
  Set<Criteria> criterias = new HashSet<>();
  Criteria criteriaOne,criteriaTwo;
  FormQuestion formQuestion;
  FormStep formStep;
  Set<FormQuestion> formQuestions = new HashSet<>();

  @BeforeEach
  void setUp(){
    stepId = 1L;
    formQuestion = new FormQuestion();
    formQuestion.setQuestionId(1L);
    criteriaOne = new Criteria();
    criteriaOne.setCriteriaId(1L);
    criteriaOne.setCriteriaName("criteriaOne");
    criteriaOne.setFormQuestion(formQuestion);
    criteriaOne.setCriteriaValue(0);

    criteriaTwo = new Criteria();
    criteriaTwo.setCriteriaId(1L);
    criteriaTwo.setCriteriaName("criteriaTwo");
    criteriaTwo.setFormQuestion(formQuestion);
    criteriaTwo.setCriteriaValue(0);

    criterias.add(criteriaOne);
    criterias.add(criteriaTwo);

    formStep = new FormStep();
    formStep.setStepId(1L);

    formQuestions.add(formQuestion);
    formStep.setFormQuestions(formQuestions);
  }

  @Test
  void createFormQuestions_Success() {
    Set<FormQuestion> persistedFormQuestions = new HashSet<>();
    FormQuestion questionToPersist =
      FormQuestion.builder()
        .questionText(formQuestion.getQuestionText())
        .answerText("")
        .formStep(formStep)
        .build();
    persistedFormQuestions.add(formQuestion);
    when(formQuestionRepository.save(questionToPersist)).thenReturn(formQuestion);
    assertEquals(persistedFormQuestions,formQuestionService.createFormQuestions(formQuestions,criterias,stepId));
  }

  @Test
  void updateFormQuestion_QuestionsNotFoundException() {
    when(formQuestionRepository.findById(anyLong())).thenReturn(Optional.empty());
    assertThrows(QuestionsNotFoundException.class,() -> formQuestionService.updateFormQuestion(formQuestion));
  }

  @Test
  void updateFormQuestion_Success() throws  QuestionsNotFoundException {
    when(formQuestionRepository.findById(anyLong())).thenReturn(Optional.of(formQuestion));
    formQuestion.setCriterias(criterias);
    when(formQuestionRepository.save(formQuestion)).thenReturn(formQuestion);
    assertEquals(formQuestion,formQuestionService.updateFormQuestion(formQuestion));
  }

  @Test
  void updateFormQuestion_SuccessTwo() throws  QuestionsNotFoundException {
    formQuestion.setAnswerText("answer");
    when(formQuestionRepository.findById(anyLong())).thenReturn(Optional.of(formQuestion));
    formQuestion.setCriterias(criterias);
    when(formQuestionRepository.save(formQuestion)).thenReturn(formQuestion);
    assertEquals(formQuestion,formQuestionService.updateFormQuestion(formQuestion));
  }

  @Test
  void updateFormQuestion_SuccessThree() throws  QuestionsNotFoundException {
    formQuestion.setAnswerText("");
    when(formQuestionRepository.findById(anyLong())).thenReturn(Optional.of(formQuestion));
    formQuestion.setCriterias(criterias);
    when(formQuestionRepository.save(formQuestion)).thenReturn(formQuestion);
    assertEquals(formQuestion,formQuestionService.updateFormQuestion(formQuestion));
  }
}
