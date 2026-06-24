package com.elit.outildedecision.audit.calculate;

import com.elit.outildedecision.audit.entity.AuditCategoryResult;
import com.elit.outildedecision.audit.entity.AuditScore;
import com.elit.outildedecision.audit.entity.LevelScore;
import com.elit.outildedecision.audit.entity.comment.CategoryComment;
import com.elit.outildedecision.audit.entity.comment.GlobalComment;
import com.elit.outildedecision.audit.service.CategoryCommentService;
import com.elit.outildedecision.audit.service.GlobalCommentService;
import com.elit.outildedecision.shared.util.MethodType;
import com.elit.outildedecision.shared.util.QuestionCategory;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * Component responsible for enriching {@link AuditCategoryResult} objects
 * with additional metadata such as the evaluation level and associated comment.
 * This class encapsulates the logic for determining the {@link LevelScore}
 * based on the score percentage and retrieving the corresponding {@link CategoryComment}
 * from the database via {@link CategoryCommentService}.
*/
@Component
public class AuditCategoryResultEnricher {

  private final CategoryCommentService categoryCommentService;
  private final GlobalCommentService globalCommentService;

  /**
   * Constructs an instance of {@code AuditCategoryResultEnricher}.
   *
   * @param categoryCommentService the service used to retrieve category comments
   *                                based on category and level
   * @param globalCommentService the service used to retrieve global comment
   */
  public AuditCategoryResultEnricher(final CategoryCommentService categoryCommentService, final GlobalCommentService globalCommentService) {
    this.categoryCommentService = categoryCommentService;
    this.globalCommentService = globalCommentService;
  }

  /**
   * Enriches the AuditScore object with a global comment and detailed audit category results.
   *
   * @param auditScore the AuditScore object to be enriched
   * @param methodType the MethodType used to retrieve the global comment and enrich category results
   * @param auditCategoryResults the list of AuditCategoryResult to be enriched and set
   */
  public void enrichAuditComment(final AuditScore auditScore, final MethodType methodType,  final List<AuditCategoryResult> auditCategoryResults) {
    GlobalComment globalComment = globalCommentService.findByMethodAndLevel(methodType, LevelScore.fromPercentage(auditScore.getGlobalScorePercentage()));
    enrichAuditCategoryResult(methodType, auditCategoryResults);
    auditScore.setGlobalComment(globalComment);
    auditScore.setAuditCategoryResults(auditCategoryResults);
  }


  /**
   * Enriches the given {@link AuditCategoryResult} by .
   *Determining the {@link LevelScore} based on the score percentage
   * Fetching the corresponding {@link CategoryComment}
   * Setting the level, comment ID, and comment text into the DTO
   *
   * @param auditCategoryResults the list of AuditCategoryResult to be enriched and set
   * @param methodType the methodType
   */
  public void enrichAuditCategoryResult(final MethodType methodType, final List<AuditCategoryResult> auditCategoryResults) {
    List<CategoryComment> allCategoryComment = categoryCommentService.findAllByMethod(methodType);
    auditCategoryResults.forEach(auditCategoryResult -> {
      LevelScore levelScore = resolveLevelFromScore(auditCategoryResult.getScorePercentageCategory());
      QuestionCategory questionCategory = auditCategoryResult.getCategory();
      CategoryComment matchingComment = allCategoryComment.stream()
        .filter(categoryComment -> categoryComment.getCategory().equals(questionCategory)
          && categoryComment.getLevel().equals(levelScore))
        .findFirst()
        .orElse(null);
      auditCategoryResult.setCategoryComment(matchingComment);
      auditCategoryResult.setLevel(levelScore);
    });
  }


  /**
   * Resolves the {@link LevelScore} based on the given score percentage.
   *
   * @param percentage the score percentage to evaluate
   * @return the corresponding {@link LevelScore}
   */
  private LevelScore resolveLevelFromScore(final double percentage) {
    return LevelScore.fromPercentage(percentage);
  }
}
