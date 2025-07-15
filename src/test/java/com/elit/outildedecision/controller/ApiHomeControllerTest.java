package com.elit.outildedecision.controller;

import com.elit.outildedecision.exception.RestResponseEntityExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@RunWith(MockitoJUnitRunner.class)
class ApiHomeControllerTest {
  @InjectMocks
  private ApiHomeController apiHomeController;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    mockMvc = standaloneSetup(apiHomeController).setControllerAdvice(RestResponseEntityExceptionHandler.class).build();
  }
  @Test
  void index() throws Exception {
    mockMvc.perform(get("/toto"))
      .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
  }
}
