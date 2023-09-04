
package com.elit.outildedecision.service;


import com.elit.outildedecision.exception.VoteAlreadyExistException;
import com.elit.outildedecision.exception.VoteNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.model.VoteModelRequest;
import com.elit.outildedecision.model.VoteModelResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * This interface defines the contract for the Vote Service, which provides methods to manage votes.
 */
public interface VoteService {

  /**
   * Creates a new vote for the specified predictability analysis.
   *
   * @param voteModelRequest a VoteModelRequest object representing the vote to create
   * @return a ResponseEntity indicating the success or failure of the vote creation operation
   * @throws VoteAlreadyExistException if a vote for the specified predictability analysis already exists
   */
  ResponseEntity<String> createVote(VoteModelRequest voteModelRequest)
    throws VoteAlreadyExistException;

  /**
   * Creates a new vote for the specified predictability analysis.
   *
   * @param projectId the project id.
   * @param voteModelRequest a VoteModelRequest object representing the vote to create
   * @return a ResponseEntity indicating the success or failure of the vote creation operation
   * @throws VoteAlreadyExistException if a vote for the specified predictability analysis already exists
   */
  ResponseEntity<String> createVote(Long projectId, VoteModelRequest voteModelRequest)
    throws VoteAlreadyExistException, ProjectNotFoundException, PredictibilityAnalysisNotFoundException;
  /**
   * Retrieves the votes for the specified predictability analysis.
   *
   * @param predictibilityAnalysisId a Long representing the ID of the predictibility analysis to retrieve the votes for
   * @return a List of VoteModelResponse objects representing the votes for the specified predictibility analysis
   * @throws UserNotFoundException if a user associated with one of the votes cannot be found
   */
  List<VoteModelResponse> getVote(Long predictibilityAnalysisId) throws UserNotFoundException;
  /**
   * Retrieves the votes for the specified predictability analysis.
   *
   * @param projectId project id.
   * @param predictibilityAnalysisId a Long representing the ID of the predictibility analysis to retrieve the votes for
   * @return a List of VoteModelResponse objects representing the votes for the specified predictibility analysis
   * @throws UserNotFoundException if a user associated with one of the votes cannot be found
   */
  List<VoteModelResponse> getVote(Long projectId, Long predictibilityAnalysisId) throws ProjectNotFoundException, PredictibilityAnalysisNotFoundException;
  /**
   * Updates an existing vote.
   *
   * @param voteId a Long representing the ID of the vote to update
   * @param voteModelRequest a VoteModelRequest object representing the updated vote information
   * @return a ResponseEntity indicating the success or failure of the vote update operation
   */
  ResponseEntity<String> updateVote(Long voteId, VoteModelRequest voteModelRequest);
  /**
   * Updates an existing vote.
   *
   * @param projectId project id.
   * @param voteId a Long representing the ID of the vote to update
   * @param voteModelRequest a VoteModelRequest object representing the updated vote information
   * @return a ResponseEntity indicating the success or failure of the vote update operation
   */
  ResponseEntity<String> updateVote(Long projectId, Long voteId, VoteModelRequest voteModelRequest) throws ProjectNotFoundException, VoteNotFoundException;

}

