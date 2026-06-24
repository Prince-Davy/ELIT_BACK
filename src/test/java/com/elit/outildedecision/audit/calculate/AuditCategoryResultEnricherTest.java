package com.elit.outildedecision.audit.calculate;

import com.elit.outildedecision.audit.entity.AuditCategoryResult;
import com.elit.outildedecision.audit.entity.AuditScore;
import com.elit.outildedecision.audit.entity.LevelScore;
import com.elit.outildedecision.audit.entity.comment.CategoryComment;
import com.elit.outildedecision.audit.entity.comment.GlobalComment;
import com.elit.outildedecision.audit.entity.comment.ScrumCategoryComment;
import com.elit.outildedecision.audit.entity.comment.ScrumGlobalComment;
import com.elit.outildedecision.audit.service.CategoryCommentService;
import com.elit.outildedecision.audit.service.GlobalCommentService;
import com.elit.outildedecision.shared.util.MethodType;
import com.elit.outildedecision.shared.util.QuestionCategory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class AuditCategoryResultEnricherTest {


  @Mock private CategoryCommentService categoryCommentService;
  @Mock private GlobalCommentService globalCommentService;
  @InjectMocks private AuditCategoryResultEnricher enricher;

  private static final double GOOD_SCORE = 85.0;
  private static final String GOOD_TEXT = "Bonne Organisation";
  @BeforeEach
  void setUp() {
    enricher = new AuditCategoryResultEnricher(categoryCommentService,globalCommentService);
  }


  @Test
  void enrichAuditComment_shouldSetGlobalCommentAndCategoryResults() {
    MethodType methodType = MethodType.SCRUM;
    AuditScore auditScore = new AuditScore();
    auditScore.setGlobalScorePercentage(GOOD_SCORE);

    QuestionCategory category = QuestionCategory.ORGANISATIONS;
    AuditCategoryResult categoryResult = new AuditCategoryResult();
    categoryResult.setCategory(category);
    categoryResult.setScorePercentageCategory(GOOD_SCORE);

    List<AuditCategoryResult> categoryResults = List.of(categoryResult);

    LevelScore levelScore = LevelScore.BON;
    GlobalComment globalComment = ScrumGlobalComment.builder().text(GOOD_TEXT).build();
    CategoryComment categoryComment = ScrumCategoryComment.builder().text(GOOD_TEXT).build();
    categoryComment.setCategory(category);
    categoryComment.setLevel(levelScore);

    Mockito.when(globalCommentService.findByMethodAndLevel(methodType, levelScore))
      .thenReturn(globalComment);
    Mockito.when(categoryCommentService.findAllByMethod(methodType))
      .thenReturn(List.of(categoryComment));

    enricher.enrichAuditComment(auditScore, methodType, categoryResults);

    Assertions.assertEquals(globalComment, auditScore.getGlobalComment());
    Assertions.assertEquals(1, auditScore.getAuditCategoryResults().size());
    AuditCategoryResult enrichedResult = auditScore.getAuditCategoryResults().get(0);
    Assertions.assertEquals(categoryComment, enrichedResult.getCategoryComment());
    Assertions.assertEquals(levelScore, enrichedResult.getLevel());
  }

  @Test
  void enrichAuditComment_missingGlobalComment_doesNotThrowAndLeavesNull() {
    MethodType methodType = MethodType.SCRUM;
    AuditScore auditScore = new AuditScore();
    auditScore.setGlobalScorePercentage(GOOD_SCORE);

    QuestionCategory category = QuestionCategory.ORGANISATIONS;
    AuditCategoryResult categoryResult = new AuditCategoryResult();
    categoryResult.setCategory(category);
    categoryResult.setScorePercentageCategory(GOOD_SCORE);
    List<AuditCategoryResult> categoryResults = List.of(categoryResult);

    // Reference data not seeded for this (method, level) pair: must not break the calculation.
    Mockito.when(globalCommentService.findByMethodAndLevel(methodType, LevelScore.BON))
      .thenReturn(null);
    Mockito.when(categoryCommentService.findAllByMethod(methodType))
      .thenReturn(List.of());

    Assertions.assertDoesNotThrow(() ->
      enricher.enrichAuditComment(auditScore, methodType, categoryResults));

    Assertions.assertNull(auditScore.getGlobalComment());
    Assertions.assertEquals(1, auditScore.getAuditCategoryResults().size());
    AuditCategoryResult enrichedResult = auditScore.getAuditCategoryResults().get(0);
    Assertions.assertNull(enrichedResult.getCategoryComment());
    Assertions.assertEquals(LevelScore.BON, enrichedResult.getLevel());
  }

}

