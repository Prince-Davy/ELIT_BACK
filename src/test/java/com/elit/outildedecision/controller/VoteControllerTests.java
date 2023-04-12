package com.elit.outildedecision.controller;

import com.elit.outildedecision.model.VoteModelRequest;
import com.elit.outildedecision.model.VoteModelResponse;
import com.elit.outildedecision.service.VoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.elit.outildedecision.util.ModelSamples.VoteSamples;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.elit.outildedecision.util.ModelSamples.toJson;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class VoteControllerTests {
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  VoteService voteService;

  @InjectMocks
  VoteController voteController;

  private VoteModelRequest voteModelRequest;


  @BeforeEach
  public void setup() {
     voteModelRequest = VoteSamples.getVoteModelRequest();
  }
  @Test
  @WithMockUser(authorities = {"ADMIN"})
  void shouldCreateVote_Success() throws Exception {
    voteModelRequest = VoteSamples.getVoteModelRequest();
    when(voteService.createVote(voteModelRequest)).thenReturn(ResponseEntity.ok().body("Le vote a bien été enregistré."));
    mockMvc.perform(post("/api/vote").contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(toJson((voteModelRequest))))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("Le vote a bien ")));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN"})
  void shouldCreateVote_ThrowVoteExists() throws Exception {
    voteModelRequest = VoteSamples.getVoteModelRequest();
    when(voteService.createVote(voteModelRequest)).thenReturn(ResponseEntity.badRequest().body("Cet utilisateur à déjà soumis un vote."));
    mockMvc.perform(post("/api/vote").contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(toJson((voteModelRequest))))
      .andDo(print())
      .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(authorities = {"USER", "ADMIN"})
  void shouldGetVote_Success() throws Exception {
    List<VoteModelResponse> listVoteModelResponse = VoteSamples.getVotes();
    when(voteService.getVote(listVoteModelResponse.get(0).getPredictibilityAnalysisId())).thenReturn(listVoteModelResponse);
    mockMvc.perform(get("/api/vote/"+ listVoteModelResponse.get(0).getPredictibilityAnalysisId()))
      .andDo(print())
      .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = {"USER", "ADMIN"})
  void shouldUpdateVote_Success() throws Exception {
    voteModelRequest = VoteSamples.getVoteModelRequest();
    voteModelRequest.setChosenPMM("modified chosenPMM");
    when(voteService.updateVote(voteModelRequest.getVoteId(), voteModelRequest)).thenReturn(ResponseEntity.status(HttpStatus.OK).body("Vote mis à jour."));
    mockMvc.perform(MockMvcRequestBuilders.put("/api/vote/"+ voteModelRequest.getVoteId()).contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(toJson(voteModelRequest)))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("Vote mis ")));
  }

  @Test
  @WithMockUser(authorities = {"USER", "ADMIN"})
  void shouldUpdateVote_ThrowConflictException() throws Exception {
    voteModelRequest = VoteSamples.getVoteModelRequest();
    when(voteService.updateVote(voteModelRequest.getVoteId(), voteModelRequest)).thenReturn(ResponseEntity.status(HttpStatus.CONFLICT).body("Vous n'êtes pas autorisé à modifier ce vote."));
    mockMvc.perform(MockMvcRequestBuilders.put("/api/vote/"+ voteModelRequest.getVoteId()).contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(toJson(voteModelRequest)))
      .andDo(print()).andExpect(status().isConflict());
  }
}
