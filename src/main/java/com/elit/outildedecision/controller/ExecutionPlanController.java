package com.elit.outildedecision.controller;

import com.elit.outildedecision.exception.ExecutionPlanNotFoundException;
import com.elit.outildedecision.exception.UserNotAuthorizedException;
import com.elit.outildedecision.model.ExecutionPlanAnswerModel;
import com.elit.outildedecision.model.ExecutionPlanScoreResponseModel;
import com.elit.outildedecision.model.ExecutionVoteRequestModel;
import com.elit.outildedecision.service.ExecutionPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ExecutionPlanController {
    private final ExecutionPlanService executionPlanService;

    public ExecutionPlanController(ExecutionPlanService executionPlanService) {
        this.executionPlanService = executionPlanService;
    }

    @PostMapping("/executionPlan/create/{projectId}")
    ResponseEntity<Long> createExecutionPlan(@PathVariable Long projectId) {
        return executionPlanService.createExecutionPlan(projectId);
    }

    @GetMapping("/executionPlan/formConstraints/{executionPlanId}")
    // @PreAuthorize("isContributer(#projectId)")
    public ResponseEntity<Object> getQuestionsAndAnswers(@PathVariable Long executionPlanId){
        return executionPlanService.getQuestionsAndAnswers(executionPlanId);
    }

    @PutMapping(value = "/executionPlan/formConstraints/putAnswers/{executionPlanId}")
    // @PreAuthorize("isProjectOwner(#projectId)")
    public ResponseEntity<Object> updateAnswers(@RequestBody List<ExecutionPlanAnswerModel> executionPlanAnswerModel, @PathVariable Long executionPlanId){
        return executionPlanService.updateAnswers(executionPlanAnswerModel, executionPlanId);
    }

    @GetMapping("/executionPlan/methodologies/{executionPlanId}")
    public ResponseEntity<Object> getMethodologies(@PathVariable Long executionPlanId){
        return executionPlanService.getMethodologies(executionPlanId);
    }

    @PostMapping("/executionPlan/methodologies/vote")
    public ResponseEntity<String> postMethodologiesVote(@RequestBody ExecutionVoteRequestModel executionVoteRequestModel) throws UserNotAuthorizedException {
        return executionPlanService.postMethodologiesVote(executionVoteRequestModel);
    }

    @GetMapping("/executionPlan/{executionPlanId}/score")
    public ExecutionPlanScoreResponseModel getExecutionPlanScore(@PathVariable Long executionPlanId) {
        return executionPlanService.getExecutionPlanScore(executionPlanId);
    }

    @PostMapping("/executionPlan/isFinished/{executionPlanId}")
    public ResponseEntity<Boolean> putIsFinishedExecutionPlan(@PathVariable Long executionPlanId) throws UserNotAuthorizedException, ExecutionPlanNotFoundException {
        return executionPlanService.putIsFinishedExecutionPlan(executionPlanId);
    }
}
