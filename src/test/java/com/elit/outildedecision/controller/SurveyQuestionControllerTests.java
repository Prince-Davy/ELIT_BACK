package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.SurveyQuestion;
import com.elit.outildedecision.service.SurveyQuestionService;
import com.elit.outildedecision.util.ModelSamples;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SurveyQuestionControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  SurveyQuestionService surveyQuestionService;

  @InjectMocks
  SurveyQuestionController surveyQuestionController;

  private SurveyQuestion question;

  @BeforeEach
  public void setup() {
    ModelSamples.SurveyQuestionSamples.getSurveyQuestionRequestModel();
    question = ModelSamples.SurveyQuestionSamples.getSurveyQuestion();
  }

  @Test
  @WithMockUser(authorities = {"USER"})
  void shoudGetQuestionList_Success() throws Exception {
    when(surveyQuestionService.getQuestionsList()).thenReturn(Arrays.asList(question));
    mockMvc.perform(get("/survey/question"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().json(ModelSamples.toJson(Arrays.asList(question))));
  }
}
