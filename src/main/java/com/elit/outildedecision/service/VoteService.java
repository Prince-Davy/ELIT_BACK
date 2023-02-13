package com.elit.outildedecision.service;

import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.exception.VoteAlreadyExistException;
import com.elit.outildedecision.model.VoteModelRequest;
import com.elit.outildedecision.model.VoteModelResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface VoteService {
    ResponseEntity<String> createVote(VoteModelRequest voteModelRequest) throws VoteAlreadyExistException;

    List<VoteModelResponse> getVote(Long predictibilityAnalysisId) throws UserNotFoundException;

    ResponseEntity<String> updateVote(Long voteId, VoteModelRequest voteModelRequest);
}
