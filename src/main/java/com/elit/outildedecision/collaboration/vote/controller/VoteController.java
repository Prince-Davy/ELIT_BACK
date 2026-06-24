package com.elit.outildedecision.collaboration.vote.controller;

import com.elit.outildedecision.collaboration.vote.dto.VoteRequest;
import com.elit.outildedecision.collaboration.vote.dto.VoteResponse;
import com.elit.outildedecision.collaboration.vote.exception.VoteNotFoundException;
import com.elit.outildedecision.collaboration.vote.service.VoteService;
import com.elit.outildedecision.collaboration.predictibility.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.collaboration.executionplan.exception.VoteAlreadyExistException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.user.exception.UserNotFoundException;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controller routing the API calls for vote management. Errors are delegated to the global handler.
 */
@RestController
@RequestMapping("/")
@Tag(name = "Collaboration - Vote", description = "Gestion des votes")
public class VoteController {

  /**
   * The service used to manage votes.
   */
  private final VoteService voteService;

  /**
   * Constructor of VoteController.
   *
   * @param voteService the service used to manage votes
   */
  public VoteController(final VoteService voteService) {
    this.voteService = voteService;
  }

  /**
   * Adds a new vote.
   *
   * @param projectId   project id
   * @param voteRequest the vote to create
   * @return a success message
   * @throws VoteAlreadyExistException when a vote already exists for the same user and analysis
   */
  @PostMapping(path = {"/{projectId}/vote", "/{projectId}/vote/"})
  @PreAuthorize("isContributer(#projectId)")
  public String createVote(@PathVariable final Long projectId, @Valid @RequestBody final VoteRequest voteRequest)
    throws VoteAlreadyExistException, ProjectNotFoundException, PredictibilityAnalysisNotFoundException {
    return voteService.createVote(projectId, voteRequest);
  }

  /**
   * Gets all votes for a predictibility analysis.
   *
   * @param predictibilityAnalysisId the id of the predictibility analysis
   * @return the list of votes
   * @throws UserNotFoundException if the user is not found
   */
  @GetMapping("/vote/{predictibilityAnalysisId}")
  public List<VoteResponse> getVote(@PathVariable final Long predictibilityAnalysisId)
      throws UserNotFoundException {
    return voteService.getVote(predictibilityAnalysisId);
  }

  /**
   * Updates a vote.
   *
   * @param projectId   project id
   * @param voteId      the id of the vote to update
   * @param voteRequest the vote to update
   * @return a success message
   */
  @PutMapping("/vote/{projectId}/{voteId}")
  @PreAuthorize("isContributer(#projectId)")
  public ResponseEntity<String> updateVote(
    @PathVariable final Long projectId, @PathVariable final Long voteId, @RequestBody final VoteRequest voteRequest)
    throws ProjectNotFoundException, VoteNotFoundException {
    return ResponseEntity.ok(voteService.updateVote(projectId, voteId, voteRequest));
  }
}
