package com.elit.outildedecision.collaboration.survey.service.implementation;

import com.elit.outildedecision.collaboration.survey.dto.SurveyQuestionRequest;
import com.elit.outildedecision.collaboration.survey.dto.SurveyQuestionResponse;
import com.elit.outildedecision.collaboration.survey.entity.SurveyQuestion;
import com.elit.outildedecision.collaboration.survey.exception.QuestionFormatNotValidException;
import com.elit.outildedecision.collaboration.survey.mapper.SurveyMapper;
import com.elit.outildedecision.collaboration.survey.repository.SurveyQuestionRepository;
import com.elit.outildedecision.collaboration.survey.service.SurveyQuestionService;
import com.elit.outildedecision.shared.exception.QuestionsNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Service implementation for survey question operations.
 * it is used to save, modify and delete the questions of the survey
 */
@Service
@Slf4j
public class SurveyQuestionServiceImpl implements SurveyQuestionService {

  /**
   * The survey question repository.
   */
  private final SurveyQuestionRepository questionRepository;

  /**
   * Mapper between survey entities and their DTO representations.
   */
  private final SurveyMapper surveyMapper;

  /**
   * Constructor for creating a new SurveyQuestionServiceImpl instance.
   *
   * @param questionRepository question repository to use
   * @param surveyMapper the survey mapper to use
   */
  public SurveyQuestionServiceImpl(final SurveyQuestionRepository questionRepository,
                                   final SurveyMapper surveyMapper) {
    this.questionRepository = questionRepository;
    this.surveyMapper = surveyMapper;
  }

  /**
   * Creates a new survey question.
   *
   * @param question The survey question to create.
   * @return The created survey question.
   * @throws QuestionFormatNotValidException If the specified question format is not valid.
   */
  @Override
  @Transactional
  public SurveyQuestion createQuestion(final SurveyQuestionRequest question)
    throws QuestionFormatNotValidException {
    if (Objects.isNull(question)) {
      throw new QuestionFormatNotValidException("question format not valid");
    }
    SurveyQuestion newQuestion = new SurveyQuestion();
    newQuestion.setQuestion(question.question());
    newQuestion.setType(question.type());
    SurveyQuestion saved = questionRepository.save(newQuestion);
    log.info("Created survey question with id {}", saved.getId());
    return saved;
  }

  /**
   * Modifies an existing survey question.
   *
   * @param questionId The ID of the survey question to modify.
   * @param question The modified survey question.
   * @return The modified survey question.
   * @throws QuestionFormatNotValidException If the specified question format is not valid.
   * @throws QuestionsNotFoundException If the specified question does not exist.
   */
  @Override
  @Transactional
  public SurveyQuestion modifyQuestion(final Long questionId,
                                       final SurveyQuestionRequest question)
    throws QuestionFormatNotValidException, QuestionsNotFoundException {
    if (Objects.isNull(question)) {
      throw new QuestionFormatNotValidException("le format de la question n'est pas valide");
    }
    if (!questionRepository.existsById(questionId)) {
      throw new QuestionsNotFoundException("la question que vous voulez modifier n'existe pas");
    }
    SurveyQuestion questionToUpdate = questionRepository.getById(questionId);
    questionToUpdate.setQuestion(question.question());
    log.info("Modified survey question with id {}", questionId);
    return questionRepository.save(questionToUpdate);
  }

  /**
   * Deletes a survey question.
   *
   * @param questionId The ID of the survey question to delete.
   * @return The deleted survey question, wrapped in an Optional.
   * @throws QuestionsNotFoundException If the specified question cannot be found.
   */
  @Override
  public Optional<SurveyQuestion> deleteQuestion(final Long questionId)
    throws QuestionsNotFoundException {
    Optional<SurveyQuestion> toBeDeleted = questionRepository.findById(questionId);
    if (toBeDeleted.isPresent() && questionId.equals(toBeDeleted.get().getId())) {
      questionRepository.deleteById(questionId);
      log.info("Deleted survey question with id {}", questionId);
    } else {
      throw new QuestionsNotFoundException("La question que vous voulez supprimer n'existe pas");
    }
    return toBeDeleted;
  }

  /**
   * Retrieves a survey question by ID.
   *
   * @param questionId The ID of the survey question to retrieve.
   * @return The survey question.
   * @throws QuestionsNotFoundException If the specified question cannot be found.
   */
  @Override
  @Transactional(readOnly = true)
  public SurveyQuestion selectQuestion(final Long questionId) throws QuestionsNotFoundException {
    return questionRepository.findById(questionId)
      .orElseThrow(() -> new QuestionsNotFoundException("Question Introuvable"));
  }

  /**
   * Retrieves a list of all survey questions.
   *
   * @return A list of SurveyQuestionResponse objects representing all survey questions.
   */
  @Override
  @Transactional(readOnly = true)
  public List<SurveyQuestionResponse> getQuestionsList() {
    return questionRepository.findAll().stream()
      .map(surveyMapper::toQuestionResponse)
      .toList();
  }
}
