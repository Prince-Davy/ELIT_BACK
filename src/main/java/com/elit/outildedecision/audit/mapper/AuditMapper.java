package com.elit.outildedecision.audit.mapper;

import com.elit.outildedecision.audit.dto.AuditCategoryResultResponse;
import com.elit.outildedecision.audit.dto.AuditResponse;
import com.elit.outildedecision.audit.dto.AuditScoreResponse;
import com.elit.outildedecision.audit.dto.CommentResponse;
import com.elit.outildedecision.audit.dto.QuestionRequest;
import com.elit.outildedecision.audit.dto.QuestionResponse;
import com.elit.outildedecision.audit.dto.QuestionResponseScoreResponse;
import com.elit.outildedecision.audit.entity.Audit;
import com.elit.outildedecision.audit.entity.AuditCategoryResult;
import com.elit.outildedecision.audit.entity.AuditScore;
import com.elit.outildedecision.audit.entity.Question;
import com.elit.outildedecision.audit.entity.QuestionResponseScore;
import com.elit.outildedecision.audit.entity.comment.GlobalComment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for the audit context. Maps the direct, side-effect-free fields between the
 * audit entities and their DTOs. The computed fields (question metrics, aggregated totals,
 * {@code totalAuditCount}, user role) are intentionally left out of the mapper and resolved in the
 * service layer, which owns the repository access and the scoring calculations.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AuditMapper {

  /**
   * Maps the base fields of an audit to its response representation. The user role, the aggregated
   * totals ({@code totalQuestions}, {@code totalWeight}, {@code totalAuditCount}) and the category
   * results are computed in the service; the per-question scoring metrics are computed there too.
   *
   * @param audit the audit entity to map
   * @return the corresponding {@link AuditResponse} with only the base fields populated
   */
  @Mapping(target = "projectId", source = "project.projectId")
  @Mapping(target = "projectName", source = "project.name")
  @Mapping(target = "userId", source = "user.userId")
  @Mapping(target = "userFirstName", source = "user.userFirstName")
  @Mapping(target = "userLastName", source = "user.userLastName")
  @Mapping(target = "userRole", ignore = true)
  @Mapping(target = "totalQuestions", ignore = true)
  @Mapping(target = "totalWeight", ignore = true)
  @Mapping(target = "totalAuditCount", ignore = true)
  @Mapping(target = "categoryResults", ignore = true)
  AuditResponse toAuditResponse(Audit audit);

  /**
   * Maps the base fields of a question to its response representation. The scoring metrics
   * ({@code weightedScore}, {@code maxPossibleScore}, {@code scorePercentage}, {@code isAnswered})
   * are computed in the service.
   *
   * @param question the question entity to map
   * @return the corresponding {@link QuestionResponse} with only the base fields populated
   */
  @Mapping(target = "id", source = "questionId")
  @Mapping(target = "categoryDisplayName", source = "category.displayName")
  @Mapping(target = "auditId", source = "audit.auditId")
  @Mapping(target = "weightedScore", ignore = true)
  @Mapping(target = "maxPossibleScore", ignore = true)
  @Mapping(target = "scorePercentage", ignore = true)
  @Mapping(target = "isAnswered", ignore = true)
  QuestionResponse toQuestionResponse(Question question);

  /**
   * Maps an audit score and its nested category/question results to their response representation.
   *
   * @param auditScore the audit score entity to map
   * @return the corresponding {@link AuditScoreResponse}
   */
  @Mapping(target = "auditScoreId", source = "id")
  @Mapping(target = "auditId", source = "audit.auditId")
  @Mapping(target = "userId", source = "user.userId")
  AuditScoreResponse toAuditScoreResponse(AuditScore auditScore);

  /**
   * Maps a global comment to its response representation.
   *
   * @param globalComment the global comment entity to map
   * @return the corresponding {@link CommentResponse}, or {@code null} if the input is {@code null}
   */
  CommentResponse toCommentResponse(GlobalComment globalComment);

  /**
   * Maps an audit category result to its response representation, flattening the associated
   * category comment.
   *
   * @param result the audit category result entity to map
   * @return the corresponding {@link AuditCategoryResultResponse}
   */
  @Mapping(target = "category", source = "category.displayName")
  @Mapping(target = "categoryCommentId", source = "categoryComment.id")
  @Mapping(target = "text", source = "categoryComment.text")
  AuditCategoryResultResponse toAuditCategoryResultResponse(AuditCategoryResult result);

  /**
   * Maps a question response score to its response representation, flattening the associated
   * question.
   *
   * @param questionResponseScore the question response score entity to map
   * @return the corresponding {@link QuestionResponseScoreResponse}
   */
  @Mapping(target = "questionId", source = "question.questionId")
  @Mapping(target = "questionRef", source = "question.questionRef")
  @Mapping(target = "category", source = "question.category")
  QuestionResponseScoreResponse toQuestionResponseScoreResponse(QuestionResponseScore questionResponseScore);

  /**
   * Maps a question request to a new question entity attached to the given audit.
   *
   * @param request the question request to map
   * @param audit   the audit the question belongs to
   * @return the corresponding {@link Question} entity (without an identifier)
   */
  @Mapping(target = "questionId", ignore = true)
  @Mapping(target = "audit", source = "audit")
  Question toQuestion(QuestionRequest request, Audit audit);
}
