package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.Project;
import com.elit.outildedecision.entity.ProjectUserPermission;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.*;
import com.elit.outildedecision.model.VoteModelRequest;
import com.elit.outildedecision.model.VoteModelResponse;
import com.elit.outildedecision.security.CustomMethodSecurityExpressionRoot;
import com.elit.outildedecision.service.VoteService;
import com.elit.outildedecision.util.ModelSamples;
import com.elit.outildedecision.util.ProjectPermession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.elit.outildedecision.util.ModelSamples.toJson;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@ActiveProfiles("test")
@RunWith(MockitoJUnitRunner.class)
class VoteControllerTests {

  private MockMvc mockMvc;

  @Mock
  CustomMethodSecurityExpressionRoot customMethodSecurity;

  private User user;
  private Project project;

  private Long projectId;

  private ProjectUserPermission permission;

  @Mock
  VoteService voteService;

  @InjectMocks
  VoteController voteController;

  private VoteModelRequest voteModelRequest;


  @BeforeEach
  public void setup() {
    MockitoAnnotations.initMocks(this);
    mockMvc = standaloneSetup(voteController).setControllerAdvice(RestResponseEntityExceptionHandler.class).build();

    projectId = 1L;
    voteModelRequest = ModelSamples.VoteSamples.getVoteModelRequest();

    user = new User();
    user.setUserEmail("user@user.com");
    project = new Project();
    project.setProjectId(1L);
    permission = new ProjectUserPermission();
    permission.setProject(project);
    permission.setUser(user);
    permission.setPermission(ProjectPermession.CONTRIBUTER.toString());
    user.setUserProjectPermissions(List.of(permission));
  }

  @Test
  void shouldCreateVote_success() throws Exception {
    when(customMethodSecurity.getPrincipal()).thenReturn(user.getUserEmail());
    when(customMethodSecurity.isContributer(project.getProjectId())).thenReturn(true);

    when(voteService.createVote(projectId, voteModelRequest)).thenReturn(ResponseEntity.ok().body("Le vote a bien été enregistré."));

    mockMvc.perform(post("/"+projectId + "/vote")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON_VALUE).content(ModelSamples.toJson(voteModelRequest)))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("Le vote a bien")));
  }

  @Test
  void shouldCreateVote_ThrowVoteException() throws Exception {
    when(customMethodSecurity.getPrincipal()).thenReturn(user.getUserEmail());
    when(customMethodSecurity.isContributer(project.getProjectId())).thenReturn(true);

    when(voteService.createVote(projectId, voteModelRequest)).thenThrow(new VoteAlreadyExistException("Cet utilisateur a déjà soumis un vote."));

    mockMvc.perform(post("/"+projectId + "/vote")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON_VALUE).content(ModelSamples.toJson(voteModelRequest)))
      .andDo(print())
      .andExpect(status().isConflict())
      .andExpect(content().string(containsString("Cet utilisateur")));
  }

  @Test
  void shouldCreateVote_ThrowProjectException() throws Exception {
    when(customMethodSecurity.getPrincipal()).thenReturn(user.getUserEmail());
    when(customMethodSecurity.isContributer(project.getProjectId())).thenReturn(true);

    when(voteService.createVote(projectId, voteModelRequest)).thenThrow(new ProjectNotFoundException("Ce projet n'existe pas."));

    mockMvc.perform(post("/"+projectId + "/vote")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON_VALUE).content(ModelSamples.toJson(voteModelRequest)))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(content().string(containsString("Ce projet")));
  }

  @Test
  void shouldCreateVote_ThrowPredictibilityAnalysisException() throws Exception {
    when(customMethodSecurity.getPrincipal()).thenReturn(user.getUserEmail());
    when(customMethodSecurity.isContributer(project.getProjectId())).thenReturn(true);

    when(voteService.createVote(projectId, voteModelRequest)).thenThrow(new PredictibilityAnalysisNotFoundException("Analyse de Predictibilité n'existe pas "));

    mockMvc.perform(post("/"+projectId + "/vote")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON_VALUE).content(ModelSamples.toJson(voteModelRequest)))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(content().string(containsString("Analyse de")));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN"})
  void shouldCreateVote_Success() throws Exception {
    voteModelRequest = ModelSamples.VoteSamples.getVoteModelRequest();
    when(voteService.createVote(voteModelRequest)).thenReturn(ResponseEntity.ok().body("Le vote a bien été enregistré."));
    mockMvc.perform(post("/vote").contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(toJson((voteModelRequest))))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("Le vote a bien ")));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN"})
  void shouldCreateVote_ThrowVoteExists() throws Exception {
    voteModelRequest = ModelSamples.VoteSamples.getVoteModelRequest();
    when(voteService.createVote(voteModelRequest)).thenReturn(ResponseEntity.badRequest().body("Cet utilisateur à déjà soumis un vote."));
    mockMvc.perform(post("/vote").contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(toJson((voteModelRequest))))
      .andDo(print())
      .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(authorities = {"USER", "ADMIN"})
  void shouldGetVote_Success() throws Exception {
    List<VoteModelResponse> listVoteModelResponse = ModelSamples.VoteSamples.getVotes();
    when(voteService.getVote(listVoteModelResponse.get(0).getPredictibilityAnalysisId())).thenReturn(listVoteModelResponse);
    mockMvc.perform(get("/vote/"+ listVoteModelResponse.get(0).getPredictibilityAnalysisId()))
      .andDo(print())
      .andExpect(status().isOk());
  }

  @Test
  void shouldGetVote_success() throws Exception {
   when(customMethodSecurity.getPrincipal()).thenReturn(user.getUserEmail());
   when(customMethodSecurity.isContributer(project.getProjectId())).thenReturn(true);

    List<VoteModelResponse> listVoteModelResponse = ModelSamples.VoteSamples.getVotes();
    when(voteService.getVote(projectId,listVoteModelResponse.get(0).getPredictibilityAnalysisId())).thenReturn(listVoteModelResponse);
    mockMvc.perform(get("/vote/"+projectId+"/"+ listVoteModelResponse.get(0).getPredictibilityAnalysisId()))
      .andDo(print())
      .andExpect(status().isOk());
  }

  @Test
  void shouldGetVote_ThrowPredictibilityAnalysisException() throws Exception {
    when(customMethodSecurity.getPrincipal()).thenReturn(user.getUserEmail());
    when(customMethodSecurity.isContributer(project.getProjectId())).thenReturn(true);

    List<VoteModelResponse> listVoteModelResponse = ModelSamples.VoteSamples.getVotes();
    when(voteService.getVote(projectId,listVoteModelResponse.get(0).getPredictibilityAnalysisId())).thenThrow(new PredictibilityAnalysisNotFoundException("Analyse de predictibilité n'existe pas"));
    mockMvc.perform(get("/vote/"+projectId+"/"+ listVoteModelResponse.get(0).getPredictibilityAnalysisId()))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(content().string(containsString("Analyse de predictibilit")));
  }

  @Test
  void shouldGetVote_ThrowProjectException() throws Exception {
    when(customMethodSecurity.getPrincipal()).thenReturn(user.getUserEmail());
    when(customMethodSecurity.isContributer(project.getProjectId())).thenReturn(true);

    List<VoteModelResponse> listVoteModelResponse = ModelSamples.VoteSamples.getVotes();
    when(voteService.getVote(projectId,listVoteModelResponse.get(0).getPredictibilityAnalysisId())).thenThrow(new ProjectNotFoundException("Ce projet n'existe pas."));
    mockMvc.perform(get("/vote/"+projectId+"/"+ listVoteModelResponse.get(0).getPredictibilityAnalysisId()))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(content().string(containsString("Ce projet")));
  }

  @Test
  @WithMockUser(authorities = {"USER", "ADMIN"})
  void shouldUpdateVote_Success() throws Exception {
    voteModelRequest = ModelSamples.VoteSamples.getVoteModelRequest();
    voteModelRequest.setChosenPMM("modified chosenPMM");
    when(voteService.updateVote(voteModelRequest.getVoteId(), voteModelRequest)).thenReturn(ResponseEntity.status(HttpStatus.OK).body("Vote mis à jour."));
    mockMvc.perform(MockMvcRequestBuilders.put("/vote/"+ voteModelRequest.getVoteId()).contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(toJson(voteModelRequest)))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("Vote mis ")));
  }

  @Test
  @WithMockUser(authorities = {"USER", "ADMIN"})
  void shouldUpdateVote_ThrowConflictException() throws Exception {
    voteModelRequest = ModelSamples.VoteSamples.getVoteModelRequest();
    when(voteService.updateVote(voteModelRequest.getVoteId(), voteModelRequest)).thenReturn(ResponseEntity.status(HttpStatus.CONFLICT).body("Vous n'êtes pas autorisé à modifier ce vote."));
    mockMvc.perform(MockMvcRequestBuilders.put("/vote/"+ voteModelRequest.getVoteId()).contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(toJson(voteModelRequest)))
      .andDo(print()).andExpect(status().isConflict());
  }

  @Test
  void shouldUpdateVote_success() throws Exception {
    when(customMethodSecurity.getPrincipal()).thenReturn(user.getUserEmail());
    when(customMethodSecurity.isContributer(project.getProjectId())).thenReturn(true);

    voteModelRequest = ModelSamples.VoteSamples.getVoteModelRequest();
    voteModelRequest.setChosenPMM("modified chosenPMM");
    when(voteService.updateVote(projectId ,voteModelRequest.getVoteId(), voteModelRequest)).thenReturn(ResponseEntity.status(HttpStatus.OK).body("Vote mis à jour."));
    mockMvc.perform(MockMvcRequestBuilders.put("/vote/"+ projectId + "/" + voteModelRequest.getVoteId()).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(toJson(voteModelRequest)))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("Vote mis ")));
  }

  @Test
  void shouldUpdateVote_ThrowProjectException() throws Exception {
    when(customMethodSecurity.getPrincipal()).thenReturn(user.getUserEmail());
    when(customMethodSecurity.isContributer(project.getProjectId())).thenReturn(true);

    voteModelRequest = ModelSamples.VoteSamples.getVoteModelRequest();
    voteModelRequest.setChosenPMM("modified chosenPMM");
    when(voteService.updateVote(projectId ,voteModelRequest.getVoteId(), voteModelRequest)).thenThrow(new ProjectNotFoundException("Ce projet n'existe pas."));
    mockMvc.perform(MockMvcRequestBuilders.put("/vote/"+ projectId + "/" + voteModelRequest.getVoteId()).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(toJson(voteModelRequest)))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(content().string(containsString("Ce projet ")));
  }

  @Test
  void shouldUpdateVote_ThrowVoteException() throws Exception {
    when(customMethodSecurity.getPrincipal()).thenReturn(user.getUserEmail());
    when(customMethodSecurity.isContributer(project.getProjectId())).thenReturn(true);

    voteModelRequest = ModelSamples.VoteSamples.getVoteModelRequest();
    voteModelRequest.setChosenPMM("modified chosenPMM");
    when(voteService.updateVote(projectId ,voteModelRequest.getVoteId(), voteModelRequest)).thenThrow(new VoteNotFoundException("Ce vote n'existe pas!"));
    mockMvc.perform(MockMvcRequestBuilders.put("/vote/"+ projectId + "/" + voteModelRequest.getVoteId()).contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON).content(toJson(voteModelRequest)))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(content().string(containsString("Ce vote")));
  }
}
