package com.elit.outildedecision.filter;


import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.elit.outildedecision.security.AppUserPrincipal;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.junit.jupiter.MockitoExtension;
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


@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTests {
    
  @Mock
  JwtProperties jwtProperties;

  @Mock
  AuthenticationManager authenticationManager;

  @InjectMocks
  JwtAuthenticationFilter jwtAuthenticationFilter;
  
  MockHttpServletResponse response;
  MockHttpServletRequest request;

  @BeforeEach
  void setUp(){
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    Mockito.reset(authenticationManager);
  }
  @Test
  void testAttemptAuthenticationSuccess() throws JsonProcessingException {
  
    Map<String, String> credentials = new HashMap<>();
    credentials.put("username", "john.doe");
    credentials.put("password", "password123");
    request.setContent(new ObjectMapper().writeValueAsBytes(credentials));

    Authentication authExpected = mock(Authentication.class);
    when(authenticationManager.authenticate(any())).thenReturn(authExpected);

    Authentication authResult = jwtAuthenticationFilter.attemptAuthentication(request, response);

    assertEquals(authExpected, authResult);
  }


}
