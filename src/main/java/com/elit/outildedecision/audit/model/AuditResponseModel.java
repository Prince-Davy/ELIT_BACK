package com.elit.outildedecision.audit.model;

import com.elit.outildedecision.util.MethodType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;

/**
 * Model for audit response.
 * Enhanced to include questionnaire data following Open/Closed Principle.
 * Supports methodology-agnostic question presentation and analytics.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditResponseModel {

  /**
   * The unique identifier of the audit.
   */
  private Long auditId;

  /**
   * The methodology type used in the audit.
   */
  private MethodType method;

  /**
   * The ID of the associated project.
   */
  private Long projectId;

  /**
   * The name of the associated project.
   */
  private String projectName;

  /**
   * The ID of the user linked to the audit.
   */
  private Long userId;

  /**
   * The first name of the user.
   */
  private String userFirstName;

  /**
   * The last name of the user.
   */
  private String userLastName;

  /**
   * The role of the user in the audit or project.
   */
  private String userRole;

  /**
   * the date of the audit creation.
   */
  @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Europe/Paris")
  private Timestamp auditLaunchDate;

  /**
   * List of questions associated with this audit.
   * Following Single Responsibility Principle - provides complete audit
   * information.
   * Supports methodology-specific question analysis.
   */
  @Builder.Default
  private List<QuestionResponseModel> questions = new ArrayList<>();

  /**
   * Computed field: total number of questions in the audit.
   * Useful for analytics and summary displays.
   */
  private Integer totalQuestions;

  /**
   * Computed field: total weight of all questions.
   * Supports scoring and importance analysis.
   */
  private Integer totalWeight;

  /**
   * Computed field: total number of audits associated with this project.
   * Useful for project-level analytics and reporting.
   */
  private Long totalAuditCount;

  /**
   * Score percentage per category.
   */
  @Builder.Default
  private List<AuditCategoryResultDTO> categoryResults = new ArrayList<>();

}
