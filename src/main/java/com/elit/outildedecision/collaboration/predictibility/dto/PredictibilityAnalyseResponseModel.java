package com.elit.outildedecision.collaboration.predictibility.dto;

import com.elit.outildedecision.collaboration.brainstorming.dto.BrainstormingSummaryResponse;
import com.elit.outildedecision.collaboration.executionplan.dto.ExecutionPlanScoreResponse;
import com.elit.outildedecision.collaboration.vote.dto.VoteSummaryResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * This class represents a predictibility analysis response model containing information about a predictibility analysis.
 * It is used to store and manage data associated with predictibility analyses, including ID, finished status, iteration number, and associated execution plan.
 * It uses Lombok annotations to generate constructors, getters, and setters.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PredictibilityAnalyseResponseModel implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * The ID associated with the predictibility analysis.
   */
  private Long predictibilityAnalysisId;

  /**
   * The finished status of the predictibility analysis.
   */
  private Boolean isFinished;

  /**
   * The iteration number associated with the predictibility analysis.
   */
  private Integer predictibilityAnalysisIteration;

  /**
   * The final choosen méthodology.
   */
  private String finalChosenMethodology;

  /**
   * The list of the brainstormings.
   */
  private List<BrainstormingSummaryResponse> brainstormings;

  /**
   * The list of the votes.
   */
  private List<VoteSummaryResponse> votes;

  /**
   * The execution plan associated with the predictibility analysis.
   */
  private ExecutionPlanScoreResponse executionPlan;
}
