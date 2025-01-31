package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionMappingModel {

  private String id;
  private String questionText;
  private Integer questionNumber;
  private List<ResponseModel> responses;

}
