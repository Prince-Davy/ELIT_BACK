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
import com.elit.outildedecision.service.implementation.VerificationTokenServiceImpl;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

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

  @MockBean
  private PasswordEncoder passwordEncoder;

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
   HttpServletRequest request = new MockHttpServletRequest();
   final long EXPIRATION_TIME = 5 * 60 * 1000;
    String jwtSecret = "ELITE2022";
    Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
    User user = ModelSamples.UserSamples.getUser();
    String access_token =
      JWT.create()
        .withSubject(user.getUsername()) // the (email) of the authenticated user
        .withExpiresAt(
          new Date(
            System.currentTimeMillis() + EXPIRATION_TIME)) // the token will expire in 5min
        .withIssuer(
          request
            .getRequestURL()
            .toString()) // the issuer of the token is the URL of the server
        .withClaim(
          "roles","ADMIN")// the
        // roles
        // of
        // the
        // authenticated
        // user
        .withClaim(
          "firstname", user.getUserFirstName()) // the first name of the authenticated user
        .withClaim(
          "lastname", user.getUserLastName()) // the last name of the authenticated user
        .withClaim("id", user.getUserId()) // the user id of the authenticated user
        .sign(algorithm);


    when(userService.findByEmail(user.getUserEmail())).thenReturn(user);
    MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/refreshToken").header("Authorization", "Bearer " + access_token)
      .accept(MediaType.APPLICATION_JSON).content(String.valueOf(httpServletResponse))).andExpect(status().isOk()).andDo(print()).andReturn().getResponse();
    Assertions.assertEquals(403, response.getStatus());*/
  }
}
