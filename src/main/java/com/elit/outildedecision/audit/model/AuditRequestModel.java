package com.elit.outildedecision.audit.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import com.elit.outildedecision.util.MethodType;

/**
 * Model for audit creation request.
 * Allows methodology-specific question sets while maintaining backward
 * compatibility.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditRequestModel {

  @NotNull(message = "L'ID du projet est obligatoire")
  private Long projectId;

  private MethodType method;

  private Long auditId;

  /**
   * Optional list of questions for the audit questionnaire.
   * can be null for audits without
   * custom questions.
   * Supports methodology-agnostic question management.
   */
  @Valid
  private List<QuestionRequestModel> questions = new ArrayList<>();
}
