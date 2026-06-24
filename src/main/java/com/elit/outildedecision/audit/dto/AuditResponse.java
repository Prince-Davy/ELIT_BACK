package com.elit.outildedecision.audit.dto;

import com.elit.outildedecision.shared.util.MethodType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Response payload exposing an audit, its questions and aggregated analytics.
 * Supports methodology-agnostic question presentation.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditResponse {

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
   * The date of the audit creation.
   */
  @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Europe/Paris")
  private Timestamp auditLaunchDate;

  /**
   * List of questions associated with this audit.
   * Supports methodology-specific question analysis.
   */
  @Builder.Default
  private List<QuestionResponse> questions = new ArrayList<>();

  /**
   * Computed field: total number of questions in the audit.
   */
  private Integer totalQuestions;

  /**
   * Computed field: total weight of all questions.
   */
  private Integer totalWeight;

  /**
   * Computed field: total number of audits associated with this project.
   */
  private Long totalAuditCount;

  /**
   * Score percentage per category.
   */
  @Builder.Default
  private List<AuditCategoryResultResponse> categoryResults = new ArrayList<>();

}
