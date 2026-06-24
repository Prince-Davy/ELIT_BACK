package com.elit.outildedecision.collaboration.multicriteria.dto;

import com.elit.outildedecision.collaboration.predictibility.dto.PredictibilityAnalyseResponseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * Response payload exposing the latest multi-criteria analysis of a project: its identifiers,
 * timestamps, iteration, completion flag, form steps and the optional predictibility analysis.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class MulticriteriaAnalysisResponse implements Serializable {

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
  private List<FormStepResponse> formSteps;

  /**
   * The predictibility analysis response model associated with the multi-criteria analysis.
   */
  private PredictibilityAnalyseResponseModel predictibilityAnalysis;
}
