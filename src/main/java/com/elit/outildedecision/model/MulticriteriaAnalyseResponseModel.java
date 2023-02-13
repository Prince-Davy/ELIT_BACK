package com.elit.outildedecision.model;

import com.elit.outildedecision.entity.FormStep;
import com.elit.outildedecision.entity.PredictibilityAnalysis;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor @NoArgsConstructor
public class MulticriteriaAnalyseResponseModel implements Serializable {
    private Long multiCriteriaAnalysisId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Integer multiCriteriaAnalysisIteration;
    private Boolean isFinished;
    //TODO: change to FormStepResponseModel
    private List<FormStep> formSteps;
    private PredictibilityAnalyseResponseModel predictibilityAnalysis;

}
