package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
/**
 * The QuestionMappingModelKiviat class is an matrix with some answerIA questions with value for each answer.
 *  who give an note for making kiviat project model
 *

 * @see com.elit.outildedecision.entity.AnswerIa
 * @see ResponseModelKiviat
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionMappingModelKiviat {

  private Integer questionNumber;
  private String questionText;
  private ThemeModelKiviatId theme;
  private List<ResponseModelKiviat> responses;

}
