
package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.SurveyAnswer;
import com.elit.outildedecision.entity.SurveyQuestion;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.QuestionsNotFoundException;
import com.elit.outildedecision.exception.ResponseNotFoundException;
import com.elit.outildedecision.exception.ResponseNotValidException;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.model.SurveyAnswerRequestModel;
import com.elit.outildedecision.model.SurveyAnswerResponseModel;
import com.elit.outildedecision.repository.SurveyAnswerRepository;
import com.elit.outildedecision.repository.SurveyQuestionRepository;
import com.elit.outildedecision.repository.UserRepository;
import com.elit.outildedecision.service.SurveyAnswerService;
import com.elit.outildedecision.util.AnswerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Service implementation for survey answer operations.
 * it is used to save the answers of the user to the questions
 */
@Service("SurveyAnswerServiceImpl")
public class SurveyAnswerServiceImpl implements SurveyAnswerService {

  /**
   * Repository for accessing survey answer data.
   */
  private final SurveyAnswerRepository answerRepo;

  /**
   * Repository for accessing survey question data.
   */
  private final SurveyQuestionRepository questionRepository;

  /**
   * Repository for accessing user data.
   */
  private final UserRepository userRepository;

  /**
   * Constructor for creating a new SurveyAnswerServiceImpl instance.
   *
   * @param answerRepo         The survey answer repository to use.
   * @param questionRepository The survey question repository to use.
   * @param userRepository     The user repository to use.
   */
  public SurveyAnswerServiceImpl(
    final SurveyAnswerRepository answerRepo,
    final SurveyQuestionRepository questionRepository,
    final UserRepository userRepository) {
    this.answerRepo = answerRepo;
    this.questionRepository = questionRepository;
    this.userRepository = userRepository;
  }

  /**
   * Saves the specified user answers to the database.
   *
   * @param userAnswerList The list of user answers to save.
   * @return A response entity with a message indicating that the responses have been saved.
   * @throws UserNotFoundException      If the specified user does not exist.
   * @throws QuestionsNotFoundException If the specified questions do not exist.
   * @throws ResponseNotFoundException  If a required answer is missing.
   * @throws ResponseNotValidException  If an answer is invalid.
   */
  @Override
  @Transactional
  public ResponseEntity<String> replyToQuestion(final List<SurveyAnswerRequestModel> userAnswerList)
    throws UserNotFoundException, QuestionsNotFoundException, ResponseNotFoundException,
    ResponseNotValidException {

    for (SurveyAnswerRequestModel userAnswer : userAnswerList) {
      SurveyAnswer response = new SurveyAnswer();
      User user =
        userRepository
          .findById(userAnswer.getUserId())
          .orElseThrow(() -> new UserNotFoundException("Cet utilisateur n'existe pas"));
      SurveyQuestion question =
        questionRepository
          .findById(userAnswer.getQuestionId())
          .orElseThrow(() -> new QuestionsNotFoundException("questionnaire non existant"));
      response.setUser(user);
      response.setQuestion(question);

      AnswerType answerType = userAnswer.getAnswerType();
      switch (answerType) {
        case SELECTED_ANSWER:
          if (isAnswerValidated(userAnswer.getAnswerValue())) {
            response.setType(AnswerType.SELECTED_ANSWER);
            response.setAnswerValue(userAnswer.getAnswerValue());
            break;
          }
          throw new ResponseNotValidException(
            "la réponse doit être un entier compris entre 1 et 5");
        case FREE_ANSWER:
          response.setType(AnswerType.FREE_ANSWER);
          response.setAnswerValue(userAnswer.getAnswerValue());
          break;
        default:
          throw new ResponseNotFoundException("reponse manquante");
      }
      answerRepo.save(response);
    }
    return ResponseEntity.ok().body("reponses enregistrées avec succès");
  }

  /**
   * Validates whether the specified answer value is valid for a selected answer.
   *
   * @param answerValue The answer value to validate.
   * @return True if the answer value is valid, false otherwise.
   * @throws ResponseNotValidException If the answer value is not valid.
   */
  private boolean isAnswerValidated(final String answerValue) throws ResponseNotValidException {
    try {
      int value = Integer.parseInt(answerValue);
      return value >= 1 && value <= 5;
    } catch (Exception e) {
      throw new ResponseNotValidException("la réponse doit être un entier compris entre 1 et 5");
    }
  }

  /**
   * Retrieves the list of user answers for the specified user.
   *
   * @param userId The ID of the user to retrieve answers for.
   * @return A list of SurveyAnswerResponseModel objects representing the user's answers.
   * @throws UserNotFoundException If the specified user does not exist.
   */
  @Override
  public List<SurveyAnswerResponseModel> getUserAnswer(final Long userId)
    throws UserNotFoundException {

    List<SurveyAnswerResponseModel> userAnswerList = new ArrayList<>();

    if (!userRepository.existsById(userId)) {
      throw new UserNotFoundException("Utilisateur non identifiable");
    }

    List<SurveyAnswer> answers =
      answerRepo.findAll().stream()
        .filter(e -> e.getUser().getUserId().equals(userId))
        .collect(Collectors.toList());

    for (SurveyAnswer answer : answers) {
      SurveyAnswerResponseModel surveyAnswerResponseModel = new SurveyAnswerResponseModel();
      surveyAnswerResponseModel.setUserId(userId);
      surveyAnswerResponseModel.setQuestionId(answer.getQuestion().getId());
      surveyAnswerResponseModel.setAnswer(answer.getAnswerValue());
      userAnswerList.add(surveyAnswerResponseModel);
    }
    return userAnswerList;
  }

  /**
   * Retrieves a page of survey answers for reporting purposes.
   *
   * @param pageNumber The page number to retrieve.
   * @return A list of SurveyAnswerResponseModel objects representing the survey answers.
   */
  @Override
  public List<SurveyAnswerResponseModel> getSurveyReport(final int pageNumber) {
    Page<SurveyAnswer> surveyAnswers =
      answerRepo.findAll(PageRequest.of(pageNumber - 1, 10));
    List<SurveyAnswerResponseModel> surveyAnswerResponseModelList = new ArrayList<>();
    List<SurveyAnswer> surveyAnswersList = surveyAnswers.getContent();
    for (SurveyAnswer surveyAnswer : surveyAnswersList) {
      SurveyAnswerResponseModel surveyAnswerResponseModel = new SurveyAnswerResponseModel();
      surveyAnswerResponseModel.setQuestionId(
        surveyAnswer.getQuestion().getId());
      surveyAnswerResponseModel.setUserId(
        surveyAnswer.getUser().getUserId());
      surveyAnswerResponseModel.setAnswer(surveyAnswer.getAnswerValue());
      surveyAnswerResponseModelList.add(
        surveyAnswerResponseModel);
    }
    return surveyAnswerResponseModelList;
  }
}
