package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.lang.Nullable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "project")
@EqualsAndHashCode(exclude = {"reviews" /* ,"report" */})
public class Project {
  @OneToMany(mappedBy = "project")
  @JsonManagedReference("project_permissions")
  private List<ProjectUserPermission> projectUserPermissions;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long projectId;

  @NotEmpty(message = "Ce champs est obligatoire !")
  @Size(min = 3, message = "Le nom du projet doit contenir au moins 3 caractères")
  @Size(max = 20, message = "Le nom du projet ne doit pas dépasser 20 caractères")
  private String name;

  @Nullable private String description;
  private String folderPath;
  private Long chefId;
  private String confirmationState;
  private Boolean isArchived;

  @CreationTimestamp
  @JsonFormat(pattern = "dd-MM-yyyy", timezone = "Europe/Paris")
  private Timestamp createdAt;

  @UpdateTimestamp
  @JsonFormat(pattern = "dd-MM-yyyy", timezone = "Europe/Paris")
  private Timestamp updatedAt;

  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Attachment> attachments;

  @OneToMany(
      mappedBy = "project",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @JsonManagedReference("multiCriteria_project")
  private List<MultiCriteriaAnalysis> multiCriteriaAnalysisList;

  @OneToMany(
      mappedBy = "project",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @JsonManagedReference("report_project")
  private List<Report> reports;

  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("notification_project")
  private List<Notification> notifications;

  /**
   * get the list of attachments of the project.
   * @return list of attachments
   */
  @JsonManagedReference("project_attachment")
  public List<Attachment> getAttachments() {
    return this.attachments;
  }
  /**
   * return the project as a string.
   * @return project as a string
   */
  @Override
  public String toString() {
    return "Project{"
        + "projectId="
        + projectId
        + ", name='"
        + name
        + '\''
        + ", description='"
        + description
        + '\''
        + ", folderPath='"
        + folderPath
        + '\''
        + ", createdAt="
        + createdAt
        + ", updatedAt="
        + updatedAt
        + '}';
  }
}
