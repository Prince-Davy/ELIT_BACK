package com.elit.outildedecision.model;

import com.elit.outildedecision.entity.Brainstorming;
import com.elit.outildedecision.entity.Criteria;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormQuestionModel implements Serializable {
    private Long questionId;
    private String questionText;
    private String answerText;
    private String questionRef;
    private Long stepId;
    private List<CriteriaModel> criterias;
    private List<BrainstormingModel> brainstormings;
}
