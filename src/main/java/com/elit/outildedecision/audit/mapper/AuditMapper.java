package com.elit.outildedecision.audit.mapper;

import com.elit.outildedecision.audit.entity.Audit;
import com.elit.outildedecision.audit.entity.AuditScore;
import com.elit.outildedecision.audit.entity.Question;
import com.elit.outildedecision.audit.entity.QuestionResponseScore;
import com.elit.outildedecision.audit.model.AuditResponseModel;
import com.elit.outildedecision.audit.model.AuditScoreDTO;
import com.elit.outildedecision.audit.model.QuestionRequestModel;
import com.elit.outildedecision.audit.model.QuestionResponseModel;
import com.elit.outildedecision.audit.model.QuestionResponseScoreDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


// todo : Temporairement method static a modifier quand on aura la bonne version de lombok + mapStruct
//
// @Mapper(componentModel = "spring")
// public interface AuditMapper {
//
// Audit auditResponseModelToAudit(AuditResponseModel auditResponseModel);
// AuditResponseModel auditResponseModelToAudit(Audit audit);
// AuditScoreDTO auditScoreToAuditScoreDTO(AuditScore auditScore);
// AuditScore auditScoreDtoToAuditScore(AuditScoreDTO auditScoreDTO);
//
// QuestionResponseScoreDTO questionResponseScoreToQuestionResponseScoreDTO(QuestionResponseScore  questionResponseScore);
// QuestionResponseScore questionResponseScoreDtoToQuestionResponseScore(QuestionResponseScoreDTO questionResponseScoreDTO);
//}


/**
 * Mapper class responsible for converting between domain models and DTO related to audit .
 * This component is used to transform {@link AuditScore} entities into {@link AuditScoreDTO} objects and vice versa.
 */
@Component
public class AuditMapper {


  /**
   * Converts an {@link AuditScore} entity into an {@link AuditScoreDTO}.
   *
   * @param auditScore The domain model representing the audit score.
   * @return A DTO containing audit score data, including category scores and question responses.
   */
  public AuditScoreDTO auditScoretoAuditScoreDto(final AuditScore auditScore) {
    return AuditScoreDTO.builder()
      .auditScoreId(auditScore.getAuditScoreId())
      .auditId(auditScore.getAudit().getAuditId())
      .userId(auditScore.getUser().getUserId())
      .globalScorePercentage(auditScore.getGlobalScorePercentage())
      .submissionDate(auditScore.getSubmissionDate())
      .categoryScores(auditScore.getCategoryScores())
      .questionResponses(auditScore.getQuestionResponses().stream().map(q -> QuestionResponseScoreDTO.builder()
        .questionId(q.getQuestion().getQuestionId())
        .questionRef(q.getQuestion().getQuestionRef())
        .category(q.getQuestion().getCategory())
        .answerLabel(q.getAnswerLabel())
        .answerValue(q.getAnswerValue())
        .score(q.getScore())
        .build()).collect(Collectors.toList()))
      .build();
  }

  /**
   * Converts an {@link AuditResponseModel} and its associated {@link Audit} entity
   * into an {@link AuditScore} domain model.
   *
   * @param model The response model containing audit data from the scoring process.
   * @param audit The audit entity to associate with the score.
   * @return A fully constructed {@link AuditScore} entity with linked question responses.
   */
  public AuditScore responseModeltoAuditScore(final AuditResponseModel model, final Audit audit) {
    AuditScore auditScore = AuditScore.builder()
      .audit(audit)
      .user(audit.getUser())
      .globalScorePercentage(model.getGlobalScorePercentage())
      .submissionDate(model.getAuditLaunchDate())
      .categoryScores(model.getCategoryScores())
      .questionResponses(convertQuestionResponses(model.getQuestions(), audit))
      .build();

    auditScore.getQuestionResponses().forEach(q -> q.setAuditScore(auditScore));
    return auditScore;
  }

  /**
   * Converts a list of {@link QuestionResponseModel} objects into a list of
   * {@link QuestionResponseScore} entities, linking them to the corresponding audit questions.
   *
   * @param questions The list of question responses from the audit.
   * @param audit The audit entity containing the reference questions.
   * @return A list of {@link QuestionResponseScore} entities.
   * @throws IllegalArgumentException if a question reference cannot be matched to an audit question.
   */
  private List<QuestionResponseScore> convertQuestionResponses(final List<QuestionResponseModel> questions, final Audit audit) {
    return questions.stream().map(q -> QuestionResponseScore.builder()
      .answerLabel(q.getAuditAnswerLabel())
      .answerValue(q.getAuditAnswerValue())
      .score(q.getCurrentScore())
      .question(
        audit.getQuestions().stream()
          .filter(questionAudit -> Objects.equals(questionAudit.getQuestionRef(), q.getQuestionRef()))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Question not found for ref: " + q.getQuestionRef()))
      )

      .build()).collect(Collectors.toList());
  }

  /**
   * Map Question entity to QuestionResponseModel.
   * Following Single Responsibility Principle - handles only question response
   * mapping.
   * Includes complete scoring metrics and answer history.
   *
   * @param question the question entity
   * @return the question response model
   */
  public QuestionResponseModel mapToQuestionResponseModel(final Question question) {
    return QuestionResponseModel.builder()
      .questionRef(question.getQuestionRef())
      .weight(question.getWeight())
      .auditAnswerLabel(question.getAuditAnswerLabel())
      .auditAnswerValue(question.getAuditAnswerValue())
      .currentScore(question.getCurrentScore())
      .category(question.getCategory())
      .scoringType(question.getScoringType())
      .build();
  }

  /**
   * Map QuestionRequestModel to Question entity.
   * Following Single Responsibility Principle - handles only question request
   * mapping.
   * Includes automatic score calculation and history creation.
   *
   * @param questionRequest the question request model
   * @param audit           the associated audit entity
   * @return the question entity
   */
  public  Question questionRequestModelToQuestion(final QuestionRequestModel questionRequest, final Audit audit) {
    return  Question.builder()
      .category(questionRequest.getCategory())
      .questionRef(questionRequest.getQuestionRef())
      .weight(questionRequest.getWeight())
      .auditAnswerLabel(questionRequest.getAuditAnswerLabel())
      .auditAnswerValue(questionRequest.getAuditAnswerValue())
      .audit(audit)
      .scoringType(questionRequest.getScoringType())
      .build();
  }

}


