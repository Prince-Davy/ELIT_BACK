package com.elit.outildedecision.controller;

import com.elit.outildedecision.exception.OpinionAlreadyExistException;
import com.elit.outildedecision.model.OpinionModel;
import com.elit.outildedecision.service.OpinionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpinionControllerTest {
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  OpinionService opinionService;
  @InjectMocks
  OpinionController OpinionController;

  public static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void createOpinion() throws Exception {
    OpinionModel opinionModel = new OpinionModel();
    opinionModel.setDescription("description");
    opinionModel.setRating(2);
    when(opinionService.createOpinion(opinionModel)).thenReturn(ResponseEntity.ok().body("L'avis a bien été enregistré."));
    MockHttpServletResponse response = mockMvc.perform(post("/opinion").contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(asJsonString(opinionModel) )).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200,response.getStatus());
  }
  @Test
  void createOpinion_whenOpinionOfUserAlreadyExists() throws Exception {
    OpinionModel opinionModel = new OpinionModel();
    opinionModel.setDescription("description");
    opinionModel.setRating(2);
    when(opinionService.createOpinion(opinionModel)).thenThrow(new OpinionAlreadyExistException("Cet utilisateur à déjà renseigné un avis."));
    MockHttpServletResponse response = mockMvc.perform(post("/opinion").contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(asJsonString(opinionModel) )).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(409,response.getStatus());
  }

  @Test
  void getOpinionIsAlreadyExist() throws Exception {
    when(opinionService.getOpinionIsAlreadyExist()).thenReturn(true);
    MockHttpServletResponse response = mockMvc.perform(get("/opinion/user")).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200,response.getStatus());
  }
  @Test
  void getOpinion_whenOpinionDoseNotExist() throws Exception {
    when(opinionService.getOpinionIsAlreadyExist()).thenReturn(false);
    MockHttpServletResponse response = mockMvc.perform(get("/opinion/user")).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200,response.getStatus());
  }
}
