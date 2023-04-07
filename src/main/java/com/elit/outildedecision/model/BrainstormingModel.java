package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * This class represents a brainstorming model containing information about a brainstorming session.
 * It is used to store and manage brainstorming data associated with a specific question and user.
 * It uses Lombok annotations to generate constructors, getters, and setters.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrainstormingModel implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * A unique identifier for the brainstorming session.
   */
  private Long brainstormingId;

  /**
   * The text content generated during the brainstorming session.
   */
  private String brainstormingText;

  /**
   * A boolean flag indicating whether the brainstorming session has been checked or not.
   */
  private Boolean isChecked;

  /**
   * A unique identifier for the associated question.
   */
  private Long questionId;

  /**
   * A unique identifier for the predictability analysis associated with the brainstorming session.
   */
  private Long predictibilityAnalysisId;

  /**
   * A unique identifier for the user who initiated the brainstorming session.
   */
  private Long userId;

  /**
   * The first name of the user who initiated the brainstorming session.
   */
  private String firstName;

  /**
   * The last name of the user who initiated the brainstorming session.
   */
  private String lastName;

  /**
   * The role of the user who initiated the brainstorming session.
   */
  private String role;
}
