package com.elit.outildedecision.ai.result.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Question mapping model class. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionMappingModel {

  private String id;
  private String questionText;
  private Integer questionNumber;
  private List<ResponseModel> responses;

}
