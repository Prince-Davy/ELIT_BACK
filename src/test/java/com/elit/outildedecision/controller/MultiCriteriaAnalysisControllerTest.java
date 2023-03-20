package com.elit.outildedecision.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.elit.outildedecision.entity.ProjectUserPermission;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.FormAlreadyExistsException;
import com.elit.outildedecision.service.MultiCriteriaAnalysisService;
import org.apache.tomcat.util.json.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class MultiCriteriaAnalysisControllerTest {
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  MultiCriteriaAnalysisService multiCriteriaAnalysisService;
  @WithMockUser(authorities = {"ADMIN"})
 // @WithMockUser("true")
 @ExtendWith(SpringExtension.class)
 // @PreAuthorize("isProjectOwner(#projectId)")
  @Test
  void createMultiCriteriaAnalysis() throws Exception {
    Long projectId = 1L;
    when(multiCriteriaAnalysisService.createMulticriteriaAnalysisForm(projectId)).thenReturn(ResponseEntity.ok().body("Votre Analyse multicritère est crée avec succés"));
    //MockHttpServletResponse response = mockMvc.perform(post("/api/multi-criteria-analysis/"+projectId)).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals("Votre Analyse multicritère est crée avec succés",multiCriteriaAnalysisService.createMulticriteriaAnalysisForm(projectId).getBody());


  }

  @Test
  void getMultiCriteriaAnalysis() {
  }

  @Test
  void increaseMulticriteriaIteration() {
  }

  @Test
  void updateMultiCriteriaAnalysis() {
  }

  @Test
  void updateQuestion() {
  }

  @Test
  void getMulticriteriaAnalysisScore() {
  }
}
