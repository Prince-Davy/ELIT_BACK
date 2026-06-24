package com.elit.outildedecision.collaboration.survey.service;

import com.elit.outildedecision.collaboration.survey.dto.SurveyQuestionRequest;
import com.elit.outildedecision.collaboration.survey.dto.SurveyQuestionResponse;
import com.elit.outildedecision.collaboration.survey.entity.SurveyQuestion;
import com.elit.outildedecision.collaboration.survey.exception.QuestionFormatNotValidException;
import com.elit.outildedecision.collaboration.survey.mapper.SurveyMapper;
import com.elit.outildedecision.collaboration.survey.repository.SurveyQuestionRepository;
import com.elit.outildedecision.collaboration.survey.service.implementation.SurveyQuestionServiceImpl;
import com.elit.outildedecision.shared.exception.QuestionsNotFoundException;
import com.elit.outildedecision.shared.util.ModelSamples;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SurveyQuestionServiceImplTest {
  @Mock
  SurveyQuestionRepository questionRepository;
  @Mock
  SurveyMapper surveyMapper;
  @InjectMocks
  SurveyQuestionServiceImpl surveyQuestionService;

  SurveyQuestion question;
  SurveyQuestionRequest questionModel;

  Long questionId;

  @BeforeEach
  void setup() {
    question = ModelSamples.SurveyQuestionSamples.getSurveyQuestion2();
    questionModel = ModelSamples.SurveyQuestionSamples.getSurveyQuestionRequestModel();
    questionId = 1L;
  }

  @Test
  void shouldCreateQuestion_Success() throws QuestionFormatNotValidException {
    when(questionRepository.save(question)).thenReturn(question);
    Assertions.assertEquals(question, surveyQuestionService.createQuestion(questionModel));
  }

  @Test
  void shouldCreateQuestion_ThrowQuestionFormatNotValidException() {
    QuestionFormatNotValidException p = Assertions.assertThrows(QuestionFormatNotValidException.class, ()-> surveyQuestionService.createQuestion(null));
    Assertions.assertEquals("question format not valid", p.getMessage());
  }

  @Test
  void shouldModifyQuestion_ThrowQuestionFormatNotValidException() {
    QuestionFormatNotValidException p = Assertions.assertThrows(QuestionFormatNotValidException.class, ()-> surveyQuestionService.modifyQuestion(questionId,null));
    Assertions.assertEquals("le format de la question n'est pas valide", p.getMessage());
  }

  @Test
  void shouldModifyQuestion_ThrowQuestionNotFound() {
    when(questionRepository.existsById(questionId)).thenReturn(false);
    QuestionsNotFoundException p = Assertions.assertThrows(QuestionsNotFoundException.class, ()-> surveyQuestionService.modifyQuestion(questionId, questionModel));
    Assertions.assertEquals("la question que vous voulez modifier n'existe pas", p.getMessage());
  }

  @Test
  void shouldModifyQuestion_Success() throws QuestionFormatNotValidException, QuestionsNotFoundException {
    when(questionRepository.existsById(questionId)).thenReturn(true);
    when(questionRepository.getById(questionId)).thenReturn(question);
    when(questionRepository.save(question)).thenReturn(question);
    Assertions.assertEquals(question, surveyQuestionService.modifyQuestion(questionId,questionModel));
  }

  @Test
  void shouldDeleteQuestion_Success() throws QuestionsNotFoundException {
    question.setId(questionId);
    Optional<SurveyQuestion> toBeDeleted = Optional.of(question);
    when(questionRepository.findById(questionId)).thenReturn(toBeDeleted);

    doNothing().when(questionRepository).deleteById(questionId);

    Optional<SurveyQuestion> deletedQuestion = surveyQuestionService.deleteQuestion(questionId);
    Assertions.assertEquals(toBeDeleted, deletedQuestion);
  }

  @Test
   void shouldDeleteQuestion_QuestionNotFound() {
    Optional<SurveyQuestion> toBeDeleted = Optional.empty();
    when(questionRepository.findById(questionId)).thenReturn(toBeDeleted);
    Assertions.assertThrows(QuestionsNotFoundException.class, () -> {
      surveyQuestionService.deleteQuestion(questionId);
    });
  }


  @Test
  void shouldSelectQuestion_Success() throws QuestionsNotFoundException {
    when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
    Assertions.assertEquals(question, surveyQuestionService.selectQuestion(questionId));
  }

  @Test
  void shouldSelectQuestion_ThrowException() {
    when(questionRepository.findById(questionId)).thenReturn(Optional.empty());
    Assertions.assertThrows(QuestionsNotFoundException.class, () -> {
      surveyQuestionService.selectQuestion(questionId);
    });
  }

  @Test
  void shouldFindAllQuestions() {
    List<SurveyQuestion> questionList = Arrays.asList(question);
    SurveyQuestionResponse response = new SurveyQuestionResponse(question.getId(), question.getQuestion(), question.getType());
    when(questionRepository.findAll()).thenReturn(questionList);
    when(surveyMapper.toQuestionResponse(question)).thenReturn(response);
    Assertions.assertEquals(Arrays.asList(response), surveyQuestionService.getQuestionsList());
  }
}
