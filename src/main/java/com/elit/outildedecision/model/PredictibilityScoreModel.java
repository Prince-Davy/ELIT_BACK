package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictibilityScoreModel {
  private Float average;
  private ArrayList<MethodologyScoreModel> methodologies;
  private List<FormStepModel> formSteps;
  private Integer predictibilityIteration;
}
