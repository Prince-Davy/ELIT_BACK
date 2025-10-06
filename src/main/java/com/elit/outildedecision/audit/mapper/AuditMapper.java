package com.elit.outildedecision.audit.mapper;

import com.elit.outildedecision.audit.entity.AuditScore;
import com.elit.outildedecision.audit.entity.Audit;
import com.elit.outildedecision.audit.entity.AuditCategoryResult;
import com.elit.outildedecision.audit.entity.CategoryComment;
import com.elit.outildedecision.audit.entity.GlobalComment;
import com.elit.outildedecision.audit.entity.Question;
import com.elit.outildedecision.audit.entity.QuestionResponseScore;
import com.elit.outildedecision.audit.model.AuditResponseModel;
import com.elit.outildedecision.audit.model.AuditCategoryResultDTO;
import com.elit.outildedecision.audit.model.QuestionResponseScoreDTO;
import com.elit.outildedecision.audit.model.CommentDTO;
import com.elit.outildedecision.audit.model.AuditScoreDTO;
import com.elit.outildedecision.audit.model.QuestionRequestModel;
import com.elit.outildedecision.audit.model.QuestionResponseModel;
import com.elit.outildedecision.util.QuestionCategory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
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
      .auditScoreId(auditScore.getAuditScoreId())
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
   * Converts an AuditResponseModel and its associated Audit entity.
   * into an AuditScore domain model.
   *
   * @param model the response model containing audit data from the scoring process
   * @param audit the audit entity to associate with the score
   * @param commentById a map of category comment IDs to CategoryComment objects
   * @return a fully constructed AuditScore entity with linked question responses
   */
  public AuditScore responseModeltoAuditScore(final AuditResponseModel model, final Audit audit, final Map<Long, CategoryComment> commentById) {
    return AuditScore.builder()
      .audit(audit)
      .user(audit.getUser())
      .globalScorePercentage(model.getGlobalScorePercentage())
      .submissionDate(model.getAuditLaunchDate())
      .auditCategoryResults(
        model.getCategoryResults().stream()
          .map(categoryResultDTO -> {
            CategoryComment comment = commentById.get(categoryResultDTO.getCategoryCommentId());
            return auditCategoryResultDtoToEntity(categoryResultDTO, comment);
          })
          .collect(Collectors.toList())
      )
      .questionResponses(convertQuestionResponses(model.getQuestions(), audit))
      .build();
  }

  /**
   * Converts a list of {@link QuestionResponseModel} objects into a list of
   * {@link QuestionResponseScore} entities, linking them to the corresponding audit questions.
   *
   * @param questions The list of question responses from the audit.
   * @param audit     The audit entity containing the reference questions.
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


