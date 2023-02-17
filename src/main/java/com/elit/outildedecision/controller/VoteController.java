package com.elit.outildedecision.controller;

import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.exception.VoteAlreadyExistException;
import com.elit.outildedecision.model.VoteModelRequest;
import com.elit.outildedecision.model.VoteModelResponse;
import com.elit.outildedecision.service.VoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class VoteController {
  private final VoteService voteService;

  public VoteController(VoteService voteService) {
    this.voteService = voteService;
  }

  /**
   * @param voteModelRequest
   * @return ResponseEntity<String>
   * @throws VoteAlreadyExistException
   */
  @PostMapping("/vote")
  public ResponseEntity<String> createVote(@Valid @RequestBody VoteModelRequest voteModelRequest)
      throws VoteAlreadyExistException {
    return voteService.createVote(voteModelRequest);
  }

  @GetMapping("/vote/{predictibilityAnalysisId}")
  public List<VoteModelResponse> getVote(@PathVariable Long predictibilityAnalysisId)
      throws UserNotFoundException {
    return voteService.getVote(predictibilityAnalysisId);
  }

  @PutMapping("/vote/{voteId}")
  public ResponseEntity<String> updateVote(
      @PathVariable Long voteId, @RequestBody VoteModelRequest voteModelRequest) {
    return voteService.updateVote(voteId, voteModelRequest);
  }
}
