package com.elit.outildedecision.controller;

import com.elit.outildedecision.exception.VoteAlreadyExistException;
import com.elit.outildedecision.exception.VoteNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.model.VoteModelRequest;
import com.elit.outildedecision.model.VoteModelResponse;
import com.elit.outildedecision.service.VoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
/**
 * class used to root API calls for vote management. Contains methods to call to create
 * and update votes.
 *
 */
@RestController
@RequestMapping("/")
public class VoteController {
  /**
   * VoteService the service used to manage votes.
   */
  private final VoteService voteService;
  /**
   * Constructor of VoteController.
   *
   * dependency injection of VoteService to manage votes.
   * @param voteService VoteService
   */
  public VoteController(final VoteService voteService) {
    this.voteService = voteService;
  }
  /**
   * method called to add a new vote.
   *
   * @param voteModelRequest the vote to create
   *
   * @return ResponseEntity<String>
   * @throws VoteAlreadyExistException when the vote already exist in database for the same user and predictibility analysis id combination.
   */
  @PostMapping("/vote")
  public ResponseEntity<String> createVote(@Valid @RequestBody final VoteModelRequest voteModelRequest)
      throws VoteAlreadyExistException {
    return voteService.createVote(voteModelRequest);
  }
  /**
   * method called to add a new vote.
   *
   * @param voteModelRequest the vote to create
   *
   * @param projectId project id.
   * @return ResponseEntity<String>
   * @throws VoteAlreadyExistException when the vote already exist in database for the same user and predictibility analysis id combination.
   */
  @PostMapping("/{projectId}/vote")
  @PreAuthorize("isContributer(#projectId)")
  public ResponseEntity<String> createVote(@PathVariable final Long projectId, @Valid @RequestBody final VoteModelRequest voteModelRequest)
    throws VoteAlreadyExistException, ProjectNotFoundException, PredictibilityAnalysisNotFoundException {
    return voteService.createVote(projectId, voteModelRequest);
  }
  /**
   * method called to get all votes for a predictibility analysis.
   *
   * @param predictibilityAnalysisId the id of the predictibility analysis
   * @return List<VoteModelResponse> the list of votes
   * @throws UserNotFoundException if the user is not found
   */
  @GetMapping("/vote/{predictibilityAnalysisId}")
  public List<VoteModelResponse> getVote(@PathVariable final Long predictibilityAnalysisId)
      throws UserNotFoundException {
    return voteService.getVote(predictibilityAnalysisId);
  }
  /**
   * method called to get all votes for a predictibility analysis.
   *
   * @param projectId project id.
   * @param predictibilityAnalysisId the id of the predictibility analysis
   * @return List<VoteModelResponse> the list of votes
   * @throws UserNotFoundException if the user is not found
   */
  @GetMapping("/vote/{projectId}/{predictibilityAnalysisId}")
  @PreAuthorize("isContributer(#projectId)")
  public List<VoteModelResponse> getVote(@PathVariable final Long projectId, @PathVariable final Long predictibilityAnalysisId)
    throws ProjectNotFoundException, PredictibilityAnalysisNotFoundException {
    return voteService.getVote(projectId, predictibilityAnalysisId);
  }
  /**
   * method called to update a vote.
   *
   * @param voteId the id of the vote to update
   * @param voteModelRequest the vote to update
   * @return ResponseEntity<String>
   */
  @PutMapping("/vote/{voteId}")
  public ResponseEntity<String> updateVote(
      @PathVariable final Long voteId, @RequestBody final VoteModelRequest voteModelRequest) {
    return voteService.updateVote(voteId, voteModelRequest);
  }
  /**
   * method called to update a vote.
   *
   * @param projectId project id.
   * @param voteId the id of the vote to update
   * @param voteModelRequest the vote to update
   * @return ResponseEntity<String>
   */
  @PutMapping("/vote/{projectId}/{voteId}")
  @PreAuthorize("isContributer(#projectId)")
  public ResponseEntity<String> updateVote(
    @PathVariable final Long projectId, @PathVariable final Long voteId, @RequestBody final VoteModelRequest voteModelRequest) throws ProjectNotFoundException, VoteNotFoundException {
    return voteService.updateVote(projectId, voteId, voteModelRequest);
  }
}
