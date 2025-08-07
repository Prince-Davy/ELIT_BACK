package com.elit.outildedecision.audit.entity;

import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.util.MethodType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;

/**
 * Audit entity to track project activities.
 */
@Entity
@Table(name = "audit")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Audit implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long auditId;
  /**
   * Methodology used for the audit.
   * Allows extending functionality without modifying core audit logic.
   */
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private MethodType method;

  /**
   * Date and time when the audit was launched.
   * Useful for tracking audit timelines.
   */
  @CreationTimestamp
  @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Europe/Paris")
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
}
