package com.elit.outildedecision.shared.project.dto;

import com.elit.outildedecision.collaboration.multicriteria.dto.MulticriteriaAnalysisResponse;
import com.elit.outildedecision.ai.result.dto.ResultResponse;
import com.elit.outildedecision.shared.attachment.dto.AttachmentResponse;
import com.elit.outildedecision.shared.util.FormProgressStatus;
import com.elit.outildedecision.shared.util.ProjectState;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

/**
 * Response payload exposing the full information of a project: identity, timestamps,
 * confirmation state, chef, contributors, attachments and the related analyses.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ProjectResponse {

  /**
   * The ID of the project.
   */
  private Long projectId;

  /**
   * The name of the project.
   */
  private String name;

  /**
   * The description of the project.
   */
  private String description;

  /**
   * The creation timestamp of the project.
   */
  private Timestamp createdAt;

  /**
   * The update timestamp of the project.
   */
  private Timestamp updatedAt;

  /**
   * The confirmation state of the project.
   */
  private String confirmationState;

  /**
   * The ID of the chef associated with the project.
   */
  private Long chefId;

  /**
   * The lastname of the chef project owner.
   */
  private String chefLasName;

  /**
   * The firstname of the chef project owner.
   */
  private String chefFirstName;

  /**
   * The archived status of the project.
   */
  private Boolean isArchived;

  /**
   * The form type status of the project.
   */
  private FormProgressStatus formProgressStatus;

  /**
   * The current state of the project (e.g., In Progress, Completed, Pending).
   */
  private ProjectState projectState;

  /**
   * A list of contributors associated with the project.
   */
  private List<ContributorRequest> contributors;

  /**
   * A list of attachments associated with the project.
   */
  private List<AttachmentResponse> attachments;

  /**
   * A list of multicriteria analyse response models associated with the project.
   */
  private List<MulticriteriaAnalysisResponse> multiCriteriaAnalysisList;

  /**
   * The status of the multi criteria analysis.
   */
  private String multiCriteriaStatus;

  /**
   * The results of the A.I. analysis.
   */
  private ResultResponse resultAi;
}
