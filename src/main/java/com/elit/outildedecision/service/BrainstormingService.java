
package com.elit.outildedecision.service;

import com.elit.outildedecision.exception.BrainstormingNotFoundException;
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
   * @param brainstormingModel the model for the brainstorming
   * @throws Exception if there is an error creating the brainstorming
   * @return a ResponseEntity representing the result of the operation
   */
  ResponseEntity<String> createBrainstorming(BrainstormingModel brainstormingModel)
    throws Exception;

  /**
   * Updates an existing brainstorming with the given model.
   *
   * @param brainstormingModel the model for the brainstorming
   * @throws Exception if there is an error updating the brainstorming
   * @return a ResponseEntity representing the result of the operation
   */
  ResponseEntity<String> updateBrainstorming(BrainstormingModel brainstormingModel)
    throws Exception;

  /**
   * Deletes the brainstorming with the given ID.
   *
   * @param brainstormingId the ID of the brainstorming to delete
   * @throws BrainstormingNotFoundException if the brainstorming with the given ID does not exist
   * @return a ResponseEntity representing the result of the operation
   */
  ResponseEntity<String> deleteBrainstorming(Long brainstormingId)
    throws BrainstormingNotFoundException;

  /**
   * Validates or rejects the brainstorming with the given ID.
   *
   * @param brainstormingId the ID of the brainstorming to validate or reject
   * @param isChecked an HttpEntity representing whether the brainstorming is validated or rejected
   * @throws JsonProcessingException if there is an error processing the validation status
   * @throws BrainstormingNotFoundException if the brainstorming with the given ID does not exist
   * @return a ResponseEntity representing the result of the operation
   */
  ResponseEntity<String> validateOrRejectBrainstorming(
    Long brainstormingId, HttpEntity<String> isChecked)
    throws JsonProcessingException, BrainstormingNotFoundException;

  /**
   * Gets the full details of the brainstorming with the given ID.
   *
   * @param predictibilityAnalysisId the ID of the predictibility analysis to get the brainstorming for
   * @throws Exception if there is an error getting the brainstorming
   * @return a ResponseEntity representing the result of the operation
   */
  ResponseEntity<Object> getBrainstorming(Long predictibilityAnalysisId) throws Exception;

  /**
   * Gets a summary of the brainstorming with the given ID.
   *
   * @param predictibilityAnalysisId the ID of the predictibility analysis to get the brainstorming for
   * @throws Exception if there is an error getting the brainstorming summary
   * @return a ResponseEntity representing the result of the operation
   */
  ResponseEntity<Object> getBrainstormingAbstract(Long predictibilityAnalysisId) throws Exception;
}
