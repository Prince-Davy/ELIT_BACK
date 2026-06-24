package com.elit.outildedecision.collaboration.brainstorming.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Outbound representation of a brainstorming entry, including its author details.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class BrainstormingResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * A unique identifier for the brainstorming entry.
   */
  private Long brainstormingId;

  /**
   * The text content of the brainstorming idea.
   */
  private String brainstormingText;

  /**
   * Indicates whether the brainstorming idea has been marked as checked.
   */
  private Boolean isChecked;

  /**
   * A unique identifier for the associated question.
   */
  private Long questionId;

  /**
   * A unique identifier for the predictability analysis associated with the brainstorming.
   */
  private Long predictibilityAnalysisId;

  /**
   * A unique identifier for the user who initiated the brainstorming entry.
   */
  private Long userId;

  /**
   * The first name of the user who initiated the brainstorming entry.
   */
  private String firstName;

  /**
   * The last name of the user who initiated the brainstorming entry.
   */
  private String lastName;

  /**
   * The role of the user who initiated the brainstorming entry.
   */
  private String role;
}
