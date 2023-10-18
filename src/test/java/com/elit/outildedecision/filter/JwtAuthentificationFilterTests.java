package com.elit.outildedecision.filter;


import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.model.UserResponseModel;
import com.elit.outildedecision.service.implementation.UserServiceImpl;
import com.elit.outildedecision.util.ModelSamples;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.elit.outildedecision.security.AppUserPrincipal;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import com.elit.outildedecision.config.JwtProperties;
import com.elit.outildedecision.filters.JwtAuthenticationFilter;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JwtAuthenticationFilterTests {

  @Mock
  JwtProperties jwtProperties;

  @Mock
  AuthenticationManager authenticationManager;
  @Mock
  private UserServiceImpl customUserDetailsService;
  @MockBean
  private UserServiceImpl userService;

  @InjectMocks
  JwtAuthenticationFilter jwtAuthenticationFilter;
  private UserResponseModel userResponseModel;
  private User user;
  MockHttpServletResponse response;
  MockHttpServletRequest request;

  @BeforeEach
  void setUp(){
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    userResponseModel = ModelSamples.UserSamples.getUserResponseModel();
    user =  ModelSamples.UserSamples.getUser();

    Mockito.reset(authenticationManager);
  }
  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"},username = "user@user.com")
  void testAttemptAuthenticationSuccess() throws JsonProcessingException {

    Map<String, String> credentials = new HashMap<>();
    credentials.put("username", "john.doe");
    credentials.put("password", "password123");
    request.setContent(new ObjectMapper().writeValueAsBytes(credentials));
    when(userService.getAllInfosUserById(Mockito.any())).thenReturn(userResponseModel);
    when(customUserDetailsService.findByEmail(Mockito.anyString())).thenReturn(user);
    Authentication authExpected = mock(Authentication.class);
    when(authenticationManager.authenticate(any())).thenReturn(authExpected);

    Authentication authResult = jwtAuthenticationFilter.attemptAuthentication(request, response);

    assertEquals(authExpected, authResult);
  }


}
