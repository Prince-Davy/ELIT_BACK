package com.elit.outildedecision.model;

import com.elit.outildedecision.entity.FormQuestion;
import com.elit.outildedecision.entity.StepScore;
import com.elit.outildedecision.entity.StepWeight;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormStepModelForBrainstorming {
    private Long stepId;
    private String stepName;
    private Long stepRef;
    private Float eScore;
    private Long multiCriteriaAnalysisId;
    private List<FormQuestionModel> formQuestions;
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
