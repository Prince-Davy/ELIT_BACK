package com.elit.outildedecision.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import com.elit.outildedecision.shared.util.MethodType;

/**
 * Request payload used to create or update an audit and its questions.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditRequest {

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
  private List<QuestionRequest> questions = new ArrayList<>();
}
