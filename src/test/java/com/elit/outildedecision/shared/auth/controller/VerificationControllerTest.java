package com.elit.outildedecision.shared.auth.controller;

import com.elit.outildedecision.shared.auth.dto.PasswordChangeRequest;
import com.elit.outildedecision.shared.auth.service.ConfirmationOutcome;
import com.elit.outildedecision.shared.auth.service.PasswordResetOutcome;
import com.elit.outildedecision.shared.auth.service.ResetEmailOutcome;
import com.elit.outildedecision.shared.auth.service.ResetPasswordEmailResult;
import com.elit.outildedecision.shared.auth.service.VerificationTokenService;
import com.elit.outildedecision.shared.util.ModelSamples;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class VerificationControllerTest {

  @Autowired
  MockMvc mockMvc;
  @MockitoBean
  VerificationTokenService verificationTokenService;
  @InjectMocks
  VerificationController verificationController;

  private String token;

  @Test
  void shouldConfirmAccount_Success() throws Exception {
    String validToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBlbGl0ZS5mciIsImZpcnN0bmFtZSI6Ik5hbmN5Iiwicm9sZXMiOlsiQURNSU4iXSwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL2xvZ2luIiwiaWQiOjIsImV4cCI6MTY3OTA2MTMyMiwibGFzdG5hbWUiOiJNaWNoZWwifQ.l_tFKwjYbqjQYZmSZ4Q4Nh5839Pi7e_c6eOkr1PyxGg";
    when(verificationTokenService.confirmUser(validToken)).thenReturn(ConfirmationOutcome.CONFIRMED);
    mockMvc.perform(get("/confirm-account").param("token", validToken))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("Account confirmed")));
  }

  @Test
  void shouldConfirmAccount_ThrowInvalidException() throws Exception {
    token = "";
    when(verificationTokenService.confirmUser(token)).thenReturn(ConfirmationOutcome.INVALID_TOKEN);
    mockMvc.perform(get("/confirm-account").param("token", token))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(content().string(containsString("Invalid token")));
  }

  @Test
  void shouldConfirmAccount_ThrowTockenExpired() throws Exception {
    token = "";
    when(verificationTokenService.confirmUser(token)).thenReturn(ConfirmationOutcome.EXPIRED);
    mockMvc.perform(get("/confirm-account").param("token", token))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(content().string(containsString("Token expired")));
  }

  @Test
  void shouldReinstateForgotPassword_Success() throws Exception {
    token = "";
    PasswordChangeRequest passModel = ModelSamples.PasswordChangeRequestSamples.getPasswordChangeRequest();
    when(verificationTokenService.reinstateForgotPassword(token, passModel)).thenReturn(PasswordResetOutcome.UPDATED);
    mockMvc.perform(post("/reset-password").param("token", token).contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE).content(ModelSamples.toJson(passModel)))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("Le mot de passe est ")));
  }

  @Test
  void shouldSendResetPasswordEmail_Success() throws Exception {
    token = "";
    when(verificationTokenService.sendResetPasswordEmail(any())).thenReturn(ResetPasswordEmailResult.of(ResetEmailOutcome.SENT));
    mockMvc.perform(post("/send-reset-password-email").contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE).content("{\"email\":\"test\"}"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("Email de récupération envoyé")));
  }

  @Test
  void shouldSendResetPasswordEmail_NotVerified() throws Exception {
    token = "";
    when(verificationTokenService.sendResetPasswordEmail(any()))
      .thenReturn(new ResetPasswordEmailResult(ResetEmailOutcome.NOT_VERIFIED, "exception"));
    mockMvc.perform(post("/send-reset-password-email").contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE).content("{\"email\":\"test\"}"))
      .andDo(print())
      .andExpect(status().isUnprocessableEntity())
      .andExpect(content().string(containsString("User verification error")));
  }

  @Test
  void shouldSendResetPasswordEmail_InvalidJson() throws Exception {
    token = "";
    when(verificationTokenService.sendResetPasswordEmail(any()))
      .thenReturn(new ResetPasswordEmailResult(ResetEmailOutcome.INVALID_JSON, "exception"));
    mockMvc.perform(post("/send-reset-password-email").contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE).content("{\"email\":\"test\"}"))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(content().string(containsString("JSON processing error")));
  }

  @Test
  void shouldSendResetPasswordEmail_ThrowException() throws Exception {
    token = "";
    when(verificationTokenService.sendResetPasswordEmail(any())).thenThrow(new RuntimeException("exception"));
    mockMvc.perform(post("/send-reset-password-email").contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE).content("{\"email\":\"test\"}"))
      .andDo(print())
      .andExpect(status().isInternalServerError())
      .andExpect(content().string(containsString("An unexpected error occurred")));
  }
}
