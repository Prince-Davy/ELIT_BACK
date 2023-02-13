package com.elit.outildedecision.model;

import com.elit.outildedecision.entity.ExecutionPlanVote;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionVoteResponseModel {
    private String methodologyName;
    private Long id;
    private String methodName;
    private String methodDescription;
    private String disadvantages;
    private String advantages;
    private List<ExecutionVoteModel> votes;
}
