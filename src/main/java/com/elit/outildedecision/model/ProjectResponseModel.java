package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * This class represents a project response model containing information about a project.
 * It is used to store and manage data associated with projects, including name, description,
 * creation and update timestamps, confirmation state, chef ID, archived status, a list of contributors,
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
   * The archived status of the project.
   */
  private Boolean isArchived;

  /**
   * A list of contributor models associated with the project.
   */
  private ArrayList<ContributerModel> contributors;

  /**
   * A list of attachment response models associated with the project.
   */
  private ArrayList<AttachmentResponseModel> attachments;

  /**
   * A list of multicriteria analyse response models associated with the project.
   */
  private ArrayList<MulticriteriaAnalyseResponseModel> multiCriteriaAnalysisList;

  /**
   * The status of the multi criteria analysis.
   */
  private String multiCriteriaStatus;
}
