package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultAiModel {

  private Long id;

  private String predictedMethod;

  private Double predictedProba;

  private Long projectId;

}
