package com.elit.outildedecision.controller;

import com.elit.outildedecision.model.VoteModelRequest;
import com.elit.outildedecision.model.VoteModelResponse;
import com.elit.outildedecision.service.VoteService;
import com.elit.outildedecision.service.implementation.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.elit.outildedecision.controller.ModelSamples.VoteSamples;

import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = VoteController.class)
@ExtendWith(MockitoExtension.class)
public class VoteControllerTests {
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  VoteService voteService;

  @MockBean
  private UserServiceImpl userService;

  @InjectMocks
  VoteController voteController;

    private Long voteId = 1l;
  public static String toJson(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  @Test
  @WithMockUser(authorities = {"ADMIN"})
  void shouldCreateVote_Success() throws Exception {
    VoteModelRequest voteModelRequest = VoteSamples.getVoteModelRequest();
    when(voteService.createVote(voteModelRequest)).thenReturn(ResponseEntity.ok().body("Le vote a bien été enregistré."));
    mockMvc.perform(post("/api/vote").contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(toJson((voteModelRequest)))).andDo(print()).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = {"ADMIN"})
  void shouldCreateVote_Fail() throws Exception {
    VoteModelRequest voteModelRequest = VoteSamples.getVoteModelRequest();
    when(voteService.createVote(voteModelRequest)).thenReturn(ResponseEntity.badRequest().body("Cet utilisateur à déjà soumis un vote."));
    mockMvc.perform(post("/api/vote").contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(toJson((voteModelRequest)))).andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(authorities = {"USER", "ADMIN"})
  void shouldGetVote_Success() throws Exception {
    List<VoteModelResponse> listVoteModelResponse = VoteSamples.getVotes();
    when(voteService.getVote(listVoteModelResponse.get(0).getPredictibilityAnalysisId())).thenReturn(listVoteModelResponse);
    mockMvc.perform(get("/api/vote/"+ listVoteModelResponse.get(0).getPredictibilityAnalysisId())).andDo(print()).andExpect(status().isOk());
  }

}
