package com.elit.outildedecision.model;

import com.elit.outildedecision.entity.FormStep;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PredictibilityAnalyseResponseModel implements Serializable {
    private Long predictibilityAnalysisId;
    private Boolean isFinished;
    private Integer predictibilityAnalysisIteration;
    private ExecutionPlanScoreResponseModel executionPlan;
}
