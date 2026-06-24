package com.elit.outildedecision.shared.auth.controller;

import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.user.exception.CouldNotSaveUserException;
import com.elit.outildedecision.shared.user.exception.UserAlreadyExistsException;
import com.elit.outildedecision.shared.auth.dto.UserRequest;
import com.elit.outildedecision.shared.user.repository.UserRepository;
import com.elit.outildedecision.shared.user.service.implementation.UserServiceImpl;

import com.elit.outildedecision.shared.util.ModelSamples;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
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
  @MockitoBean
  private UserServiceImpl userService;
  @MockitoBean
  private UserRepository userRepository;
  @MockitoBean
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
  @WithMockUser(authorities = {"USER", "ADMIN"})
  @Test
  void register_whenUserRequest_isConform() throws Exception {
    UserRequest userModel = ModelSamples.UserSamples.getUserRequest();
    when(userService.saveUser(userModel)).thenReturn("Votre compte a été créé avec succès");
    MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post("/register").contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(asJsonString(userModel))).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(201, response.getStatus());
  }
  //@Test
  void register_whenUserAlreadyExists() throws Exception {
    UserRequest userModel = ModelSamples.UserSamples.getUserRequest();
    User user = ModelSamples.UserSamples.getUser();
    when(userService.saveUser(userModel)).thenThrow(new UserAlreadyExistsException("Utilisateur déjà existant"));
    MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post("/register").contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(asJsonString(user))).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }

  @Test
  void register_couldnotSaveUserException() throws Exception {
    User user = ModelSamples.UserSamples.getUser();
    when(userService.saveUser(any())).thenThrow(new CouldNotSaveUserException("Une erreur est survenue lors de la création de votre compte."));
    MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post("/register").contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(asJsonString(user))).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(400, response.getStatus());
  }
}


