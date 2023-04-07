package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents an execution vote model containing information about a user's vote in an execution plan.
 * It is used to store and manage data associated with users' votes in execution plans, including user and vote identifiers.
 * It uses Lombok annotations to generate constructors, getters, and setters.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionVoteModel {

  /**
   * A unique identifier for the vote.
   */
  private Long id;

  /**
   * A unique identifier for the user who cast the vote.
   */
  private Long userId;
}
