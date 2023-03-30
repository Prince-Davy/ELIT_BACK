package com.elit.outildedecision.controller;

import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.model.SurveyAnswerRequestModel;
import com.elit.outildedecision.model.SurveyAnswerResponseModel;
import com.elit.outildedecision.service.SurveyAnswerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.elit.outildedecision.controller.ModelSamples.UserSamples;
import com.elit.outildedecision.controller.ModelSamples.SurveyAnswerSamples;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SurveyAnswerControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private SurveyAnswerService surveyAnswerService;

  @InjectMocks
  private SurveyAnswerController surveyAnswerController;

  private SurveyAnswerRequestModel surveyAnswerRequestModel;
  private SurveyAnswerResponseModel surveyAnswerResponseModel;

  private int pageNumber = 1;

  @BeforeEach
  public void setup() {
    surveyAnswerRequestModel = SurveyAnswerSamples.getSurveyAnswerRequestModel();
    surveyAnswerResponseModel = SurveyAnswerSamples.getSurveyAnswerResponseModel();
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldReplyToQuestion_Success() throws Exception {
    List<SurveyAnswerRequestModel> SurveyAnswerRequestModelList = Arrays.asList(surveyAnswerRequestModel);
    when(surveyAnswerService.replyToQuestion(SurveyAnswerRequestModelList)).thenReturn(ResponseEntity.ok().body("reponses enregistrées avec succès"));
    mockMvc.perform(post("/api/survey/answer")
         .contentType(MediaType.APPLICATION_JSON)
         .accept(MediaType.APPLICATION_JSON).content(ModelSamples.toJson(SurveyAnswerRequestModelList)))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("reponses enregis")));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldGetUserAnswer_Success() throws Exception {
      when(surveyAnswerService.getUserAnswer(UserSamples.getUser().getUserId())).thenReturn(Arrays.asList(surveyAnswerResponseModel));
      mockMvc.perform(get("/api/survey/answer/" + UserSamples.getUser().getUserId()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().json(ModelSamples.toJson(Arrays.asList(surveyAnswerResponseModel))));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldGetUserAnswer_ThrowUserNotFoundException() throws Exception {
    when(surveyAnswerService.getUserAnswer(UserSamples.getUser().getUserId())).thenThrow(new UserNotFoundException("UserNotFoundException"));
    mockMvc.perform(get("/api/survey/answer/" + UserSamples.getUser().getUserId()))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.status").value("NOT_FOUND"))
      .andExpect(jsonPath("$.message").value("UserNotFoundException"));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldGetSurveyReport_Success() throws Exception {
    when(surveyAnswerService.getSurveyReport(pageNumber)).thenReturn(Arrays.asList(surveyAnswerResponseModel));
    mockMvc.perform(get("/api/survey/answer/page/" + pageNumber))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().json(ModelSamples.toJson(Arrays.asList(surveyAnswerResponseModel))));

  }
}
