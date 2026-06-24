package com.elit.outildedecision.collaboration.vote.service;

import com.elit.outildedecision.collaboration.vote.dto.VoteRequest;
import com.elit.outildedecision.collaboration.vote.dto.VoteResponse;
import com.elit.outildedecision.collaboration.predictibility.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.collaboration.executionplan.exception.VoteAlreadyExistException;
import com.elit.outildedecision.collaboration.vote.exception.VoteNotFoundException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.user.exception.UserNotFoundException;

import java.util.List;

/**
 * Contract for the vote service: create, read and update votes of a predictibility analysis.
 */
public interface VoteService {

  /**
   * Creates a new vote for the specified predictability analysis.
   *
   * @param projectId   the project id
   * @param voteRequest the vote to create
   * @return the success message
   * @throws VoteAlreadyExistException if a vote for the analysis already exists for the user
   */
  String createVote(Long projectId, VoteRequest voteRequest)
    throws VoteAlreadyExistException, ProjectNotFoundException, PredictibilityAnalysisNotFoundException;

  /**
   * Retrieves the votes for the specified predictability analysis.
   *
   * @param predictibilityAnalysisId the id of the predictibility analysis
   * @return the votes for the predictibility analysis
   * @throws UserNotFoundException if a user associated with one of the votes cannot be found
   */
  List<VoteResponse> getVote(Long predictibilityAnalysisId) throws UserNotFoundException;

  /**
   * Updates an existing vote.
   *
   * @param projectId   the project id
   * @param voteId      the id of the vote to update
   * @param voteRequest the updated vote information
   * @return the success message; a
   *     {@link com.elit.outildedecision.shared.exception.ConflictException} (HTTP 409) is thrown
   *     when the caller does not own the vote
   */
  String updateVote(Long projectId, Long voteId, VoteRequest voteRequest)
    throws ProjectNotFoundException, VoteNotFoundException;
}
