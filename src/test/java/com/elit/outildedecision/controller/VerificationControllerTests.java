package com.elit.outildedecision.controller;

import com.elit.outildedecision.model.PasswordChangeModel;
import com.elit.outildedecision.service.VerificationTokenService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class VerificationControllerTests {

  @Autowired
  MockMvc mockMvc;
  @MockBean
  VerificationTokenService verificationTokenService;
  @InjectMocks
  VerificationController verificationController;

  private String token;

  @Test
  void shouldConfirmAccount_Success() throws Exception {
    String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBlbGl0ZS5mciIsImZpcnN0bmFtZSI6Ik5hbmN5Iiwicm9sZXMiOlsiQURNSU4iXSwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL2xvZ2luIiwiaWQiOjIsImV4cCI6MTY3OTA2MTMyMiwibGFzdG5hbWUiOiJNaWNoZWwifQ.l_tFKwjYbqjQYZmSZ4Q4Nh5839Pi7e_c6eOkr1PyxGg";
    when(verificationTokenService.confirmUser(token)).thenReturn(ResponseEntity.ok("Account confirmed"));
    mockMvc.perform(get("/confirm-account").param("token", token))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("Account confirmed")));
  }

  @Test
  void shouldConfirmAccount_ThrowInvalidException() throws Exception {
     token = "";
    when(verificationTokenService.confirmUser(token)).thenReturn(ResponseEntity.badRequest().body("Invalid token"));
    mockMvc.perform(get("/confirm-account").param("token", token))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(content().string(containsString("Invalid token")));
  }

  @Test
  void shouldConfirmAccount_ThrowTockenExpired() throws Exception {
     token = "";
    when(verificationTokenService.confirmUser(token)).thenReturn(ResponseEntity.badRequest().body("Token expired"));
    mockMvc.perform(get("/confirm-account").param("token", token))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(content().string(containsString("Token expired")));
  }

  @Test
  void shouldReinstateForgotPassword_Success() throws Exception {
     token = "";
    PasswordChangeModel passModel = ModelSamples.PasswordChangeModelSamples.getPasswordChangeModel();
    when(verificationTokenService.reinstateForgotPassword(token, passModel)).thenReturn(ResponseEntity.ok("Le mot de passe est mis à jour"));
    mockMvc.perform(post("/reset-password").param("token", token).contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE).content(ModelSamples.toJson(passModel)))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("Le mot de passe est ")));
  }

}
