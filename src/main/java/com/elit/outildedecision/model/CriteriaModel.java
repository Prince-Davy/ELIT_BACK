package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * This class represents a criteria model containing information about a specific evaluation criterion.
 * It is used to store and manage data associated with criteria used to evaluate questions or other project elements.
 * The class implements Serializable interface to allow its instances to be converted to a byte stream.
 * It uses Lombok annotations to generate constructors, getters, and setters.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CriteriaModel implements Serializable {

  /**
   * A unique identifier for the evaluation criterion.
   */
  private Long criteriaId;

  /**
   * The name of the evaluation criterion.
   */
  private String criteriaName;

  /**
   * The value or weight assigned to the evaluation criterion.
   */
  private int criteriaValue;

  /**
   * A unique identifier for the associated question.
   */
  private Long questionId;
}
