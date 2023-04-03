package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.SurveyQuestion;
import com.elit.outildedecision.exception.QuestionFormatNotValidException;
import com.elit.outildedecision.model.SurveyQuestionModel;
import com.elit.outildedecision.service.SurveyQuestionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SurveyQuestionControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  SurveyQuestionService surveyQuestionService;

  @InjectMocks
  SurveyQuestionController surveyQuestionController;

  private SurveyQuestion question;
  private SurveyQuestionModel questionModel;

  @BeforeEach
  public void setup() {
    questionModel = ModelSamples.SurveyQuestionSamples.getSurveyQuestionRequestModel();
     question = ModelSamples.SurveyQuestionSamples.getSurveyQuestion();
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldCreateQuestion_Success() throws Exception {
    when(surveyQuestionService.createQuestion(questionModel)).thenReturn(question);
    mockMvc.perform(post("/api/survey/question").contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(ModelSamples.toJson(question)))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().json(ModelSamples.toJson(question)));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shoudCreateQuestion_ThrowQuestionFormatNotValidException() throws Exception {
  when(surveyQuestionService.createQuestion(null)).thenThrow(new QuestionFormatNotValidException("question format not valid"));
  mockMvc.perform(post("/api/survey/question").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
    .andDo(print())
    .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldModifyQuestion_Success() throws Exception {
    question.setQuestion("this is modified question");
    when(surveyQuestionService.modifyQuestion(question.getId(), questionModel)).thenReturn(question);
    mockMvc.perform(put("/api/survey/question/"+ question.getId()).contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(ModelSamples.toJson(questionModel)))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().json(ModelSamples.toJson(question)));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shoudModifyQuestion_ThrowQuestionFormatNotValidException() throws Exception {
    when(surveyQuestionService.modifyQuestion(question.getId(),null)).thenThrow(new QuestionFormatNotValidException("question format not valid"));
    mockMvc.perform(put("/api/survey/question/"+ question.getId()).contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON))
      .andDo(print())
      .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldDeleteQuestion_Success() throws Exception {
    when(surveyQuestionService.deleteQuestion(question.getId())).thenReturn(Optional.of(question));
    mockMvc.perform(delete("/api/survey/question/"+ question.getId()))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().json(ModelSamples.toJson(question)));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldDeleteQuestion_ThrowQuestionNotFound() throws Exception {
    when(surveyQuestionService.deleteQuestion(question.getId())).thenReturn(Optional.empty());
    mockMvc.perform(delete("/api/survey/question/"+ question.getId()))
      .andDo(print())
      .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shoudGetQuestionList_Success() throws Exception {
    when(surveyQuestionService.getQuestionsList()).thenReturn(Arrays.asList(question));
    mockMvc.perform(get("/api/survey/question"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().json(ModelSamples.toJson(Arrays.asList(question))));
  }

  @Test
  @WithMockUser
  void shouldSelectQuestion_Success() throws Exception {
    when(surveyQuestionService.selectQuestion(question.getId())).thenReturn(question);
    mockMvc.perform(get("/api/survey/question/"+question.getId()))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().json(ModelSamples.toJson(question)));
  }
}
