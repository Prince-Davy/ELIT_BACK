package com.elit.outildedecision.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.CouldNotSaveUserException;
import com.elit.outildedecision.exception.UserAlreadyExistsException;
import com.elit.outildedecision.model.UserModel;
import com.elit.outildedecision.repository.UserRepository;
import com.elit.outildedecision.service.implementation.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.RequestEntity.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthentificationController.class)
@ExtendWith(MockitoExtension.class)
class AuthentificationControllerTest {
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private UserServiceImpl userService;
  @MockBean
  private UserRepository userRepository;

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
    when(userRepository.findByUseremailOrUsername(userModel.getUserEmail(), userModel.getUsername())).thenReturn(null);
    when(userService.saveUser(userModel)).thenReturn(ResponseEntity.ok().body("Votre compte a été créé avec succès"));
    MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post("/register").contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(asJsonString(userModel))).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }
  @Test
  void register_whenUserAlreadyExists() throws Exception {
    UserModel userModel = ModelSamples.UserSamples.getUserModel();
    User user = ModelSamples.UserSamples.getUser();
    when(userRepository.findByUseremailOrUsername(userModel.getUserEmail(), userModel.getUsername())).thenReturn(user);
    when(userService.saveUser(userModel)).thenThrow(new UserAlreadyExistsException("Utilisateur déjà existant"));
    MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post("/register").contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON).content(asJsonString(user))).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(200, response.getStatus());
  }
  @WithMockUser(authorities = {"USER", "ADMIN"})
  @Test
  void refreshToken() throws Exception {
   /* HttpServletResponse httpServletResponse = new MockHttpServletResponse();
    String jwtToken = "eyJ0eXAiOiJKV1QiLCJraWQiOiJzaWduYXR1cmUta2V5IiwiYWxnIjoiUlMyNTYifQ.eyJzY29wZSI6eyJlbWFpbCI6ImRlbW9AZW1haWwuY29tIiwiY2l2aWxpdGUiOiJNIiwibmFtZSI6ImRlbW9OYW1lIiwidXNlck5hbWUiOiJkZW1vIiwiYXV0b3Jpc2F0aW9uIjoidHJ1ZSIsIm1zaXNkbiI6IjA2MTEyMjMzNDQiLCJwYXJ0bmVybmFtZSI6Ik9DUyIsImFkcmVzc2UiOiIxMyBhdmVudWUgZHUgTWFyZWNoYWwgSlVJTiIsImNwIjoiOTI1MDAiLCJ2aWxsZSI6Ik1ldWRvbiIsImRhdGVfZGVfbmFpc3NhbmNlIjoiMDEvMDEvMTk5MCIsImxpZXVfZGVfbmFpc3NhbmNlIjoiUEFSSVMifX0.K7rQGLpO4zBzwz2DfolAJCUuoG9W55bhIGHEtPtryREodZx1XooPwAQWU8rbaNIuNpC4iFf_3AzMjfqbBoiBD6y6-n86wTQ2V9SSemm392oEQ4WY_6vIbSq87Z4k9SZlXMv89x9Uv3Oz2GKC8JQl17h2ytVJRsTcpSjv2vNHZf2xeVEF7NiQ0gHuCTNtmC4fHR3Oo7FmuhI770K29yrh7A1xPzfrWi-m1HtFM_teSSabn41UZujRhwf-1G3KNRQ9HGmvev0BUkHQ3nt_9gQNys0Zs8rvFca3l3RHezAd6am6GTv9-7zQwZvB1x6MdJzZ-Fy3WSLurSo6ls_DhwD8Yw";
    String jwtSecret = "ELITE2022";

    User user = ModelSamples.UserSamples.getUser();
    String jwtAuthorizationToken = "Bearer " + jwtToken;


    when(userService.findByEmail(user.getUserEmail())).thenReturn(user);
    MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/refreshToken").header("Authorization", "Bearer " + jwtToken)
      .accept(MediaType.APPLICATION_JSON).content(String.valueOf(httpServletResponse))).andExpect(status().isBadRequest()).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(400, response.getStatus());*/
  }
}
