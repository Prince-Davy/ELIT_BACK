package com.elit.outildedecision.entity;

import com.elit.outildedecision.config.View;
import com.elit.outildedecision.audit.entity.Audit;
import com.elit.outildedecision.util.FormProgressStatus;
import com.elit.outildedecision.util.ProjectState;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "project")
public class Project implements Serializable {

  private static final long serialVersionUID = 1L;
  @OneToMany(mappedBy = "project")
  @JsonManagedReference("project_permissions")
  private List<ProjectUserPermission> projectUserPermissions;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonView(View.AnswerIa.class)
  private Long projectId;

  @NotEmpty(message = "Ce champs est obligatoire !")
  @Size(min = 3, message = "Le nom du projet doit contenir au moins 3 caractères")
  @Size(max = 20, message = "Le nom du projet ne doit pas dépasser 20 caractères")
  private String name;

  @Size(min = 3, message = "La description du projet doit contenir au moins 3 caractères")
  @Size(max = 255, message = "La description du projet ne doit pas dépasser 255 caractères")
  private String description;
  private String folderPath;
  private Long chefId;
  private String confirmationState;
  private Boolean isArchived;
  @Enumerated(EnumType.STRING)
  private FormProgressStatus formProgressStatus;
  @Enumerated(EnumType.STRING)
  private ProjectState projectState;

  @CreationTimestamp
  @JsonFormat(pattern = "dd-MM-yyyy", timezone = "Europe/Paris")
  private Timestamp createdAt;

  @UpdateTimestamp
  @JsonFormat(pattern = "dd-MM-yyyy", timezone = "Europe/Paris")
  private Timestamp updatedAt;

  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Attachment> attachments;

  @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("multiCriteria_project")
  private List<MultiCriteriaAnalysis> multiCriteriaAnalysisList;

  @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("report_project")
  private List<Report> reports;

  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("notification_project")
  private List<Notification> notifications;

  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @JsonManagedReference("audit_project")
  private List<Audit> audits;

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
