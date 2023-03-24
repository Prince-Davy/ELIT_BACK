package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents a predictibility results response model containing information about a predictibility results.
 * It is used to store and manage data associated with predictibility results, including name and value fields.
 * It uses Lombok annotations to generate constructors, getters, and setters.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PredictibilityResultsResponseModel {

  /**
   * The name associated with the predictibility results.
   */
  private String name;

  /**
   * The value associated with the predictibility results.
   */
  private Float value;
}
