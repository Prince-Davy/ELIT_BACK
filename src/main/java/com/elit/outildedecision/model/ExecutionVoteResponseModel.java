package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * This class represents an execution vote response model containing information about the results of an execution vote.
 * It is used to store and manage data associated with the execution vote results, such as methodology name, method details, and a list of votes.
 * It uses Lombok annotations to generate constructors, getters, and setters.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionVoteResponseModel {

  /**
   * The name of the methodology associated with the vote.
   */
  private String methodologyName;

  /**
   * A unique identifier for the vote result.
   */
  private Long id;

  /**
   * The name of the method being voted on.
   */
  private String methodName;

  /**
   * The description of the method being voted on.
   */
  private String methodDescription;

  /**
   * The disadvantages of the method being voted on.
   */
  private String disadvantages;

  /**
   * The advantages of the method being voted on.
   */
  private String advantages;

  /**
   * A list of execution vote models containing information about individual votes.
   */
  private List<ExecutionVoteModel> votes;
}
