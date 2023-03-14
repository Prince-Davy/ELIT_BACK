package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity
@Table(name = "project_user_permission")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties("id")
public class ProjectUserPermission {
  @EmbeddedId
  private ProjectPropertyKey id;
  private String permission;

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
}
