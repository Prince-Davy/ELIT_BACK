package com.elit.outildedecision.audit.model;

import com.elit.outildedecision.util.MethodType;
import com.elit.outildedecision.util.QuestionCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

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
  private Long auditId;
  private MethodType method;
  private Long projectId;
  private String projectName;
  private Long userId;
  private String userFirstName;
  private String userLastName;
  private String userRole;

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
   * computed field : global percentage.
   */
  private double globalScorePercentage;

  /**
   * Score percentage per category.
   */
  @Builder.Default
  private Map<QuestionCategory, Double> categoryScores = new HashMap<>();

}
