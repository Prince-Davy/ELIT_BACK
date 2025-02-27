package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * The ResponseModelKiviat class is a response part of KiviatProjectMatrixJson for get the note.
 * from an answerIA
 *
 * @see com.elit.outildedecision.entity.AnswerIa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseModelKiviat {

  private ResponseModelId id;
  private String label;
  private Integer note;
}
