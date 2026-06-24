package com.elit.outildedecision.shared.project.entity;

import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.io.Serializable;

/** Project user permission class. */
@Entity
@Table(name = "project_user_permission")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties("id")
public class ProjectUserPermission implements Serializable {
  @EmbeddedId
  private ProjectPropertyKey id;
  private String permission;

  private String userFunction;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @MapsId("projectId")
  @JoinColumn(name = "project_id", referencedColumnName = "projectId", nullable = true)
  @JsonBackReference("project_permissions")
  private Project project;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @MapsId("userId")
  @JoinColumn(name = "user_id", nullable = true)
  @JsonBackReference("user_permissions")
  private User user;

  /**
   * get permission.
   * @return permission
   */
  public String getPermission() {
    return permission;
  }

  /**
   * set permission.
   * @param permission permission
   */
  public void setPermission(final String permission) {
    this.permission = permission;
  }
}
