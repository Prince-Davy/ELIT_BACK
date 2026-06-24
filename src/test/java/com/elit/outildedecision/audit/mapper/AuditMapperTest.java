package com.elit.outildedecision.audit.mapper;

import com.elit.outildedecision.audit.dto.AuditScoreResponse;
import com.elit.outildedecision.audit.dto.CommentResponse;
import com.elit.outildedecision.audit.dto.QuestionRequest;
import com.elit.outildedecision.audit.dto.QuestionResponse;
import com.elit.outildedecision.audit.dto.QuestionResponseScoreResponse;
import com.elit.outildedecision.audit.entity.Audit;
import com.elit.outildedecision.audit.entity.AuditCategoryResult;
import com.elit.outildedecision.audit.entity.AuditScore;
import com.elit.outildedecision.audit.entity.LevelScore;
import com.elit.outildedecision.audit.entity.Question;
import com.elit.outildedecision.audit.entity.QuestionResponseScore;
import com.elit.outildedecision.audit.entity.ScoringType;
import com.elit.outildedecision.audit.entity.comment.GlobalComment;
import com.elit.outildedecision.audit.entity.comment.ScrumCategoryComment;
import com.elit.outildedecision.audit.entity.comment.ScrumGlobalComment;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.util.MethodType;
import com.elit.outildedecision.shared.util.QuestionCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuditMapperTest {

  private final AuditMapper mapper = Mappers.getMapper(AuditMapper.class);

  private static final Long COMMON_ID = 1L;
  private static final Double GLOBALE_PERCENTAGE = 50.0;

  private static AuditScore auditScore;
  private static Audit audit;
  private static Question question;
  private static QuestionResponseScore questionResponseScore;
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
      .score(10.0).build();

    auditCategoryResult = AuditCategoryResult.builder()
      .level(LevelScore.BON)
      .categoryComment(ScrumCategoryComment.builder().build())
      .scorePercentageCategory(GLOBALE_PERCENTAGE)
      .category(question.getCategory())
      .build();

    auditScore = AuditScore.builder()
     .audit(audit)
     .user(user)
     .globalScorePercentage(GLOBALE_PERCENTAGE)
      .auditCategoryResults(List.of(auditCategoryResult))
     .questionResponses(List.of(questionResponseScore))
     .submissionDate(new Timestamp(System.currentTimeMillis()))
     .build();
  }

  @Test
  public void toAuditScoreResponse_shouldSuccess() {
    AuditScoreResponse dto = mapper.toAuditScoreResponse(auditScore);

    assertNotNull(dto);
    assertEquals(auditScore.getGlobalScorePercentage(), dto.getGlobalScorePercentage());
    assertEquals(auditScore.getUser().getUserId(), dto.getUserId());
    assertEquals(auditScore.getAudit().getAuditId(), dto.getAuditId());
    assertEquals(auditScore.getAuditCategoryResults().size(), dto.getAuditCategoryResults().size());
    assertEquals(auditScore.getAuditCategoryResults().get(0).getLevel(), dto.getAuditCategoryResults().get(0).getLevel());
    assertEquals(auditScore.getSubmissionDate(), dto.getSubmissionDate());

    assertNotNull(dto.getQuestionResponses());
    assertEquals(1, dto.getQuestionResponses().size());
    QuestionResponseScoreResponse r0 = dto.getQuestionResponses().get(0);
    assertEquals(question.getQuestionId(), r0.getQuestionId());
    assertEquals(question.getQuestionRef(), r0.getQuestionRef());
    assertEquals(question.getCategory(), r0.getCategory());
    assertEquals(questionResponseScore.getAnswerLabel(), r0.getAnswerLabel());
    assertEquals(questionResponseScore.getAnswerValue(), r0.getAnswerValue());
    assertEquals(questionResponseScore.getScore(), r0.getScore());
  }

  @Test
  void toQuestionResponse_shouldMapBaseFields() {
    QuestionResponse model = mapper.toQuestionResponse(question);

    assertNotNull(model);
    assertEquals(question.getQuestionRef(), model.getQuestionRef());
    assertEquals(question.getWeight(), model.getWeight());
    assertEquals(question.getAuditAnswerLabel(), model.getAuditAnswerLabel());
    assertEquals(question.getAuditAnswerValue(), model.getAuditAnswerValue());
    assertEquals(question.getCategory(), model.getCategory());
    assertEquals(question.getCategory().getDisplayName(), model.getCategoryDisplayName());
    assertEquals(question.getScoringType(), model.getScoringType());
  }

  @Test
  void toQuestion_shouldSuccess() {
    QuestionRequest req = QuestionRequest.builder()
      .category(question.getCategory())
      .questionRef(question.getQuestionRef())
      .weight(question.getWeight())
      .auditAnswerLabel(question.getAuditAnswerLabel())
      .auditAnswerValue(question.getAuditAnswerValue())
      .scoringType(question.getScoringType())
      .build();

    Question q = mapper.toQuestion(req, audit);

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
  void toCommentResponse_nullInput() {
    CommentResponse result = mapper.toCommentResponse(null);
    assertNull(result, "Expected result to be null when input is null");
  }

  @Test
  void toCommentResponse_validInput() {
    GlobalComment globalComment = ScrumGlobalComment.builder()
      .method(MethodType.SCRUM)
      .level(LevelScore.BON)
      .title("title")
      .text("text")
      .build();

    CommentResponse result = mapper.toCommentResponse(globalComment);

    assertNotNull(result);
    assertEquals(LevelScore.BON, result.getLevel());
    assertEquals(MethodType.SCRUM, result.getMethod());
    assertEquals("text", result.getText());
    assertEquals("title", result.getTitle());
  }

}
