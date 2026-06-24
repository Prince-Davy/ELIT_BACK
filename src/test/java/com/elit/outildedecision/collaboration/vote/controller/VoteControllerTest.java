package com.elit.outildedecision.collaboration.vote.controller;

import com.elit.outildedecision.collaboration.vote.dto.VoteRequest;
import com.elit.outildedecision.collaboration.vote.dto.VoteResponse;
import com.elit.outildedecision.collaboration.vote.exception.VoteNotFoundException;
import com.elit.outildedecision.collaboration.vote.service.VoteService;
import com.elit.outildedecision.collaboration.predictibility.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.collaboration.executionplan.exception.VoteAlreadyExistException;
import com.elit.outildedecision.shared.exception.ConflictException;
import com.elit.outildedecision.shared.exception.handler.RestResponseEntityExceptionHandler;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.util.ModelSamples;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.elit.outildedecision.shared.util.ModelSamples.toJson;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ActiveProfiles("test")
class VoteControllerTest {

  private MockMvc mockMvc;

  @Mock
  private VoteService voteService;

  @InjectMocks
  private VoteController voteController;

  private Long projectId;
  private VoteRequest voteRequest;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    mockMvc = standaloneSetup(voteController).setControllerAdvice(RestResponseEntityExceptionHandler.class).build();

    projectId = 1L;
    voteRequest = ModelSamples.VoteSamples.getVoteModelRequest();
  }

  @Test
  void shouldCreateVote_success() throws Exception {
    when(voteService.createVote(eq(projectId), any(VoteRequest.class))).thenReturn("Le vote a bien été enregistré.");

    mockMvc.perform(post("/" + projectId + "/vote")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON_VALUE).content(toJson(voteRequest)))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("Le vote a bien")));
  }

  @Test
  void shouldCreateVote_ThrowVoteException() throws Exception {
    when(voteService.createVote(eq(projectId), any(VoteRequest.class)))
      .thenThrow(new VoteAlreadyExistException("Cet utilisateur a déjà soumis un vote."));

    mockMvc.perform(post("/" + projectId + "/vote")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON_VALUE).content(toJson(voteRequest)))
      .andDo(print())
      .andExpect(status().isConflict())
      .andExpect(content().string(containsString("Cet utilisateur")));
  }

  @Test
  void shouldCreateVote_ThrowProjectException() throws Exception {
    when(voteService.createVote(eq(projectId), any(VoteRequest.class)))
      .thenThrow(new ProjectNotFoundException("Ce projet n'existe pas."));

    mockMvc.perform(post("/" + projectId + "/vote")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON_VALUE).content(toJson(voteRequest)))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(content().string(containsString("Ce projet")));
  }

  @Test
  void shouldCreateVote_ThrowPredictibilityAnalysisException() throws Exception {
    when(voteService.createVote(eq(projectId), any(VoteRequest.class)))
      .thenThrow(new PredictibilityAnalysisNotFoundException("Analyse de Predictibilité n'existe pas "));

    mockMvc.perform(post("/" + projectId + "/vote")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON_VALUE).content(toJson(voteRequest)))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(content().string(containsString("Analyse de")));
  }

  @Test
  @WithMockUser(authorities = {"USER", "ADMIN"})
  void shouldGetVote_Success() throws Exception {
    List<VoteResponse> votes = ModelSamples.VoteSamples.getVotes();
    when(voteService.getVote(votes.get(0).getPredictibilityAnalysisId())).thenReturn(votes);
    mockMvc.perform(get("/vote/" + votes.get(0).getPredictibilityAnalysisId()))
      .andDo(print())
      .andExpect(status().isOk());
  }

  @Test
  void shouldUpdateVote_success() throws Exception {
    voteRequest.setChosenPMM("modified chosenPMM");
    when(voteService.updateVote(eq(projectId), eq(voteRequest.getVoteId()), any(VoteRequest.class))).thenReturn("Vote mis à jour.");
    mockMvc.perform(MockMvcRequestBuilders.put("/vote/" + projectId + "/" + voteRequest.getVoteId())
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(toJson(voteRequest)))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("Vote mis ")));
  }

  @Test
  void shouldUpdateVote_notOwner_returnsConflict() throws Exception {
    voteRequest.setChosenPMM("modified chosenPMM");
    when(voteService.updateVote(eq(projectId), eq(voteRequest.getVoteId()), any(VoteRequest.class)))
      .thenThrow(new ConflictException("Vous n'êtes pas autorisé à modifier ce vote."));
    mockMvc.perform(MockMvcRequestBuilders.put("/vote/" + projectId + "/" + voteRequest.getVoteId())
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(toJson(voteRequest)))
      .andDo(print())
      .andExpect(status().isConflict())
      .andExpect(content().string(containsString("Vous n'êtes pas autorisé")));
  }

  @Test
  void shouldUpdateVote_ThrowProjectException() throws Exception {
    voteRequest.setChosenPMM("modified chosenPMM");
    when(voteService.updateVote(eq(projectId), eq(voteRequest.getVoteId()), any(VoteRequest.class)))
      .thenThrow(new ProjectNotFoundException("Ce projet n'existe pas."));
    mockMvc.perform(MockMvcRequestBuilders.put("/vote/" + projectId + "/" + voteRequest.getVoteId())
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(toJson(voteRequest)))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(content().string(containsString("Ce projet ")));
  }

  @Test
  void shouldUpdateVote_ThrowVoteException() throws Exception {
    voteRequest.setChosenPMM("modified chosenPMM");
    when(voteService.updateVote(eq(projectId), eq(voteRequest.getVoteId()), any(VoteRequest.class)))
      .thenThrow(new VoteNotFoundException("Ce vote n'existe pas!"));
    mockMvc.perform(MockMvcRequestBuilders.put("/vote/" + projectId + "/" + voteRequest.getVoteId())
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(toJson(voteRequest)))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(content().string(containsString("Ce vote")));
  }
}
