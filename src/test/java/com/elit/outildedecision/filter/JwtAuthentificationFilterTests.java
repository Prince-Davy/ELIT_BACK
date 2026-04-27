package com.elit.outildedecision.filter;

import com.elit.outildedecision.config.JwtProperties;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.filters.JwtAuthenticationFilter;
import com.elit.outildedecision.model.UserResponseModel;
import com.elit.outildedecision.service.implementation.UserServiceImpl;
import com.elit.outildedecision.util.ModelSamples;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


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
  @MockitoBean
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
