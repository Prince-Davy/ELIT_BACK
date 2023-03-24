package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents a method model containing information about a particular method within an execution plan.
 * It is used to store and manage data associated with methods, including method name and the number of votes received.
 * It uses Lombok annotations to generate constructors, getters, and setters.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MethodModel {

  /**
   * The name of the method.
   */
  private String name;

  /**
   * The number of votes received for the method.
   */
  private Integer votes;
}
