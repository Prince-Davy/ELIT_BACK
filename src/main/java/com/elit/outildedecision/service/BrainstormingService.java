
package com.elit.outildedecision.service;

import com.elit.outildedecision.exception.BrainstormingNotFoundException;
import com.elit.outildedecision.exception.ExistingBrainstormingException;
import com.elit.outildedecision.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.model.BrainstormingModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

/**
 * This interface represents a service that handles brainstorming-related operations.
 */
public interface BrainstormingService {

  /**
   * Creates a new brainstorming with the given model.
   *
   * @param projectId the id of the projectId
   * @param brainstormingModel the model for the brainstorming
   * @throws PredictibilityAnalysisNotFoundException if there is an error creating the brainstorming
   * @throws ExistingBrainstormingException if there is an error creating the brainstorming
   * @return a ResponseEntity representing the result of the operation
   */
  ResponseEntity<String> createBrainstorming(Long projectId, BrainstormingModel brainstormingModel)
    throws PredictibilityAnalysisNotFoundException, ExistingBrainstormingException, ProjectNotFoundException;

  /**
   * Updates an existing brainstorming with the given model.
   *
   * @param projectId the id of the projectId
   * @param brainstormingModel the model for the brainstorming
   * @throws BrainstormingNotFoundException if there is an error updating the brainstorming
   * @return a ResponseEntity representing the result of the operation
   */
  ResponseEntity<String> updateBrainstorming(Long projectId, BrainstormingModel brainstormingModel)
    throws BrainstormingNotFoundException, ProjectNotFoundException;


  /**
   * Deletes the brainstorming with the given ID.
   * @param projectId the id of the project
   * @param brainstormingId the ID of the brainstorming to delete
   * @throws BrainstormingNotFoundException if the brainstorming with the given ID does not exist
   * @return a ResponseEntity representing the result of the operation
   */
  ResponseEntity<String> deleteBrainstorming(Long projectId, Long brainstormingId)
    throws BrainstormingNotFoundException, ProjectNotFoundException;

  /**
   * Validates or rejects the brainstorming with the given ID.
   *
   * @param projectId the id of the projectId
   * @param brainstormingId the ID of the brainstorming to validate or reject
   * @param isChecked an HttpEntity representing whether the brainstorming is validated or rejected
   * @throws JsonProcessingException if there is an error processing the validation status
   * @throws BrainstormingNotFoundException if the brainstorming with the given ID does not exist
   * @return a ResponseEntity representing the result of the operation
   */
  ResponseEntity<String> validateOrRejectBrainstorming(
    Long projectId, Long brainstormingId, HttpEntity<String> isChecked)
    throws JsonProcessingException, BrainstormingNotFoundException, ProjectNotFoundException;

  /**
   * Gets the full details of the brainstorming with the given ID.
   *
   * @param projectId the id of the project
   * @param predictibilityAnalysisId the ID of the predictibility analysis to get the brainstorming for
   * @throws PredictibilityAnalysisNotFoundException if there is an error getting the brainstorming
   * @return a ResponseEntity representing the result of the operation
   */
  ResponseEntity<Object> getBrainstorming(Long projectId, Long predictibilityAnalysisId) throws PredictibilityAnalysisNotFoundException, ProjectNotFoundException;


  /**
   * Gets a summary of the brainstorming with the given ID.
   *
   * @param projectId the id of the project
   * @param predictibilityAnalysisId the ID of the predictibility analysis to get the brainstorming for
   * @throws PredictibilityAnalysisNotFoundException if the predictibility analysis with the given ID does not exist
   * @return a ResponseEntity representing the result of the operation
   */
  ResponseEntity<Object> getBrainstormingAbstract(Long projectId, Long predictibilityAnalysisId) throws PredictibilityAnalysisNotFoundException, ProjectNotFoundException;
}
