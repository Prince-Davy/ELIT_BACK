package com.elit.outildedecision.collaboration.survey.service.implementation;

import com.elit.outildedecision.collaboration.survey.dto.SurveyAnswerRequest;
import com.elit.outildedecision.collaboration.survey.dto.SurveyAnswerResponse;
import com.elit.outildedecision.collaboration.survey.entity.SurveyAnswer;
import com.elit.outildedecision.collaboration.survey.entity.SurveyQuestion;
import com.elit.outildedecision.collaboration.survey.exception.ResponseNotValidException;
import com.elit.outildedecision.collaboration.survey.mapper.SurveyMapper;
import com.elit.outildedecision.collaboration.survey.repository.SurveyAnswerRepository;
import com.elit.outildedecision.collaboration.survey.repository.SurveyQuestionRepository;
import com.elit.outildedecision.collaboration.survey.service.SurveyAnswerService;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.exception.QuestionsNotFoundException;
import com.elit.outildedecision.shared.exception.ResponseNotFoundException;
import com.elit.outildedecision.shared.user.exception.UserNotFoundException;
import com.elit.outildedecision.shared.user.repository.UserRepository;
import com.elit.outildedecision.shared.util.AnswerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for survey answer operations.
 * it is used to save the answers of the user to the questions
 */
@Service("SurveyAnswerServiceImpl")
@Slf4j
public class SurveyAnswerServiceImpl implements SurveyAnswerService {

  /**
   * Repository for accessing survey answer data.
   */
  private final SurveyAnswerRepository answerRepository;

  /**
   * Repository for accessing survey question data.
   */
  private final SurveyQuestionRepository questionRepository;

  /**
   * Repository for accessing user data.
   */
  private final UserRepository userRepository;

  /**
   * Mapper between survey entities and their DTO representations.
   */
  private final SurveyMapper surveyMapper;

  /**
   * Constructor for creating a new SurveyAnswerServiceImpl instance.
   *
   * @param answerRepository   The survey answer repository to use.
   * @param questionRepository The survey question repository to use.
   * @param userRepository     The user repository to use.
   * @param surveyMapper       The survey mapper to use.
   */
  public SurveyAnswerServiceImpl(
    final SurveyAnswerRepository answerRepository,
    final SurveyQuestionRepository questionRepository,
    final UserRepository userRepository,
    final SurveyMapper surveyMapper) {
    this.answerRepository = answerRepository;
    this.questionRepository = questionRepository;
    this.userRepository = userRepository;
    this.surveyMapper = surveyMapper;
  }

  /**
   * Saves the specified user answers to the database.
   *
   * @param userAnswerList The list of user answers to save.
   * @return A confirmation message indicating that the responses have been saved.
   * @throws UserNotFoundException      If the specified user does not exist.
   * @throws QuestionsNotFoundException If the specified questions do not exist.
   * @throws ResponseNotFoundException  If a required answer is missing.
   * @throws ResponseNotValidException  If an answer is invalid.
   */
  @Override
  @Transactional
  public String replyToQuestion(final List<SurveyAnswerRequest> userAnswerList)
    throws UserNotFoundException, QuestionsNotFoundException, ResponseNotFoundException,
    ResponseNotValidException {

    for (SurveyAnswerRequest userAnswer : userAnswerList) {
      SurveyAnswer response = new SurveyAnswer();
      User user =
        userRepository
          .findById(userAnswer.userId())
          .orElseThrow(() -> new UserNotFoundException("Cet utilisateur n'existe pas"));
      SurveyQuestion question =
        questionRepository
          .findById(userAnswer.questionId())
          .orElseThrow(() -> new QuestionsNotFoundException("questionnaire non existant"));
      response.setUser(user);
      response.setQuestion(question);

      AnswerType answerType = userAnswer.answerType();
      switch (answerType) {
        case SELECTED_ANSWER:
          if (isAnswerValidated(userAnswer.answerValue())) {
            response.setType(AnswerType.SELECTED_ANSWER);
            response.setAnswerValue(userAnswer.answerValue());
            break;
          }
          throw new ResponseNotValidException(
            "la réponse doit être un entier compris entre 1 et 5");
        case FREE_ANSWER:
          response.setType(AnswerType.FREE_ANSWER);
          response.setAnswerValue(userAnswer.answerValue());
          break;
        default:
          throw new ResponseNotFoundException("reponse manquante");
      }
      answerRepository.save(response);
      log.info("Saved survey answer for userId {} on questionId {}",
        user.getUserId(), question.getId());
    }
    return "reponses enregistrées avec succès";
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
    } catch (NumberFormatException e) {
      log.error("Invalid selected answer value '{}'", answerValue, e);
      throw new ResponseNotValidException("la réponse doit être un entier compris entre 1 et 5");
    }
  }

  /**
   * Retrieves the list of user answers for the specified user.
   *
   * @param userId The ID of the user to retrieve answers for.
   * @return A list of SurveyAnswerResponse objects representing the user's answers.
   * @throws UserNotFoundException If the specified user does not exist.
   */
  @Override
  @Transactional(readOnly = true)
  public List<SurveyAnswerResponse> getUserAnswer(final Long userId)
    throws UserNotFoundException {

    if (!userRepository.existsById(userId)) {
      throw new UserNotFoundException("Utilisateur non identifiable");
    }

    return answerRepository.findAll().stream()
      .filter(answer -> answer.getUser().getUserId().equals(userId))
      .map(surveyMapper::toAnswerResponse)
      .toList();
  }

  /**
   * Retrieves a page of survey answers for reporting purposes.
   *
   * @param pageNumber The page number to retrieve.
   * @return A list of SurveyAnswerResponse objects representing the survey answers.
   */
  @Override
  @Transactional(readOnly = true)
  public List<SurveyAnswerResponse> getSurveyReport(final int pageNumber) {
    Page<SurveyAnswer> surveyAnswers =
      answerRepository.findAll(PageRequest.of(pageNumber - 1, 10));
    return surveyAnswers.getContent().stream()
      .map(surveyMapper::toAnswerResponse)
      .toList();
  }
}
