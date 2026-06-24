package com.elit.outildedecision.collaboration.brainstorming.controller;

import com.elit.outildedecision.collaboration.brainstorming.dto.BrainstormingRequest;
import com.elit.outildedecision.collaboration.brainstorming.exception.BrainstormingNotFoundException;
import com.elit.outildedecision.collaboration.brainstorming.exception.ExistingBrainstormingException;
import com.elit.outildedecision.collaboration.brainstorming.service.BrainstormingService;
import com.elit.outildedecision.collaboration.predictibility.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.shared.exception.ForbiddenException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.user.service.implementation.UserServiceImpl;
import com.elit.outildedecision.shared.util.Constants;
import com.elit.outildedecision.shared.util.ProjectPermession;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "testuser", roles = {"USER"})
class BrainstormingControllerTest {

  private static final long PROJECT_ID = 1L;
  private static final long ANALYSIS_ID = 2L;
  private static final long BRAINSTORMING_ID = 3L;

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private BrainstormingService brainstormingService;

  @MockitoBean
  private UserServiceImpl userService;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setup() {
    // CDP satisfies isProjectOwner and isContributer, hence authorizes every endpoint under test.
    when(userService.getProjectPermissions("testuser", PROJECT_ID))
        .thenReturn(List.of(ProjectPermession.CDP.name()));
  }

  // ---------------------------------------------------------------------------
  // GET /brainstorming/{projectId}/{prevId}
  // ---------------------------------------------------------------------------

  @SneakyThrows
  @Test
  void shouldReturnOkWhenGettingBrainstorming() {
    when(brainstormingService.getBrainstorming(PROJECT_ID, ANALYSIS_ID)).thenReturn(List.of());

    mockMvc.perform(get("/brainstorming/{projectId}/{prevId}", PROJECT_ID, ANALYSIS_ID))
        .andExpect(status().isOk());
  }

  @SneakyThrows
  @Test
  void shouldReturnNotFoundWhenGettingBrainstormingOfAnotherProject() {
    when(brainstormingService.getBrainstorming(PROJECT_ID, ANALYSIS_ID))
        .thenThrow(new ProjectNotFoundException(Constants.PROJECT_DOESNT_EXIST));

    mockMvc.perform(get("/brainstorming/{projectId}/{prevId}", PROJECT_ID, ANALYSIS_ID))
        .andExpect(status().isNotFound());
  }

  @SneakyThrows
  @Test
  void shouldReturnNotFoundWhenGettingBrainstormingForUnknownAnalysis() {
    when(brainstormingService.getBrainstorming(PROJECT_ID, ANALYSIS_ID))
        .thenThrow(new PredictibilityAnalysisNotFoundException(Constants.ANALYSIS_DOESNT_EXIST));

    mockMvc.perform(get("/brainstorming/{projectId}/{prevId}", PROJECT_ID, ANALYSIS_ID))
        .andExpect(status().isNotFound());
  }

  // ---------------------------------------------------------------------------
  // GET /brainstorming/abstract/...
  // ---------------------------------------------------------------------------

  @SneakyThrows
  @Test
  void shouldReturnOkWhenGettingBrainstormingAbstract() {
    when(brainstormingService.getBrainstormingAbstract(PROJECT_ID, ANALYSIS_ID)).thenReturn(List.of());

    mockMvc.perform(get("/brainstorming/abstract/{projectId}/{prevId}", PROJECT_ID, ANALYSIS_ID))
        .andExpect(status().isOk());
  }

  @SneakyThrows
  @Test
  void shouldReturnOkWhenGettingBrainstormingAbstractByAnalysisId() {
    when(brainstormingService.getBrainstormingAbstractByAnalysisId(ANALYSIS_ID)).thenReturn(List.of());

    mockMvc.perform(get("/brainstorming/abstract/{prevId}", ANALYSIS_ID))
        .andExpect(status().isOk());
  }

  @SneakyThrows
  @Test
  void shouldReturnNotFoundWhenGettingAbstractByUnknownAnalysisId() {
    when(brainstormingService.getBrainstormingAbstractByAnalysisId(ANALYSIS_ID))
        .thenThrow(new PredictibilityAnalysisNotFoundException(Constants.ANALYSIS_DOESNT_EXIST));

    mockMvc.perform(get("/brainstorming/abstract/{prevId}", ANALYSIS_ID))
        .andExpect(status().isNotFound());
  }

  // ---------------------------------------------------------------------------
  // POST /brainstorming/{projectId}
  // ---------------------------------------------------------------------------

  @SneakyThrows
  @Test
  void shouldReturnOkWhenCreatingBrainstorming() {
    when(brainstormingService.createBrainstorming(eq(PROJECT_ID), any(BrainstormingRequest.class)))
        .thenReturn(1L);

    mockMvc.perform(post("/brainstorming/{projectId}", PROJECT_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(sampleRequest())))
        .andExpect(status().isOk());
  }

  @SneakyThrows
  @Test
  void shouldReturnNotFoundWhenCreatingForUnknownAnalysis() {
    when(brainstormingService.createBrainstorming(eq(PROJECT_ID), any(BrainstormingRequest.class)))
        .thenThrow(new PredictibilityAnalysisNotFoundException(Constants.ANALYSIS_DOESNT_EXIST));

    mockMvc.perform(post("/brainstorming/{projectId}", PROJECT_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(sampleRequest())))
        .andExpect(status().isNotFound());
  }

  @SneakyThrows
  @Test
  void shouldReturnConflictWhenBrainstormingAlreadyExists() {
    when(brainstormingService.createBrainstorming(eq(PROJECT_ID), any(BrainstormingRequest.class)))
        .thenThrow(new ExistingBrainstormingException("Cet utilisateur à déjà soumis un commentaire."));

    mockMvc.perform(post("/brainstorming/{projectId}", PROJECT_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(sampleRequest())))
        .andExpect(status().isConflict());
  }

  // ---------------------------------------------------------------------------
  // PUT /brainstorming/{projectId}
  // ---------------------------------------------------------------------------

  @SneakyThrows
  @Test
  void shouldReturnOkWhenUpdatingBrainstorming() {
    when(brainstormingService.updateBrainstorming(eq(PROJECT_ID), any(BrainstormingRequest.class)))
        .thenReturn(Constants.BRAINSTORMING_SAVED);

    mockMvc.perform(put("/brainstorming/{projectId}", PROJECT_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(sampleRequest())))
        .andExpect(status().isOk());
  }

  @SneakyThrows
  @Test
  void shouldReturnForbiddenWhenUpdatingBrainstormingOfAnotherUser() {
    when(brainstormingService.updateBrainstorming(eq(PROJECT_ID), any(BrainstormingRequest.class)))
        .thenThrow(new ForbiddenException("L'utilisateur enregistré ne correspond pas à l'utilisateur du brainstorming"));

    mockMvc.perform(put("/brainstorming/{projectId}", PROJECT_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(sampleRequest())))
        .andExpect(status().isForbidden());
  }

  @SneakyThrows
  @Test
  void shouldReturnNotFoundWhenUpdatingUnknownBrainstorming() {
    when(brainstormingService.updateBrainstorming(eq(PROJECT_ID), any(BrainstormingRequest.class)))
        .thenThrow(new BrainstormingNotFoundException(Constants.BRAINSTORMING_DOESNT_EXIST));

    mockMvc.perform(put("/brainstorming/{projectId}", PROJECT_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(sampleRequest())))
        .andExpect(status().isNotFound());
  }

  // ---------------------------------------------------------------------------
  // DELETE /brainstorming/{projectId}/{brainstormingId}
  // ---------------------------------------------------------------------------

  @SneakyThrows
  @Test
  void shouldReturnOkWhenDeletingBrainstorming() {
    when(brainstormingService.deleteBrainstorming(PROJECT_ID, BRAINSTORMING_ID))
        .thenReturn("Le commentaire du brainstorming a bien été supprimé.");

    mockMvc.perform(delete("/brainstorming/{projectId}/{brainstormingId}", PROJECT_ID, BRAINSTORMING_ID))
        .andExpect(status().isOk());
  }

  @SneakyThrows
  @Test
  void shouldReturnNotFoundWhenDeletingUnknownBrainstorming() {
    when(brainstormingService.deleteBrainstorming(PROJECT_ID, BRAINSTORMING_ID))
        .thenThrow(new BrainstormingNotFoundException(Constants.BRAINSTORMING_DOESNT_EXIST));

    mockMvc.perform(delete("/brainstorming/{projectId}/{brainstormingId}", PROJECT_ID, BRAINSTORMING_ID))
        .andExpect(status().isNotFound());
  }

  // ---------------------------------------------------------------------------
  // PUT /brainstorming/validate/{projectId}/{brainstormingId}
  // ---------------------------------------------------------------------------

  @SneakyThrows
  @Test
  void shouldReturnOkWhenValidatingBrainstorming() {
    when(brainstormingService.validateOrRejectBrainstorming(eq(PROJECT_ID), eq(BRAINSTORMING_ID), any()))
        .thenReturn("Le commentaire a bien été enregistré.");

    mockMvc.perform(put("/brainstorming/validate/{projectId}/{brainstormingId}", PROJECT_ID, BRAINSTORMING_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"isConfirmed\": true}"))
        .andExpect(status().isOk());
  }

  @SneakyThrows
  @Test
  void shouldReturnNotFoundWhenValidatingUnknownBrainstorming() {
    when(brainstormingService.validateOrRejectBrainstorming(eq(PROJECT_ID), eq(BRAINSTORMING_ID), any()))
        .thenThrow(new BrainstormingNotFoundException(Constants.BRAINSTORMING_DOESNT_EXIST));

    mockMvc.perform(put("/brainstorming/validate/{projectId}/{brainstormingId}", PROJECT_ID, BRAINSTORMING_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"isConfirmed\": true}"))
        .andExpect(status().isNotFound());
  }

  // ---------------------------------------------------------------------------
  // Security : @PreAuthorize is really enforced
  // ---------------------------------------------------------------------------

  @SneakyThrows
  @Test
  void shouldReturnForbiddenWhenUserHasNoProjectPermission() {
    when(userService.getProjectPermissions("testuser", PROJECT_ID)).thenReturn(List.of());

    mockMvc.perform(post("/brainstorming/{projectId}", PROJECT_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(sampleRequest())))
        .andExpect(status().isForbidden());
  }

  private BrainstormingRequest sampleRequest() {
    BrainstormingRequest request = new BrainstormingRequest();
    request.setBrainstormingId(BRAINSTORMING_ID);
    request.setPredictibilityAnalysisId(ANALYSIS_ID);
    request.setQuestionId(0L);
    request.setBrainstormingText("idée");
    return request;
  }
}
