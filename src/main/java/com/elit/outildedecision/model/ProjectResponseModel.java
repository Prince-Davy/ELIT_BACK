package com.elit.outildedecision.model;

import com.elit.outildedecision.util.FormProgressStatus;
import com.elit.outildedecision.util.ProjectState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

/**
 * This class represents a project response model containing information about a
 * project.
 * It is used to store and manage data associated with projects, including name,
 * description,
 * creation and update timestamps, confirmation state, chef ID, archived status,
 * a list of contributors,
 * a list of attachments, and a list of multicriteria analyses.
 * It uses Lombok annotations to generate constructors, getters, and setters.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponseModel {

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
   * A list of contributor models associated with the project.
   */
  private List<ContributerModel> contributors;

  /**
   * A list of attachment response models associated with the project.
   */
  private List<AttachmentResponseModel> attachments;

  /**
   * A list of multicriteria analyse response models associated with the project.
   */
  private List<MulticriteriaAnalyseResponseModel> multiCriteriaAnalysisList;

  /**
   * The status of the multi criteria analysis.
   */
  private String multiCriteriaStatus;

  /**
   * The results of the A.I. analysis
   */
  private ResultAiModel resultAi;
}
