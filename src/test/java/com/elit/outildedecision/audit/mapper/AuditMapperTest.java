package com.elit.outildedecision.audit.mapper;

import com.elit.outildedecision.audit.entity.*;
import com.elit.outildedecision.audit.model.*;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.util.QuestionCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class AuditMapperTest {

  private final AuditMapper mapper = new AuditMapper();

  private static final Long COMMON_ID = 1L;
  private static final Double GLOBALE_PERCENTAGE = 50.0;

  private static AuditScore auditScore;
  private static Audit audit;
  private static Question question;
  private static QuestionResponseScore questionResponseScore;
  private static QuestionResponseModel questionResponseModel;
  private static AuditResponseModel auditResponseModel;
  private static AuditCategoryResult auditCategoryResult;


  @BeforeEach
  public void setUp() {
    User user = User.builder().userId(COMMON_ID).build();
    audit = Audit.builder().auditId(COMMON_ID).build();

    question = Question.builder()
      .questionId(COMMON_ID)
      .questionRef("Q1")
      .category(QuestionCategory.ORGANISATIONS)
      .weight(2)
      .audit(audit)
      .auditAnswerLabel("NON")
      .auditAnswerValue(2)
      .scoringType(ScoringType.DIRECT)
      .build();
    audit.setQuestions(List.of(question));
    questionResponseScore = QuestionResponseScore.builder().question(question)
      .answerLabel(question.getAuditAnswerLabel())
      .answerValue(question.getAuditAnswerValue())
      .score(2).build();

    auditCategoryResult = AuditCategoryResult.builder()
      .level(LevelScore.BON)
      .categoryComment(CategoryComment.builder().build())
      .scorePercentageCategory(GLOBALE_PERCENTAGE)
      .category(question.getCategory())
      .build();

    auditScore = AuditScore.builder()
     .auditScoreId(COMMON_ID)
     .audit(audit)
     .user(user)
     .globalScorePercentage(GLOBALE_PERCENTAGE)
      .auditCategoryResults(List.of(auditCategoryResult))
     .questionResponses(List.of(questionResponseScore))
     .submissionDate(new Timestamp(System.currentTimeMillis()))
     .build();

    questionResponseModel = QuestionResponseModel.builder()
      .questionRef(question.getQuestionRef())
      .weight(question.getWeight())
      .auditAnswerLabel(question.getAuditAnswerLabel())
      .auditAnswerValue(question.getAuditAnswerValue())
      .currentScore(questionResponseScore.getScore())
      .category(question.getCategory())
      .scoringType(question.getScoringType())
      .build();

    auditResponseModel = AuditResponseModel.builder()
      .globalScorePercentage(50.0)
      .auditLaunchDate(audit.getAuditLaunchDate())
      .questions(List.of(questionResponseModel))
      .build();
  }

  @Test
  public void auditScoretoAuditScoreDto_shouldSuccess() {
    AuditScoreDTO dto = mapper.auditScoretoAuditScoreDto(auditScore);

    assertNotNull(dto);
    assertEquals(auditScore.getAuditScoreId(), dto.getAuditScoreId());
    assertEquals(auditScore.getGlobalScorePercentage(), dto.getGlobalScorePercentage());
    assertEquals(auditScore.getUser().getUserId(), dto.getUserId());
    assertEquals(auditScore.getAudit().getAuditId(), dto.getAuditId());
    assertEquals(auditScore.getAuditCategoryResults().size(), dto.getAuditCategoryResults().size());
    assertEquals(auditScore.getAuditCategoryResults().get(0).getLevel(), dto.getAuditCategoryResults().get(0).getLevel());
    assertEquals(auditScore.getSubmissionDate(), dto.getSubmissionDate());

    assertNotNull(dto.getQuestionResponses());
    assertEquals(1, dto.getQuestionResponses().size());
    QuestionResponseScoreDTO r0 = dto.getQuestionResponses().get(0);
    assertEquals(question.getQuestionId(), r0.getQuestionId());
    assertEquals(question.getQuestionRef(), r0.getQuestionRef());
    assertEquals(question.getCategory(), r0.getCategory());
    assertEquals(questionResponseScore.getAnswerLabel(), r0.getAnswerLabel());
    assertEquals(questionResponseScore.getAnswerValue(), r0.getAnswerValue());
    assertEquals(questionResponseScore.getScore(), r0.getScore());
  }



  @Test
  void responseModeltoAuditScore_shouldSuccess() {
    CategoryComment categoryComment = CategoryComment.builder().category(QuestionCategory.ORGANISATIONS).text("text").build();
    Map<Long, CategoryComment> commentById = Map.of(COMMON_ID,categoryComment);

    AuditScore result = mapper.responseModeltoAuditScore(auditResponseModel, audit, commentById);

    assertNotNull(result);
    assertEquals(auditResponseModel.getGlobalScorePercentage(), result.getGlobalScorePercentage());
    assertEquals(auditResponseModel.getAuditLaunchDate(), result.getSubmissionDate());
    assertNotNull(result.getQuestionResponses());
    assertEquals(1, result.getQuestionResponses().size());
    QuestionResponseScore r0 = result.getQuestionResponses().get(0);
    assertEquals(questionResponseModel.getAuditAnswerLabel(), r0.getAnswerLabel());
    assertEquals(questionResponseModel.getAuditAnswerValue(), r0.getAnswerValue());
    assertEquals(questionResponseModel.getCurrentScore(), r0.getScore());

    assertNotNull(r0.getQuestion());
    assertEquals(question.getQuestionId(), r0.getQuestion().getQuestionId());
    assertEquals(question.getQuestionRef(), r0.getQuestion().getQuestionRef());
  }


  @Test
  void mapToQuestionResponseModel_shouldSuccess() {
    QuestionResponseModel model = mapper.mapToQuestionResponseModel(question);

    assertNotNull(model);
    assertEquals(question.getQuestionRef(), model.getQuestionRef());
    assertEquals(question.getWeight(), model.getWeight());
    assertEquals(question.getAuditAnswerLabel(), model.getAuditAnswerLabel());
    assertEquals(question.getAuditAnswerValue(), model.getAuditAnswerValue());
    assertEquals(question.getCurrentScore(), model.getCurrentScore());
    assertEquals(question.getCategory(), model.getCategory());
    assertEquals(question.getScoringType(), model.getScoringType());
  }

  @Test
  void questionRequestModelToQuestion_shouldSuccess() {
    QuestionRequestModel req = QuestionRequestModel.builder()
      .category(question.getCategory())
      .questionRef(question.getQuestionRef())
      .weight(question.getWeight())
      .auditAnswerLabel(question.getAuditAnswerLabel())
      .auditAnswerValue(question.getAuditAnswerValue())
      .scoringType(question.getScoringType())
      .build();

    Question q = mapper.questionRequestModelToQuestion(req, audit);

    assertNotNull(q);
    assertEquals(req.getCategory(), q.getCategory());
    assertEquals(req.getQuestionRef(), q.getQuestionRef());
    assertEquals(req.getWeight(), q.getWeight());
    assertEquals(req.getAuditAnswerLabel(), q.getAuditAnswerLabel());
    assertEquals(req.getAuditAnswerValue(), q.getAuditAnswerValue());
    assertEquals(req.getScoringType(), q.getScoringType());

    assertEquals(audit, q.getAudit());
  }


  @Test
  void auditCategoryResultDtoToEntity_shouldMapCorrectly() {
    AuditCategoryResultDTO auditCategoryResultDTO = AuditCategoryResultDTO.builder().category(auditCategoryResult.getCategory().getDisplayName()).level(auditCategoryResult.getLevel()).build();
    CategoryComment categoryComment = CategoryComment.builder().category(auditCategoryResult.getCategory()).level(auditCategoryResult.getLevel()).build();
    AuditCategoryResult auditCategoryResult = mapper.auditCategoryResultDtoToEntity(auditCategoryResultDTO,categoryComment);

    assertNotNull(auditCategoryResult);
    assertEquals(auditCategoryResult.getCategory(), auditCategoryResult.getCategory());
    assertEquals(auditCategoryResult.getLevel(), auditCategoryResult.getLevel());

  }


  }
