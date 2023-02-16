package com.elit.outildedecision.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(exclude = {"reviews"/*,"report"*/})
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    @NotEmpty(message = "Ce champs est obligatoire !")
    @Size(min = 3, message = "Le nom du projet doit contenir au moins 3 caractères")
    @Size(max = 20, message = "Le nom du projet ne doit pas dépasser 20 caractères")
    private String name;
    @Nullable
    private String description;

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

    @OneToMany(
            mappedBy = "project",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Attachment> attachments;

    @OneToMany(
            mappedBy = "project"
    )
    @JsonManagedReference("project_permissions")
    List<ProjectUserPermission> projectUserPermissions;

    @OneToMany(
            mappedBy = "project",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference("multiCriteria_project")
    private List<MultiCriteriaAnalysis> multiCriteriaAnalysisList;

    @OneToMany(
            mappedBy = "project",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference("report_project")
    private List<Report> reports;


    @OneToMany(
            mappedBy = "project",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference("notification_project")
    private List<Notification> notifications;

    public void addAttachment(Attachment attachment) {
        this.getAttachments().add(attachment);

    }

    @JsonManagedReference("project_attachment")
    public List<Attachment> getAttachments() {
        return this.attachments;
    }

    public void detachPermission(ProjectUserPermission projectUserPermission) {
        this.projectUserPermissions.remove(projectUserPermission);
        projectUserPermission.setProject(null);
    }

    @Override
    public String toString() {
        return "Project{"
                + "projectId=" + projectId
                + ", name='" + name + '\''
                + ", description='" + description + '\''
                + ", folderPath='" + folderPath + '\''
                + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt
                + '}';
    }

}
