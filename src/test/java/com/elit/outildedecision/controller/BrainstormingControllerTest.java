package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.*;
import com.elit.outildedecision.exception.BrainstormingNotFoundException;
import com.elit.outildedecision.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.model.BrainstormingModel;
import com.elit.outildedecision.repository.PredictibilityAnalysisRepository;
import com.elit.outildedecision.service.implementation.BrainstormingServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class BrainstormingControllerTest {
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private BrainstormingServiceImpl brainstormingService;
  @MockBean
  private PredictibilityAnalysisRepository predictibilityAnalysisRepository;
  @InjectMocks
  BrainstormingController  brainstormingController;

  public static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @WithMockUser(authorities = {"USER", "ADMIN"})
  @Test
  void getBrainstorming_whenPredictibilityAnalysis_exists() throws Exception {
   Long predictibilityAnalysisId = 1L;
    PredictibilityAnalysis predictibilityAnalysis = new PredictibilityAnalysis();
    predictibilityAnalysis.setPredictibilityAnalysisId(predictibilityAnalysisId);
   when(predictibilityAnalysisRepository.findById(predictibilityAnalysisId)).thenReturn(Optional.of(predictibilityAnalysis));
    when(brainstormingService.getBrainstorming(1L)).thenReturn(ResponseEntity.ok().body("Liste de FormStepModelForBrainstorming"));
    ResultActions response = mockMvc.perform(get("/api/brainstorming/"+ predictibilityAnalysisId));
    response.andExpect(status().isOk())
      .andDo(print());
    Assertions.assertEquals(200, response.andReturn().getResponse().getStatus());
  }

  @Test
  void getBrainstorming_whenPredictibilityAnalysis_isNotFound() throws Exception {
    Long predictibilityAnalysisId = 1L;
    when( brainstormingService.getBrainstorming(predictibilityAnalysisId)).thenThrow(new PredictibilityAnalysisNotFoundException("Ce analysis n'existe pas"));
    ResultActions response = mockMvc.perform(get("/api/brainstorming/"+ predictibilityAnalysisId));
    response.andExpect(status().isNotFound())
      .andDo(print());
    Assertions.assertEquals(404, response.andReturn().getResponse().getStatus());
  }

  @Test
  void getBrainstormingAbstract_whenPredictibilityAnalysis_exists() throws Exception {
    Long predictibilityAnalysisId = 1L;
    PredictibilityAnalysis predictibilityAnalysis = new PredictibilityAnalysis();
    predictibilityAnalysis.setPredictibilityAnalysisId(predictibilityAnalysisId);
    when(predictibilityAnalysisRepository.findById(predictibilityAnalysisId)).thenReturn(Optional.of(predictibilityAnalysis));
    when(brainstormingService.getBrainstormingAbstract(predictibilityAnalysisId)).thenReturn(ResponseEntity.ok().body("Liste de FormStepModelForBrainstorming"));
    ResultActions response = mockMvc.perform(get("/api/brainstorming/abstract/"+ predictibilityAnalysisId));
    response.andExpect(status().isOk())
      .andDo(print());
    Assertions.assertEquals(200, response.andReturn().getResponse().getStatus());
  }

  @Test
  void getBrainstormingAbstract_whenPredictibilityAnalysis_isNotFound() throws Exception {
    Long predictibilityAnalysisId = 1L;
    when( brainstormingService.getBrainstormingAbstract(predictibilityAnalysisId)).thenThrow(new PredictibilityAnalysisNotFoundException("Ce analysis n'existe pas"));
    ResultActions response = mockMvc.perform(get("/api/brainstorming/abstract/"+ predictibilityAnalysisId));
    response.andExpect(status().isNotFound())
      .andDo(print());
    Assertions.assertEquals(404, response.andReturn().getResponse().getStatus());
  }

  @Test
  void createBrainstorming() throws Exception {
    BrainstormingModel brainstormingModel = new BrainstormingModel();
    brainstormingModel.setBrainstormingId(1L);
    brainstormingModel.setBrainstormingText("text text");
    when(brainstormingService.createBrainstorming(brainstormingModel)).thenReturn(ResponseEntity.ok().body("Le commentaire du brainstorming a bien été enregistré."));
    MockHttpServletResponse response = mockMvc.perform(post("/api/brainstorming").contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(asJsonString(brainstormingModel))).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }
  @Test
  void createBrainstorming_whenPredictibilityAnalysisNotFound() throws Exception {
    BrainstormingModel brainstormingModel = new BrainstormingModel();
    brainstormingModel.setBrainstormingId(1L);
    brainstormingModel.setBrainstormingText("text text");
    when(brainstormingService.createBrainstorming(brainstormingModel)).thenThrow(new PredictibilityAnalysisNotFoundException("Ce analysis n'existe pas"));
    MockHttpServletResponse response = mockMvc.perform(post("/api/brainstorming").contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(asJsonString(brainstormingModel))).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(404, response.getStatus());
  }

  @Test
  void updateBrainstorming() throws Exception {
    BrainstormingModel brainstormingModel = new BrainstormingModel();
    brainstormingModel.setBrainstormingId(1L);
    brainstormingModel.setBrainstormingText("text text");
    when(brainstormingService.updateBrainstorming(brainstormingModel)).thenReturn(ResponseEntity.ok().body("Le commentaire du brainstorming a bien été enregistré."));
    MockHttpServletResponse response = mockMvc.perform(put("/api/brainstorming").contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(asJsonString(brainstormingModel))).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());

  }

  @Test
  void updateBrainstorming_whenBrainstormingNotFound() throws Exception {
    BrainstormingModel brainstormingModel = new BrainstormingModel();
    brainstormingModel.setBrainstormingId(1L);
    brainstormingModel.setBrainstormingText("text text");
    when(brainstormingService.updateBrainstorming(brainstormingModel)).thenThrow(new BrainstormingNotFoundException("Cet brainstorming n'existe pas"));
    MockHttpServletResponse response = mockMvc.perform(put("/api/brainstorming").contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(asJsonString(brainstormingModel))).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(404, response.getStatus());

  }

  @Test
  void updateBrainstorming_whenBrainstormingUserDontMatch() throws Exception {
    BrainstormingModel brainstormingModel = new BrainstormingModel();
    brainstormingModel.setBrainstormingId(1L);
    brainstormingModel.setBrainstormingText("text text");
    when(brainstormingService.updateBrainstorming(brainstormingModel)).thenReturn(ResponseEntity.status(HttpStatus.FORBIDDEN)
      .body("L'utilisateur enregistré ne correspond pas à l'utilisateur du brainstorming"));
    MockHttpServletResponse response = mockMvc.perform(put("/api/brainstorming").contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(asJsonString(brainstormingModel))).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(403, response.getStatus());

  }

  @Test
  void removeBrainstorming() throws Exception {
    Long brainstormingId = 1L;
    when(brainstormingService.deleteBrainstorming(brainstormingId)).thenReturn(ResponseEntity.ok().body("Le commentaire du brainstorming a bien été supprimé."));
    MockHttpServletResponse response = mockMvc.perform(delete("/api/brainstorming/"+brainstormingId)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }

  @Test
  void removeBrainstorming_whenBrainstormingDoseNotExists() throws Exception {
    Long brainstormingId = 1L;
    when(brainstormingService.deleteBrainstorming(brainstormingId)).thenThrow(new BrainstormingNotFoundException("Cet brainstorming n'existe pas"));
    MockHttpServletResponse response = mockMvc.perform(delete("/api/brainstorming/"+brainstormingId)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(404, response.getStatus());
  }

  @Test
  void validateOrRejectBrainstorming() throws Exception {
    Long brainstormingId = 1L;
    HttpEntity<String> isChecked = new HttpEntity<>("true");
    when(brainstormingService.validateOrRejectBrainstorming(brainstormingId, isChecked)).thenReturn(ResponseEntity.ok().body("Le commentaire a bien été enregistré."));
    MockHttpServletResponse response = mockMvc.perform(put("/api/brainstorming/validate/"+brainstormingId).contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(asJsonString(isChecked))).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }

  @Test
  void validateOrRejectBrainstorming_whenBrainstormingDoseNotExists() throws Exception {
    Long brainstormingId = 1L;
    HttpEntity<String> isChecked = new HttpEntity<>("false");
    when(brainstormingService.validateOrRejectBrainstorming(brainstormingId, isChecked)).thenThrow(new BrainstormingNotFoundException("Cet brainstorming n'existe pas"));
    MockHttpServletResponse response = mockMvc.perform(put("/api/brainstorming/validate/"+brainstormingId).contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(asJsonString(isChecked))).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }

}
