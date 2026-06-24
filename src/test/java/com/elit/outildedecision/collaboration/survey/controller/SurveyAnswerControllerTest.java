package com.elit.outildedecision.collaboration.survey.controller;

import com.elit.outildedecision.collaboration.survey.dto.SurveyAnswerRequest;
import com.elit.outildedecision.collaboration.survey.dto.SurveyAnswerResponse;
import com.elit.outildedecision.collaboration.survey.service.SurveyAnswerService;
import com.elit.outildedecision.shared.user.exception.UserNotFoundException;
import com.elit.outildedecision.shared.util.ModelSamples;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.elit.outildedecision.shared.util.ModelSamples.UserSamples;
import com.elit.outildedecision.shared.util.ModelSamples.SurveyAnswerSamples;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SurveyAnswerControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private SurveyAnswerService surveyAnswerService;

  @InjectMocks
  private SurveyAnswerController surveyAnswerController;

  private SurveyAnswerRequest surveyAnswerRequestModel;
  private SurveyAnswerResponse surveyAnswerResponseModel;

  private int pageNumber = 1;

  @BeforeEach
  public void setup() {
    surveyAnswerRequestModel = SurveyAnswerSamples.getSurveyAnswerRequestModel();
    surveyAnswerResponseModel = SurveyAnswerSamples.getSurveyAnswerResponseModel();
  }

  @Test
  @WithMockUser(authorities = {"USER"})
  void shouldReplyToQuestion_Success() throws Exception {
    List<SurveyAnswerRequest> surveyAnswerRequestModelList = Arrays.asList(surveyAnswerRequestModel);
    when(surveyAnswerService.replyToQuestion(surveyAnswerRequestModelList)).thenReturn("reponses enregistrées avec succès");
    mockMvc.perform(post("/survey/answer")
         .contentType(MediaType.APPLICATION_JSON)
         .accept(MediaType.APPLICATION_JSON).content(ModelSamples.toJson(surveyAnswerRequestModelList)))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("reponses enregis")));
  }

  @Test
  @WithMockUser(authorities = {"USER"})
  void shouldGetUserAnswer_Success() throws Exception {
      when(surveyAnswerService.getUserAnswer(UserSamples.getUser().getUserId())).thenReturn(Arrays.asList(surveyAnswerResponseModel));
      mockMvc.perform(get("/survey/answer/" + UserSamples.getUser().getUserId()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().json(ModelSamples.toJson(Arrays.asList(surveyAnswerResponseModel))));
  }

  @Test
  @WithMockUser(authorities = {"USER"})
  void shouldGetUserAnswer_ThrowUserNotFoundException() throws Exception {
    when(surveyAnswerService.getUserAnswer(UserSamples.getUser().getUserId())).thenThrow(new UserNotFoundException("UserNotFoundException"));
    mockMvc.perform(get("/survey/answer/" + UserSamples.getUser().getUserId()))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.status").value(404))
      .andExpect(jsonPath("$.message").value("UserNotFoundException"));
  }
}
