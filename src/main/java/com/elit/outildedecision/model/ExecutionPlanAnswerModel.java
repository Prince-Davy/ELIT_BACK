package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class  ExecutionPlanAnswerModel {
    private Long executionPlanQuestionId;
    private String answer;
}
