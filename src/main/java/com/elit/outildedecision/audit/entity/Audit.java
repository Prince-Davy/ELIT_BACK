package com.elit.outildedecision.audit.entity;

import com.elit.outildedecision.shared.kernel.entity.Project;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.util.MethodType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.CascadeType;
import jakarta.persistence.GenerationType;
import jakarta.persistence.FetchType;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;

/**
 * Audit entity to track project activities.
 */
@Entity
@Table(name = "audit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Audit implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long auditId;
  /**
   * Methodology used for the audit.
   * Allows extending functionality without modifying core audit logic.
   */
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  @ToString.Include
  private MethodType method;

  /**
   * Date and time when the audit was launched.
   * Useful for tracking audit timelines.
   */
  @CreationTimestamp
  @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Europe/Paris")
  @ToString.Include
  private Timestamp auditLaunchDate;

  /**
   * Project associated with this audit.
   * Allows linking audit results to specific projects.
   */
  @ManyToOne(optional = false)
  @JoinColumn(name = "project_id", referencedColumnName = "projectId")
  @JsonBackReference("audit_project")
  private Project project;

  /**
   * User who initiated the audit.
   * Allows tracking who performed the audit.
   */
  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", referencedColumnName = "userId")
  @JsonBackReference("audit_user")
  private User user;

  /**
   * List of questions associated with this audit.
   * allows extending questionnaire functionality
   * without modifying the core audit logic.
   */
  @OneToMany(mappedBy = "audit", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @JsonManagedReference("audit_questions")
  @Builder.Default
  private List<Question> questions = new ArrayList<>();

  /**
   * Audit Score associated with this audit.
   */
  @OneToOne(mappedBy = "audit", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @JsonManagedReference("audit_auditScore")
  private AuditScore auditScore;
}
