package com.elit.outildedecision.service;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.elit.outildedecision.entity.ExecutionPlan;
import com.elit.outildedecision.entity.ExecutionPlanAnswer;
import com.elit.outildedecision.entity.ExecutionPlanQuestion;
import com.elit.outildedecision.model.ExecutionPlanFormModel;
import com.elit.outildedecision.repository.*;
import com.elit.outildedecision.service.implementation.ExecutionPlanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ExecutionPlanServiceImplTest {

  @Mock
  private ExecutionPlanRepository executionPlanRepository;
  @Mock
  private ExecutionPlanAnswerRepository executionPlanAnswerRepository;
  @Mock
  private ExecutionPlanQuestionRepository executionPlanQuestionRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private ProjectUserPermissionRepository projectUserPermissionRepository;
  @Mock
  private MethodRepository methodRepository;
  @Mock
  private ExecutionPlanVoteRepo executionPlanVoteRepo;
  @Mock
  private ProjectRepository projectRepository;
  @Mock
  private PredictibilityAnalysisRepository predictibilityAnalysisRepository;
  @Mock
  private MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository;
  @Mock
  private PredictibilityResultsRepository predictibilityResultsRepository;

  @InjectMocks
  private ExecutionPlanServiceImpl executionPlanServiceImpl;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    executionPlanServiceImpl = new ExecutionPlanServiceImpl(
      executionPlanAnswerRepository,
      executionPlanQuestionRepository,
      executionPlanRepository,
      userRepository,
      projectUserPermissionRepository,
      methodRepository,executionPlanVoteRepo,
      projectRepository,
      predictibilityAnalysisRepository,
      multiCriteriaAnalysisRepository,
      predictibilityResultsRepository);
  }

  @Test
  void testGetQuestionsAndAnswers() {
    Long executionPlanId = 1L;

    // Mock the execution plan
    ExecutionPlan executionPlan = new ExecutionPlan();
    executionPlan.setExecutionPlanId(executionPlanId);

    // Mock the execution plan answers
    List<ExecutionPlanAnswer> executionPlanAnswers = new ArrayList<>();
    ExecutionPlanAnswer executionPlanAnswer = new ExecutionPlanAnswer();
    executionPlanAnswer.setAnswer("Answer");
    ExecutionPlanQuestion executionPlanQuestion = new ExecutionPlanQuestion();
    executionPlanQuestion.setTitle("Title");
    executionPlanQuestion.setQuestion("Question");
    executionPlanQuestion.setExecutionPlanQuestionId(2L);
    executionPlanAnswer.setExecutionPlanQuestion(executionPlanQuestion);
    executionPlanAnswers.add(executionPlanAnswer);
    executionPlan.setExecutionPlanAnswers(executionPlanAnswers);

    // Mock the repository method
    when(executionPlanRepository.findById(executionPlanId)).thenReturn(Optional.of(executionPlan));

    // Call the method
    ResponseEntity<Object> responseEntity = executionPlanServiceImpl.getQuestionsAndAnswers(executionPlanId);

    // Verify the response
    assert responseEntity.getStatusCode() == HttpStatus.OK;
    List<ExecutionPlanFormModel> executionPlanFormModelList = (List<ExecutionPlanFormModel>) responseEntity.getBody();
    assert executionPlanFormModelList.size() == 1;
    ExecutionPlanFormModel executionPlanFormModel = executionPlanFormModelList.get(0);
    assert executionPlanFormModel.getAnswer().equals("Answer");
    assert executionPlanFormModel.getTitle().equals("Title");

    assert executionPlanFormModel.getExecutionQuestionId() == 2L;
  }
}
