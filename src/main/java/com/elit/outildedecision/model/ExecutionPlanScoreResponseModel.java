package com.elit.outildedecision.model;

import com.elit.outildedecision.entity.PredictibilityResults;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionPlanScoreResponseModel {
    private Long executionPlanId;
    private Boolean isFinished;
    private List<String> chooseMethod;
    private Integer countVotes;
    private Integer executionPlanIteration;
    private List<MethodModel> allMethods;
    private List<PredictibilityResultsResponseModel> predictibilityResults;
}
