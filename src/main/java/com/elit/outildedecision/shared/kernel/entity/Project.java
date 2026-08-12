package com.elit.outildedecision.shared.kernel.entity;

import com.elit.outildedecision.ai.methodology.evaluation.entity.MethodologyEvaluation;
import com.elit.outildedecision.ai.result.entity.ResultAi;
import com.elit.outildedecision.collaboration.multicriteria.entity.MultiCriteriaAnalysis;
import com.elit.outildedecision.collaboration.report.entity.Report;
import com.elit.outildedecision.shared.attachment.entity.Attachment;
import com.elit.outildedecision.shared.config.View;
import com.elit.outildedecision.shared.project.entity.Notification;
import com.elit.outildedecision.shared.project.entity.ProjectUserPermission;
import com.elit.outildedecision.audit.entity.Audit;
import com.elit.outildedecision.shared.util.FormProgressStatus;
import com.elit.outildedecision.shared.util.ProjectState;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/** Project class. */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "project")
public class Project implements Serializable {

  /** Serialization version identifier. */
  private static final long serialVersionUID = 1L;
  /** The project user permissions. */
  @OneToMany(mappedBy = "project")
  @JsonManagedReference("project_permissions")
  private List<ProjectUserPermission> projectUserPermissions;

  /** The project id. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonView(View.AnswerIa.class)
  private Long projectId;

  /** The name. */
  @NotEmpty(message = "Ce champs est obligatoire !")
  @Size(min = 3, message = "Le nom du projet doit contenir au moins 3 caractères")
  @Size(max = 20, message = "Le nom du projet ne doit pas dépasser 20 caractères")
  private String name;

  /** The description. */
  @Size(min = 3, message = "La description du projet doit contenir au moins 3 caractères")
  @Size(max = 255, message = "La description du projet ne doit pas dépasser 255 caractères")
  @Pattern(regexp = "^[^\\r\\n]*(?:\\r?\\n[^\\r\\n]*){0,4}$", message = "La description du projet ne doit pas dépasser 5 lignes")
  private String description;
  /** The folder path. */
  private String folderPath;
  /** The chef id. */
  private Long chefId;
  /** The confirmation state. */
  private String confirmationState;
  /** Whether the archived flag is set. */
  private Boolean isArchived;
  /** The form progress status. */
  @Enumerated(EnumType.STRING)
  private FormProgressStatus formProgressStatus;
  /** The project state. */
  @Enumerated(EnumType.STRING)
  private ProjectState projectState;

  /** The created at. */
  @CreationTimestamp
  @JsonFormat(pattern = "dd-MM-yyyy", timezone = "Europe/Paris")
  private Timestamp createdAt;

  /** The updated at. */
  @UpdateTimestamp
  @JsonFormat(pattern = "dd-MM-yyyy", timezone = "Europe/Paris")
  private Timestamp updatedAt;

  /** The attachments. */
  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Attachment> attachments;

  /** The multi criteria analysis list. */
  @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("multiCriteria_project")
  private List<MultiCriteriaAnalysis> multiCriteriaAnalysisList;

  /** The reports. */
  @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("report_project")
  private List<Report> reports;

  /** The notifications. */
  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("notification_project")
  private List<Notification> notifications;

  /** The audits. */
  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @JsonManagedReference("audit_project")
  private List<Audit> audits;

  /**
   * AI analysis results produced for the project. Inverse side only: the {@code ai} context owns
   * the lifecycle of a {@link ResultAi}, this mapping merely lets the shared kernel tell whether
   * the AI journey has produced a result.
   */
  // S1948: JPA relation never Java-serialized, observable state is exposed through DTOs.
  @SuppressWarnings("java:S1948")
  @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
  private List<ResultAi> aiResults;

  /**
   * list of methodology evaluations associated with the project.
   */
  // S1948: JPA relation never Java-serialized, observable state is exposed through DTOs.
  @SuppressWarnings("java:S1948")
  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @JsonManagedReference("methodology_evaluations_project")
  private List<MethodologyEvaluation> methodologyEvaluations;

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
