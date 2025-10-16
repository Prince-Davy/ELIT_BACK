package com.elit.outildedecision.audit.calculate;

import com.elit.outildedecision.audit.entity.Audit;
import com.elit.outildedecision.audit.entity.AuditCategoryResult;
import com.elit.outildedecision.audit.entity.AuditScore;
import com.elit.outildedecision.audit.entity.Question;
import com.elit.outildedecision.audit.model.AuditResponseModel;
import com.elit.outildedecision.audit.model.QuestionResponseModel;
import com.elit.outildedecision.audit.model.ScoringType;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.util.MethodType;
import com.elit.outildedecision.util.QuestionCategory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class ScrumScoringStrategyTest {

  private ScrumScoringStrategy strategy;

  @Mock
  private AuditCategoryResultEnricher auditCategoryResultEnricher;

  private final static double HUNDRED_PERCENT= 100.0;
  private final static int YES= 2;
  private final static int NO= 0;
  private final static QuestionCategory OUTILS_DOCUMENTATION_CATEGORY = QuestionCategory.OUTILS_ET_DOCUMENTATION;


  @BeforeEach
  public void setup() {
    strategy = new ScrumScoringStrategy(auditCategoryResultEnricher);
  }



  @Test
  public void shouldCalculateCorrectlyScoresForDirectAndReversedQuestions() {
    // Arrange
    Audit audit = Audit.builder()
      .auditId(1L)
      .method(MethodType.SCRUM)
      .user(User.builder().userId(1L).build())
      .auditLaunchDate(Timestamp.valueOf(LocalDateTime.now()))
      .build();

    Question directQuestion = Question.builder()
      .questionRef("Q1")
      .weight(2)
      .auditAnswerValue(YES) // 2
      .auditAnswerLabel("Oui")
      .category(QuestionCategory.OUTILS_ET_DOCUMENTATION)
      .scoringType(ScoringType.DIRECT)
      .audit(audit).build();

    Question reversedQuestion = Question.builder()
      .questionRef("Q2")
      .weight(2)
      .auditAnswerValue(NO) // 0
      .auditAnswerLabel("Non")
      .category(QuestionCategory.OUTILS_ET_DOCUMENTATION)
      .scoringType(ScoringType.REVERSED)
      .audit(audit).build();

    audit.setQuestions(List.of(directQuestion, reversedQuestion));

    // Act
    AuditScore result = strategy.calculateScoring(audit);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getAuditCategoryResults().size());
    assertEquals(QuestionCategory.OUTILS_ET_DOCUMENTATION, result.getAuditCategoryResults().get(0).getCategory());

    double expectedScore = 100.0; // direct = 2*2 = 4, reverse = (2 - 0)*2 = 4 → total = 8/8 = 100%
    assertEquals(expectedScore, result.getGlobalScorePercentage());

    verify(auditCategoryResultEnricher).enrichAuditCategoryResult(eq(MethodType.SCRUM), any(AuditCategoryResult.class));
  }



  @Test
  public void shouldSupportScrumMethodType() {
    assertEquals(EnumSet.of(MethodType.SCRUM, MethodType.SCALED_AGILITY, MethodType.VMODEL), strategy.supportedTypes());
  }



}
