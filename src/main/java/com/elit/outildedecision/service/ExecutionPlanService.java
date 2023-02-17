package com.elit.outildedecision.service;

import com.elit.outildedecision.exception.ExecutionPlanNotFoundException;
import com.elit.outildedecision.exception.UserNotAuthorizedException;
import com.elit.outildedecision.model.ExecutionPlanAnswerModel;
import com.elit.outildedecision.model.ExecutionPlanScoreResponseModel;
import com.elit.outildedecision.model.ExecutionVoteRequestModel;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ExecutionPlanService {
  ResponseEntity<Long> createExecutionPlan(Long projectId);

  ResponseEntity<Object> getQuestionsAndAnswers(Long executionPlanId);

  ResponseEntity<Object> updateAnswers(
      List<ExecutionPlanAnswerModel> executionPlanAnswerModel, Long executionPlanId);

  ResponseEntity<Object> getMethodologies(Long executionPlanId);

  ResponseEntity<String> postMethodologiesVote(ExecutionVoteRequestModel executionVoteRequestModel)
      throws UserNotAuthorizedException;

  ExecutionPlanScoreResponseModel getExecutionPlanScore(Long executionPlanId);

  ResponseEntity<Boolean> putIsFinishedExecutionPlan(Long executionPlanId)
      throws UserNotAuthorizedException, ExecutionPlanNotFoundException;
}
