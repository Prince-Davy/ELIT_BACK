
package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.SurveyQuestion;
import com.elit.outildedecision.exception.QuestionFormatNotValidException;
import com.elit.outildedecision.exception.QuestionsNotFoundException;
import com.elit.outildedecision.repository.SurveyQuestionRepository;
import com.elit.outildedecision.service.SurveyQuestionService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * Service implementation for survey question operations.
 * it is used to save, modify and delete the questions of the survey
 */
@Service
public class SurveyQuestionServiceImpl implements SurveyQuestionService {

  private final SurveyQuestionRepository questionRepo;

  /**
   * Constructor for creating a new SurveyQuestionServiceImpl instance.
   *
   * @param questionRepo question Repository to use
   */
  public SurveyQuestionServiceImpl(final SurveyQuestionRepository questionRepo) {
    this.questionRepo = questionRepo;
  }

  /**
   * Creates a new survey question.
   *
   * @param question The survey question to create.
   * @return The created survey question.
   * @throws QuestionFormatNotValidException If the specified question format is not valid.
   */
  @Override
  public SurveyQuestion createQuestion(final SurveyQuestion question)
    throws QuestionFormatNotValidException {
    if (Objects.isNull(question)) {
      throw new QuestionFormatNotValidException("question format not valid");
    }
    return questionRepo.save(question);
  }

  /**
   * Modifies an existing survey question.
   *
   * @param questionId The ID of the survey question to modify.
   * @param question The modified survey question.
   * @return The modified survey question.
   * @throws QuestionFormatNotValidException If the specified question format is not valid.
   */
  @Override
  public SurveyQuestion modifyQuestion(final Long questionId,
                                       final SurveyQuestion question)
    throws QuestionFormatNotValidException {
    if (Objects.isNull(question)) {
      throw new QuestionFormatNotValidException("question format not valid");
    }
    if (!question.getId().equals(questionId)) {
      throw new QuestionFormatNotValidException("question not found with this reference");
    }
    Optional<SurveyQuestion> toBeUpdated = questionRepo.findById(questionId);
    if(toBeUpdated.isPresent())
      toBeUpdated.get().setQuestion(question.getQuestion());
    return question;
  }

  /**
   * Deletes a survey question.
   *
   * @param questionId The ID of the survey question to delete.
   * @return The deleted survey question, wrapped in an Optional.
   * @throws QuestionsNotFoundException If the specified question cannot be found.
   */
  @Override
  public Optional<SurveyQuestion> deleteQuestion(final Long questionId) throws QuestionsNotFoundException {
    Optional<SurveyQuestion> toBeDeleted = questionRepo.findById(questionId);
    if (toBeDeleted.isPresent() && questionId.equals(toBeDeleted.get().getId())) {
      questionRepo.deleteById(questionId);
    } else {
      throw new QuestionsNotFoundException("La question que vous voulez supprimer n'existe pas");
    }
    return toBeDeleted;
  }

  /**
   * Retrieves a survey question by ID.
   *
   * @param questionId The ID of the survey question to retrieve.
   * @return The survey question, wrapped in an Optional.
   * @throws QuestionsNotFoundException If the specified question cannot be found.
   */
  @Override
  public SurveyQuestion selectQuestion(final Long questionId) throws QuestionsNotFoundException {
    return questionRepo.findById(questionId)
      .orElseThrow(() -> new QuestionsNotFoundException("Question Introuvable"));
  }

  /**
   * Retrieves a list of all survey questions.
   *
   * @return A list of SurveyQuestion objects representing all survey questions.
   */
  @Override
  public List<SurveyQuestion> getQuestionsList() {
    return questionRepo.findAll();
  }
}

