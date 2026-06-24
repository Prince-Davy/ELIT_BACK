package com.elit.outildedecision.collaboration.brainstorming.service;

import com.elit.outildedecision.collaboration.brainstorming.dto.BrainstormingRequest;
import com.elit.outildedecision.collaboration.brainstorming.exception.BrainstormingNotFoundException;
import com.elit.outildedecision.collaboration.brainstorming.exception.ExistingBrainstormingException;
import com.elit.outildedecision.collaboration.brainstorming.dto.FormStepModelForBrainstorming;
import com.elit.outildedecision.collaboration.predictibility.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpEntity;

import java.util.List;

/**
 * This interface represents a service that handles brainstorming-related operations.
 */
public interface BrainstormingService {

  /**
   * Creates a new brainstorming with the given request.
   *
   * @param projectId the id of the project
   * @param request the payload for the brainstorming
   * @return the id of the created brainstorming
   * @throws PredictibilityAnalysisNotFoundException if the predictibility analysis does not exist
   * @throws ExistingBrainstormingException if the user already submitted a brainstorming
   * @throws ProjectNotFoundException if the analysis does not belong to the project
   */
  Long createBrainstorming(Long projectId, BrainstormingRequest request)
    throws PredictibilityAnalysisNotFoundException, ExistingBrainstormingException, ProjectNotFoundException;

  /**
   * Updates an existing brainstorming with the given request.
   *
   * @param projectId the id of the project
   * @param request the payload for the brainstorming
   * @return the confirmation message; a
   *     {@link com.elit.outildedecision.shared.exception.ForbiddenException} (HTTP 403) is thrown
   *     when the requesting user is not the brainstorming author
   * @throws BrainstormingNotFoundException if the brainstorming does not exist
   * @throws ProjectNotFoundException if the brainstorming does not belong to the project
   */
  String updateBrainstorming(Long projectId, BrainstormingRequest request)
    throws BrainstormingNotFoundException, ProjectNotFoundException;

  /**
   * Deletes the brainstorming with the given id.
   *
   * @param projectId the id of the project
   * @param brainstormingId the id of the brainstorming to delete
   * @return the confirmation message
   * @throws BrainstormingNotFoundException if the brainstorming does not exist
   * @throws ProjectNotFoundException if the brainstorming does not belong to the project
   */
  String deleteBrainstorming(Long projectId, Long brainstormingId)
    throws BrainstormingNotFoundException, ProjectNotFoundException;

  /**
   * Validates or rejects the brainstorming with the given id.
   *
   * @param projectId the id of the project
   * @param brainstormingId the id of the brainstorming to validate or reject
   * @param isChecked an HttpEntity carrying the validation status
   * @return the confirmation message
   * @throws JsonProcessingException if there is an error processing the validation status
   * @throws BrainstormingNotFoundException if the brainstorming does not exist
   * @throws ProjectNotFoundException if the brainstorming does not belong to the project
   */
  String validateOrRejectBrainstorming(Long projectId, Long brainstormingId, HttpEntity<String> isChecked)
    throws JsonProcessingException, BrainstormingNotFoundException, ProjectNotFoundException;

  /**
   * Gets the brainstormings of a predictibility analysis grouped by form step.
   *
   * @param projectId the id of the project
   * @param predictibilityAnalysisId the id of the predictibility analysis
   * @return the form steps carrying their brainstormings
   * @throws PredictibilityAnalysisNotFoundException if the predictibility analysis does not exist
   * @throws ProjectNotFoundException if the analysis does not belong to the project
   */
  List<FormStepModelForBrainstorming> getBrainstorming(Long projectId, Long predictibilityAnalysisId)
    throws PredictibilityAnalysisNotFoundException, ProjectNotFoundException;

  /**
   * Gets the brainstorming abstract of a predictibility analysis grouped by form step.
   *
   * @param projectId the id of the project
   * @param predictibilityAnalysisId the id of the predictibility analysis
   * @return the form steps carrying their brainstormings
   * @throws PredictibilityAnalysisNotFoundException if the predictibility analysis does not exist
   * @throws ProjectNotFoundException if the analysis does not belong to the project
   */
  List<FormStepModelForBrainstorming> getBrainstormingAbstract(Long projectId, Long predictibilityAnalysisId)
    throws PredictibilityAnalysisNotFoundException, ProjectNotFoundException;

  /**
   * Gets the brainstorming abstract of a predictibility analysis from the analysis id alone,
   * resolving the owning project internally.
   *
   * @param predictibilityAnalysisId the id of the predictibility analysis
   * @return the form steps carrying their brainstormings
   * @throws PredictibilityAnalysisNotFoundException if the predictibility analysis does not exist
   */
  List<FormStepModelForBrainstorming> getBrainstormingAbstractByAnalysisId(Long predictibilityAnalysisId)
    throws PredictibilityAnalysisNotFoundException;
}
