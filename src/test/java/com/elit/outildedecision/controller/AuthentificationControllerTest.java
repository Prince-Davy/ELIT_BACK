package com.elit.outildedecision.controller;

import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.UserAlreadyExistsException;
import com.elit.outildedecision.model.UserModel;
import com.elit.outildedecision.repository.UserRepository;
import com.elit.outildedecision.service.implementation.UserServiceImpl;

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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthentificationControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  AuthenticationManager authenticationManager;
  @MockBean
  private UserServiceImpl userService;
  @MockBean
  private UserRepository userRepository;

  @MockBean
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  AuthentificationController authentificationController;

  public static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  @Test
  void register_whenUserModel_isConform() throws Exception {
    UserModel userModel = ModelSamples.UserSamples.getUserModel();
    when(userService.saveUser(userModel)).thenReturn(ResponseEntity.ok().body("Votre compte a été créé avec succès"));
    MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post("/api/register").contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(asJsonString(userModel))).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }
  @Test
  void register_whenUserAlreadyExists() throws Exception {
    UserModel userModel = ModelSamples.UserSamples.getUserModel();
    User user = ModelSamples.UserSamples.getUser();
    when(userService.saveUser(userModel)).thenThrow(new UserAlreadyExistsException("Utilisateur déjà existant"));
    MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post("/api/register").contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(asJsonString(user))).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }

}


