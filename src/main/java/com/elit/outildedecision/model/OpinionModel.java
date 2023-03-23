package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents an opinion model containing information about an opinion, such as rating and description.
 * It is used to store and manage data associated with opinions, including rating and description.
 * It uses Lombok annotations to generate constructors, getters, and setters.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpinionModel {

  /**
   * The rating associated with the opinion.
   */
  private Integer rating;

  /**
   * The description associated with the opinion.
   */
  private String description;
}
