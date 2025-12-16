package com.elit.outildedecision.audit.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import com.elit.outildedecision.util.MethodType;

/**
 * Data Transfer Object representing a request to perform an audit.
 * Lombok annotations used:
 * - @Data: Generates getters, setters, equals, hashCode, and toString methods.
 * - @Builder: Enables the builder pattern for object creation.
 * - @AllArgsConstructor: Generates a constructor with all fields.
 * - @NoArgsConstructor: Generates a no-argument constructor.
 * Fields:
 * - projectId: The ID of the project to audit. This field is required.
 * - method: The methodology type used for the audit.
 * - auditId: Optional ID of an existing audit.
 * - questions: Optional list of questions for the audit questionnaire.
 *   Can be null for audits without custom questions.
 *   Supports methodology-agnostic question management.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditRequestModel {

  /**
   * The ID of the project to audit.
   * This field is required.
   */
  @NotNull(message = "L'ID du projet est obligatoire")
  private Long projectId;

  /**
   * The methodology type used for the audit.
   */
  private MethodType method;

  /**
   * Optional ID of an existing audit.
   */
  private Long auditId;

  /**
   * Optional list of questions for the audit questionnaire.
   * Can be null for audits without custom questions.
   * Supports methodology-agnostic question management.
   */
  @Valid
  private List<QuestionRequestModel> questions = new ArrayList<>();
}
