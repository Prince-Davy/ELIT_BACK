package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.SurveyQuestion;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.QuestionFormatNotValidException;
import com.elit.outildedecision.repository.SurveyQuestionRepository;
import com.elit.outildedecision.service.SurveyQuestionService;
import org.aspectj.weaver.patterns.TypePatternQuestions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * NOT USED FOR THIS API class used to manage survey questions. Contains methods to display, create,
 * delete or modify survey questions.
 */
@Service
public class SurveyQuestionServiceImpl implements SurveyQuestionService {

  private final SurveyQuestionRepository questionRepo;

  public SurveyQuestionServiceImpl(SurveyQuestionRepository questionRepo) {
    this.questionRepo = questionRepo;
  }

  /**
   * method used to add a new question to the survey
   *
   * @param question represents the new question to add on survey
   * @return added question. return needed for API testing
   */
  @Override
  public SurveyQuestion createQuestion(SurveyQuestion question)
      throws QuestionFormatNotValidException {
    if (Objects.isNull(question)) {
      throw new QuestionFormatNotValidException("question format not valid");
    }
    return questionRepo.save(question);
  }

  /**
   * @return SurveyQuestion
   */
  // @Override
  // public SurveyQuestion createQuestion(){
  // String[] questionList = {
  // "Dans quelle mesure êtes-vous satisfait de l'ensemble de l'experience ?",
  // "Dans quelle mésure pensez-vous que ce processus de décision a permis de
  // découvrir des limites? (découverte des limites)",
  // "Dans quelle mésure le processus de décision a-t-il aidé à résoudre les
  // problèmes identifiés ?",
  // "Après avoir participé (individuellement/collectivement) à ce processus, une
  // execution (individuellement/collectivement) serait-elle plus efficace ?"
  // };
  // for (String qst : questionList
  // ) {
  // if(Objects.isNull(questionRepo.findByQuestion(qst))){
  // questionRepo.save(SurveyQuestion.builder().question(qst).build());
  // }
  // }
  // }

  /**
   * method used to modify an existing question.
   *
   * @param question given in parameter and contains modification to apply.
   * @return updated question. return needed for API testing
   */
  @Override
  public SurveyQuestion modifyQuestion(Long questionId, SurveyQuestion question)
      throws QuestionFormatNotValidException {
    if (Objects.isNull(question)) {
      throw new QuestionFormatNotValidException("question format not valid");
    }
    if (!question.getId().equals(questionId)) {
      throw new QuestionFormatNotValidException("question not found with this reference");
    }
    Optional<SurveyQuestion> toBeUpdated = questionRepo.findById(questionId);
    toBeUpdated.get().setQuestion(question.getQuestion());
    return question;
  }

  /**
   * method used to remove a question on survey
   *
   * @param questionId given in parameter and represents the id of the existing question to delete
   * @return deleted question. return needed for API testing
   */
  @Override
  public Optional<SurveyQuestion> deleteQuestion(Long questionId) {
    Optional<SurveyQuestion> toBeDeleted = questionRepo.findById(questionId);
    if (questionId.equals(toBeDeleted.get().getId())) {
      questionRepo.deleteById(questionId);
    } else {
      throw new RuntimeException("question to delete not found");
    }
    return toBeDeleted;
  }

  /**
   * method used to select and display an existing question
   *
   * @param questionId given in parameter and represents the desired question to display
   * @return selected question.
   */
  @Override
  public Optional<SurveyQuestion> selectQuestion(Long questionId) {
    if (questionRepo.findById(questionId).isPresent()) {
      return questionRepo.findById(questionId);
    } else {
      throw new RuntimeException("unknown question");
    }
  }

  /**
   * method used to display all questions that belong to the survey
   *
   * @return a list of all existing questions on survey
   */
  @Override
  public List<SurveyQuestion> getQuestionsList() {
    return questionRepo.findAll();
  }
}
