package com.elit.outildedecision.audit.mapper;

import com.elit.outildedecision.audit.entity.AuditScore;
import com.elit.outildedecision.audit.entity.Audit;
import com.elit.outildedecision.audit.entity.AuditCategoryResult;
import com.elit.outildedecision.audit.entity.comment.CategoryComment;
import com.elit.outildedecision.audit.entity.comment.GlobalComment;
import com.elit.outildedecision.audit.entity.Question;
import com.elit.outildedecision.audit.entity.QuestionResponseScore;
import com.elit.outildedecision.audit.model.AuditCategoryResultDTO;
import com.elit.outildedecision.audit.model.QuestionResponseScoreDTO;
import com.elit.outildedecision.audit.model.CommentDTO;
import com.elit.outildedecision.audit.model.AuditScoreDTO;
import com.elit.outildedecision.audit.model.QuestionRequestModel;
import com.elit.outildedecision.audit.model.QuestionResponseModel;
import com.elit.outildedecision.util.QuestionCategory;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;


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
      .auditScoreId(auditScore.getId())
      .auditId(auditScore.getAudit().getAuditId())
      .userId(auditScore.getUser().getUserId())
      .globalScorePercentage(auditScore.getGlobalScorePercentage())
      .submissionDate(auditScore.getSubmissionDate())
      .globalComment(globalCommentToCommentDTO(auditScore.getGlobalComment()))
      .auditCategoryResults(
        auditScore.getAuditCategoryResults().stream()
          .map(this::auditCategoryResultToDto)
          .collect(Collectors.toList())
      )
      .questionResponses(
        auditScore.getQuestionResponses().stream()
          .map(this::questionResponseToDto)
          .collect(Collectors.toList())
      )
      .build();
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
    if (question == null) {
      return null;
    }

    Integer weight = question.getWeight();
    Integer answerValue = question.getAuditAnswerValue();
    Integer weightedScore = null;
    Integer maxPossibleScore = null;
    Double scorePercentage = null;
    Boolean isAnswered = null;

    if (weight != null) {
      maxPossibleScore = 2 * weight;
    }
    if (answerValue != null && weight != null) {
      weightedScore = answerValue * weight;
      isAnswered = true;
      if (maxPossibleScore > 0) {
        scorePercentage = (weightedScore.doubleValue() / maxPossibleScore) * 100.0;
      }
    } else {
      isAnswered = false;
    }

    return QuestionResponseModel.builder()
      .id(question.getQuestionId())
      .questionRef(question.getQuestionRef())
      .category(question.getCategory())
      .categoryDisplayName(question.getCategory() != null ? question.getCategory().getDisplayName() : null)
      .weight(weight)
      .auditId(question.getAudit() != null ? question.getAudit().getAuditId() : null)
      .auditAnswerLabel(question.getAuditAnswerLabel())
      .auditAnswerValue(answerValue)
      .weightedScore(weightedScore)
      .maxPossibleScore(maxPossibleScore)
      .scorePercentage(scorePercentage)
      .isAnswered(isAnswered)
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
  public Question questionRequestModelToQuestion(final QuestionRequestModel questionRequest, final Audit audit) {
    return Question.builder()
      .category(questionRequest.getCategory())
      .questionRef(questionRequest.getQuestionRef())
      .weight(questionRequest.getWeight())
      .auditAnswerLabel(questionRequest.getAuditAnswerLabel())
      .auditAnswerValue(questionRequest.getAuditAnswerValue())
      .audit(audit)
      .scoringType(questionRequest.getScoringType())
      .build();
  }

  /**
   * Converts an AuditCategoryResult entity to its corresponding DTO.
   * @param result the entity to convert
   * @return the mapped AuditCategoryResultDTO, or null if input is null
   */
  public AuditCategoryResultDTO auditCategoryResultToDto(final AuditCategoryResult result) {
    if (result == null) {
      return null;
    }
    return AuditCategoryResultDTO.builder()
      .category(result.getCategory().getDisplayName())
      .scorePercentageCategory(result.getScorePercentageCategory())
      .level(result.getLevel())
      .categoryCommentId(result.getCategoryComment().getId())
      .text(result.getCategoryComment().getText())
      .build();
  }

  /**
   * Converts an AuditCategoryResultDTO to its corresponding entity.
   *
   * @param dto the DTO to convert
   * @param comment the CategoryComment entity to associate
   * @return the mapped AuditCategoryResult entity, or null if input is null
   */
  public AuditCategoryResult auditCategoryResultDtoToEntity(final AuditCategoryResultDTO dto, final CategoryComment comment) {
    if (dto == null) {
      return null;
    }
    return AuditCategoryResult.builder()
      .category(QuestionCategory.getQuestionCategory(dto.getCategory()))
      .scorePercentageCategory(dto.getScorePercentageCategory())
      .level(dto.getLevel())
      .categoryComment(comment)
      .build();
  }

  /**
   * Converts a QuestionResponseScore entity to its corresponding DTO.
   *
   * @param q the entity to convert
   * @return the mapped QuestionResponseScoreDTO, or null if input or its question is null
   */
  public QuestionResponseScoreDTO questionResponseToDto(final QuestionResponseScore q) {
    if (q == null || q.getQuestion() == null) {
      return null;
    }
    return QuestionResponseScoreDTO.builder()
      .questionId(q.getQuestion().getQuestionId())
      .questionRef(q.getQuestion().getQuestionRef())
      .category(q.getQuestion().getCategory())
      .answerLabel(q.getAnswerLabel())
      .answerValue(q.getAnswerValue())
      .score(q.getScore())
      .build();
  }

  /**
   * Converts a QuestionResponseScore entity to its corresponding DTO.
   *
   * @param globalComment the entity to convert
   * @return the mapped QuestionResponseScoreDTO, or null if input or its question is null
   */
  public CommentDTO globalCommentToCommentDTO(final GlobalComment globalComment) {
    if (globalComment == null) {
      return null;
    }
    return CommentDTO.builder()
      .id(globalComment.getId())
      .level(globalComment.getLevel())
      .method(globalComment.getMethod())
      .text(globalComment.getText())
      .title(globalComment.getTitle())
      .build();
  }
}


