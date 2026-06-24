package com.elit.outildedecision.shared.project.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request payload carrying the filter, sort and pagination criteria used to list the
 * projects of a user.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProjectFilterRequest {
  /**
   * The keyword to filter the projects by name or description.
   */
  private String key = "";
  /**
   * The project state to filter by.
   */
  private String status;
  /**
   * The sort criteria.
   */
  private String sort;
  /**
   * Whether the active (created) tab is selected.
   */
  private boolean isActiveTab;
  /**
   * The requested page index.
   */
  private int page;
  /**
   * The requested page size.
   */
  private int size;
  /**
   * The analysis type to filter by.
   */
  private String typeAnalyse;
}
