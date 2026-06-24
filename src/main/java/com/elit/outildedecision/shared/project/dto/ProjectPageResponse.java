package com.elit.outildedecision.shared.project.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

/**
 * Response payload wrapping a page of projects together with the total number of projects
 * created by and affected to the user.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProjectPageResponse {
  /**
   * The current page of projects.
   */
  private Page<ProjectResponse> projectsPage;
  /**
   * The total number of projects created by the user.
   */
  private long totalProjetCreated;
  /**
   * The total number of projects affected to the user.
   */
  private long totalProjetAffected;
}
