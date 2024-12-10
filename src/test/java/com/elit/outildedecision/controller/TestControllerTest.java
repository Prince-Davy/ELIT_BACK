package com.elit.outildedecision.controller;

import com.elit.outildedecision.exception.RestResponseEntityExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class TestControllerTest {
  @InjectMocks
  private TestController testController;
  MockMvc mockMvc;
  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    mockMvc = standaloneSetup(testController).setControllerAdvice(RestResponseEntityExceptionHandler.class).build();
  }
  @Test
  void test1() throws Exception {
    // When
    mockMvc.perform(get("/test")
        .contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse();
    // Then
    assertEquals("Hello World",testController.test());

  }
}
