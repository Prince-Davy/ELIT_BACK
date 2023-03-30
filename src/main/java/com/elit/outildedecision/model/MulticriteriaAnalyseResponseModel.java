package com.elit.outildedecision.model;

import com.elit.outildedecision.entity.FormStep;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * This class represents a multi-criteria analysis response model containing information about a multi-criteria analysis within an execution plan.
 * It is used to store and manage data associated with multi-criteria analyses, including analysis ID, creation and update timestamps, analysis iteration, completion status, form steps, and predictability analysis information.
 * It uses Lombok annotations to generate constructors, getters, and setters and implements Serializable for compatibility with serialization.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MulticriteriaAnalyseResponseModel implements Serializable {

  /**
   * A unique identifier for the multi-criteria analysis.
   */
  private Long multiCriteriaAnalysisId;

  /**
   * The timestamp when the multi-criteria analysis was created.
   */
  private Timestamp createdAt;

  /**
   * The timestamp when the multi-criteria analysis was last updated.
   */
  private Timestamp updatedAt;

  /**
   * The current iteration of the multi-criteria analysis.
   */
  private Integer multiCriteriaAnalysisIteration;

  /**
   * A boolean value indicating whether the multi-criteria analysis is finished or not.
   */
  private Boolean isFinished;

  /**
   * A list of form steps associated with the multi-criteria analysis.
   */
  private List<FormStep> formSteps;

  /**
   * The predictability analysis response model associated with the multi-criteria analysis.
   */
  private PredictibilityAnalyseResponseModel predictibilityAnalysis;
}
