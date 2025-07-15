package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.SurveyAnswer;
import com.elit.outildedecision.entity.SurveyQuestion;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.*;
import com.elit.outildedecision.model.SurveyAnswerRequestModel;
import com.elit.outildedecision.model.SurveyAnswerResponseModel;
import com.elit.outildedecision.repository.SurveyAnswerRepository;
import com.elit.outildedecision.repository.SurveyQuestionRepository;
import com.elit.outildedecision.repository.UserRepository;
import com.elit.outildedecision.service.implementation.SurveyAnswerServiceImpl;
import com.elit.outildedecision.enumeration.AnswerType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.elit.outildedecision.enumeration.AnswerType.SELECTED_ANSWER;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SurveyAnswerServiceImplTest {
  @InjectMocks
  private SurveyAnswerServiceImpl surveyAnswerServiceImpl;
  @Mock
  SurveyAnswerRepository answerRepo;
  @Mock
  SurveyQuestionRepository questionRepository;
  @Mock
  UserRepository userRepository;

  private SurveyAnswerRequestModel userAnswer;


  User user;
  SurveyAnswer response;
  AnswerType answerType;
  SurveyQuestion question;

  @BeforeEach
  public void setUp() {
    user = new User();
    user.setUserId(1L);

    question = new SurveyQuestion();
    question.setId(1L);

    answerType = SELECTED_ANSWER;

    response = new SurveyAnswer();

    userAnswer = new SurveyAnswerRequestModel();
    userAnswer.setUserId(1L);
    userAnswer.setQuestionId(1L);
    userAnswer.setAnswerType(answerType);
    userAnswer.setAnswerValue("1");

  }
  @Test
  void replyToQuestion() throws UserNotFoundException, ResponseNotFoundException, QuestionsNotFoundException, ResponseNotValidException {
    List<SurveyAnswerRequestModel> userAnswerList = new ArrayList<>();
    userAnswerList.add(userAnswer);
    when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
    when( questionRepository.findById(userAnswer.getQuestionId())).thenReturn(Optional.of(question));
    Assertions.assertEquals(ResponseEntity.ok().body("reponses enregistrées avec succès"), surveyAnswerServiceImpl.replyToQuestion(userAnswerList));
  }
  @Test
  void replyToQuestion_whenUserNotFound() {
    List<SurveyAnswerRequestModel> userAnswerList = new ArrayList<>();
    userAnswerList.add(userAnswer);
    when(userRepository.findById(user.getUserId())).thenReturn(Optional.empty());
    UserNotFoundException e = Assertions.assertThrows(UserNotFoundException.class, () -> surveyAnswerServiceImpl.replyToQuestion(userAnswerList));
    Assertions.assertEquals("Cet utilisateur n'existe pas", e.getMessage());
  }
  @Test
  void replyToQuestion_whenQuestionsNotFound() {
    List<SurveyAnswerRequestModel> userAnswerList = new ArrayList<>();
    userAnswerList.add(userAnswer);
    when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
    when( questionRepository.findById(userAnswer.getQuestionId())).thenReturn(Optional.empty());
    QuestionsNotFoundException e = Assertions.assertThrows(QuestionsNotFoundException.class, () -> surveyAnswerServiceImpl.replyToQuestion(userAnswerList));
    Assertions.assertEquals("questionnaire non existant", e.getMessage());
  }

  @Test
  void replyToQuestion_whenResponseNotValid() {
    List<SurveyAnswerRequestModel> userAnswerList = new ArrayList<>();
    userAnswerList.add(userAnswer);
    userAnswer.setAnswerValue("6");
    when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
    when( questionRepository.findById(userAnswer.getQuestionId())).thenReturn(Optional.of(question));
    ResponseNotValidException e = Assertions.assertThrows(ResponseNotValidException.class, () -> surveyAnswerServiceImpl.replyToQuestion(userAnswerList));
    Assertions.assertEquals("la réponse doit être un entier compris entre 1 et 5", e.getMessage());
  }


  @Test
  void getUserAnswer() throws UserNotFoundException {
    List<SurveyAnswerResponseModel> userAnswerList = new ArrayList<>();
    when(userRepository.existsById(user.getUserId())).thenReturn(true);
    Assertions.assertEquals(userAnswerList,surveyAnswerServiceImpl.getUserAnswer(user.getUserId()) );
  }
  @Test
  void getUserAnswer_whenUserNotFound() {
    when(userRepository.existsById(user.getUserId())).thenReturn(false);
    UserNotFoundException e = Assertions.assertThrows(UserNotFoundException.class, () -> surveyAnswerServiceImpl.getUserAnswer(user.getUserId()));
    Assertions.assertEquals("Utilisateur non identifiable", e.getMessage());
  }

  @Test
  void getSurveyReport() {
    List<SurveyAnswerResponseModel> userAnswerList = new ArrayList<>();
    Page<SurveyAnswer> surveyAnswers = Page.empty();
    int pageNumber = 2;
    when(answerRepo.findAll(PageRequest.of(pageNumber - 1, 10))).thenReturn(surveyAnswers);
    Assertions.assertEquals(userAnswerList, surveyAnswerServiceImpl.getSurveyReport(pageNumber));
  }
}
