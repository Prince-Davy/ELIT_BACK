package com.elit.outildedecision.audit.calculate;

import com.elit.outildedecision.audit.model.AuditResponseModel;
import com.elit.outildedecision.audit.model.QuestionResponseModel;
import com.elit.outildedecision.audit.model.ScoringType;
import com.elit.outildedecision.util.MethodType;
import com.elit.outildedecision.util.QuestionCategory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;


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
  public void shouldCalculateCorrectlyScoresForDirectAndReversedQuestions(){
    List<QuestionResponseModel> questions = List.of(
      QuestionResponseModel.builder()
        .questionRef("Q27")
        .categoryDisplayName(OUTILS_DOCUMENTATION_CATEGORY.getDisplayName())
        .category(OUTILS_DOCUMENTATION_CATEGORY)
        .weight(3)
        .auditAnswerValue(YES)
      .scoringType(ScoringType.DIRECT)
        .build(),
      QuestionResponseModel.builder()
        .questionRef("Q28")
        .categoryDisplayName(OUTILS_DOCUMENTATION_CATEGORY.getDisplayName())
        .category(OUTILS_DOCUMENTATION_CATEGORY)
        .weight(3)
        .auditAnswerValue(YES)
        .scoringType(ScoringType.DIRECT)
        .build(),
      QuestionResponseModel.builder()
        .questionRef("Q29")
        .categoryDisplayName(OUTILS_DOCUMENTATION_CATEGORY.toString())
        .category(OUTILS_DOCUMENTATION_CATEGORY)
        .weight(2)
        .auditAnswerValue(NO)
        .scoringType(ScoringType.REVERSED)
        .build()
    );

    AuditResponseModel auditResponseModel = AuditResponseModel.builder().method(MethodType.SCRUM).questions(questions).build();
    strategy.calculateScoring(auditResponseModel);

    Assertions.assertEquals(HUNDRED_PERCENT, auditResponseModel.getGlobalScorePercentage());
    Assertions.assertEquals(HUNDRED_PERCENT, auditResponseModel.getCategoryResults().get(0).getScorePercentageCategory());
    Assertions.assertEquals(OUTILS_DOCUMENTATION_CATEGORY.getDisplayName(), auditResponseModel.getCategoryResults().get(0).getCategory());
  }


  @Test
  public void shouldSupportScrumMethodType() {
    Assertions.assertEquals(MethodType.SCRUM, strategy.supportedType());
  }



}
