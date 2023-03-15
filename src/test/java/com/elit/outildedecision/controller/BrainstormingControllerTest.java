package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.PredictibilityAnalysis;
import com.elit.outildedecision.model.FormStepModelForBrainstorming;
import com.elit.outildedecision.repository.PredictibilityAnalysisRepository;
import com.elit.outildedecision.service.implementation.BrainstormingServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class BrainstormingControllerTest {
  @Autowired
  private MockMvc mockMvc;
  @InjectMocks
  private BrainstormingServiceImpl brainstormingService;
  @MockBean
  private PredictibilityAnalysisRepository predictibilityAnalysisRepository;
  @InjectMocks
  BrainstormingController  brainstormingController;

  @Test
  void getBrainstorming() throws Exception {
   Long predictibilityAnalysisId = 1L;
    FormStepModelForBrainstorming modelForBrainstorming= ModelSamples.UserSamples.getFormStepModelForBrainstorming();
    List<FormStepModelForBrainstorming> result = new ArrayList<>();
    result.add(modelForBrainstorming);
    PredictibilityAnalysis predictibilityAnalysis = new PredictibilityAnalysis();
    predictibilityAnalysis.setPredictibilityAnalysisId(predictibilityAnalysisId);
   // predictibilityAnalysis.setMultiCriteriaAnalysis();
    when(predictibilityAnalysisRepository.findById(predictibilityAnalysisId)).thenReturn(Optional.of(predictibilityAnalysis));
    ResultActions response = mockMvc.perform(get("/api/brainstorming/"+ predictibilityAnalysisId));
    response.andExpect(status().isOk())
      .andDo(print());
    Assertions.assertEquals(200, response.andReturn().getResponse().getStatus());
  }

  @Test
  void getBrainstorming_whenPredictibilityAnalysis_isNotFound() throws Exception {
    Long predictibilityAnalysisId = 1L;
    FormStepModelForBrainstorming modelForBrainstorming= ModelSamples.UserSamples.getFormStepModelForBrainstorming();
    List<FormStepModelForBrainstorming> result = new ArrayList<>();
    result.add(modelForBrainstorming);
    PredictibilityAnalysis predictibilityAnalysis = new PredictibilityAnalysis();
    predictibilityAnalysis.setPredictibilityAnalysisId(predictibilityAnalysisId);
    when( predictibilityAnalysisRepository.findById(predictibilityAnalysisId)).thenReturn(Optional.empty());
    ResultActions response = mockMvc.perform(get("/api/brainstorming/"+ predictibilityAnalysisId));
    response.andExpect(status().isNotFound())
      .andDo(print());
    Assertions.assertEquals(404, response.andReturn().getResponse().getStatus());
  }

  @Test
  void getBrainstormingAbstract() {
  }

  @Test
  void createBrainstorming() {
  }

  @Test
  void updateBrainstorming() {
  }

  @Test
  void removeBrainstorming() {
  }

  @Test
  void validateOrRejectBrainstorming() {
  }
}
