package com.elit.outildedecision.collaboration.survey.service;

import com.elit.outildedecision.collaboration.survey.dto.SurveyAnswerRequest;
import com.elit.outildedecision.collaboration.survey.dto.SurveyAnswerResponse;
import com.elit.outildedecision.collaboration.survey.entity.SurveyAnswer;
import com.elit.outildedecision.collaboration.survey.entity.SurveyQuestion;
import com.elit.outildedecision.collaboration.survey.exception.ResponseNotValidException;
import com.elit.outildedecision.collaboration.survey.mapper.SurveyMapper;
import com.elit.outildedecision.collaboration.survey.repository.SurveyAnswerRepository;
import com.elit.outildedecision.collaboration.survey.repository.SurveyQuestionRepository;
import com.elit.outildedecision.collaboration.survey.service.implementation.SurveyAnswerServiceImpl;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.exception.QuestionsNotFoundException;
import com.elit.outildedecision.shared.exception.ResponseNotFoundException;
import com.elit.outildedecision.shared.user.exception.UserNotFoundException;
import com.elit.outildedecision.shared.user.repository.UserRepository;
import com.elit.outildedecision.shared.util.AnswerType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.elit.outildedecision.shared.util.AnswerType.SELECTED_ANSWER;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SurveyAnswerServiceImplTest {
  @InjectMocks
  private SurveyAnswerServiceImpl surveyAnswerServiceImpl;
  @Mock
  SurveyAnswerRepository answerRepository;
  @Mock
  SurveyQuestionRepository questionRepository;
  @Mock
  UserRepository userRepository;
  @Mock
  SurveyMapper surveyMapper;

  private SurveyAnswerRequest userAnswer;

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

    userAnswer = new SurveyAnswerRequest(1L, 1L, answerType, "1");
  }
  @Test
  void replyToQuestion() throws UserNotFoundException, ResponseNotFoundException, QuestionsNotFoundException, ResponseNotValidException {
    List<SurveyAnswerRequest> userAnswerList = new ArrayList<>();
    userAnswerList.add(userAnswer);
    when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
    when( questionRepository.findById(userAnswer.questionId())).thenReturn(Optional.of(question));
    Assertions.assertEquals("reponses enregistrées avec succès", surveyAnswerServiceImpl.replyToQuestion(userAnswerList));
  }
  @Test
  void replyToQuestion_whenUserNotFound() {
    List<SurveyAnswerRequest> userAnswerList = new ArrayList<>();
    userAnswerList.add(userAnswer);
    when(userRepository.findById(user.getUserId())).thenReturn(Optional.empty());
    UserNotFoundException e = Assertions.assertThrows(UserNotFoundException.class, () -> surveyAnswerServiceImpl.replyToQuestion(userAnswerList));
    Assertions.assertEquals("Cet utilisateur n'existe pas", e.getMessage());
  }
  @Test
  void replyToQuestion_whenQuestionsNotFound() {
    List<SurveyAnswerRequest> userAnswerList = new ArrayList<>();
    userAnswerList.add(userAnswer);
    when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
    when( questionRepository.findById(userAnswer.questionId())).thenReturn(Optional.empty());
    QuestionsNotFoundException e = Assertions.assertThrows(QuestionsNotFoundException.class, () -> surveyAnswerServiceImpl.replyToQuestion(userAnswerList));
    Assertions.assertEquals("questionnaire non existant", e.getMessage());
  }

  @Test
  void replyToQuestion_whenResponseNotValid() {
    List<SurveyAnswerRequest> userAnswerList = new ArrayList<>();
    userAnswerList.add(new SurveyAnswerRequest(1L, 1L, SELECTED_ANSWER, "6"));
    when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
    when( questionRepository.findById(1L)).thenReturn(Optional.of(question));
    ResponseNotValidException e = Assertions.assertThrows(ResponseNotValidException.class, () -> surveyAnswerServiceImpl.replyToQuestion(userAnswerList));
    Assertions.assertEquals("la réponse doit être un entier compris entre 1 et 5", e.getMessage());
  }


  @Test
  void getUserAnswer() throws UserNotFoundException {
    List<SurveyAnswerResponse> userAnswerList = new ArrayList<>();
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
    List<SurveyAnswerResponse> userAnswerList = new ArrayList<>();
    Page<SurveyAnswer> surveyAnswers = Page.empty();
    int pageNumber = 2;
    when(answerRepository.findAll(PageRequest.of(pageNumber - 1, 10))).thenReturn(surveyAnswers);
    Assertions.assertEquals(userAnswerList, surveyAnswerServiceImpl.getSurveyReport(pageNumber));
  }
}
