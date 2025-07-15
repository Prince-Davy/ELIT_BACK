package com.elit.outildedecision.features.audit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.ArrayList;

import com.elit.outildedecision.features.audit.question.QuestionRequestModel;
import com.elit.outildedecision.enumeration.MethodType;

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

  @NotNull(message = "La méthode est obligatoire")
  private MethodType method;

  /**
   * Optional list of questions for the audit questionnaire.
   * can be null for audits without
   * custom questions.
   * Supports methodology-agnostic question management.
   */
  @Valid
  private List<QuestionRequestModel> questions = new ArrayList<>();
}
