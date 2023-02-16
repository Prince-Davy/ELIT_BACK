package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormStepModel {
    private Long stepId;
    private String stepName;
    private Long stepRef;
    private Float eScore;
    private Long multiCriteriaAnalysisId;
    /*@Override
    public String toString() {
        return "FormStep{" +
                "stepId=" + stepId +
                ", stepName='" + stepName + '\'' +
                ", stepRef='" + stepRef + '\'' +
                ", eScore='" + eScore + '\'' +
                ", multiCriteriaAnalysisId='" + multiCriteriaAnalysisId + '\'' +
                '}';
    }*/
}
