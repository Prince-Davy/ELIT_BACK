package com.elit.outildedecision.shared.project.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

/** Project property key class. */
@Embeddable
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectPropertyKey implements Serializable {

  private static final long serialVersionUID = 1L;
  @Column(name = "project_id")
  private Long projectId;

  @Column(name = "user_id")
  private Long userId;

  /**
   * get project ID.
   * @return project Id
   */
  public Long getProjectId() {
    return projectId;
  }

  /**
   * set project ID.
   * @param projectId project Id
   */
  public void setProjectId(final Long projectId) {
    this.projectId = projectId;
  }
}
