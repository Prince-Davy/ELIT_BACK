package com.elit.outildedecision.shared.report.controller;

import com.elit.outildedecision.shared.report.exception.ReportNotFoundException;
import com.elit.outildedecision.shared.report.service.ReportService;
import com.elit.outildedecision.shared.exception.NotFoundException;
import com.elit.outildedecision.shared.project.exception.ProjectNotFoundException;
import com.elit.outildedecision.shared.util.ReportTypes;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReportControllerTest {

  @MockitoBean
  ReportService reportService;
  @InjectMocks
  ReportController reportController;
  Integer iteration;
  @Autowired
  private MockMvc mockMvc;
  private MultipartFile fileMultipart;
  private Long projectId, analysisId, reportId;
  private ReportTypes type;

  @BeforeEach
  public void setup() {
    fileMultipart = new MockMultipartFile(
      "file",
      "test contract.pdf",
      MediaType.APPLICATION_PDF_VALUE,
      "<<pdf data>>".getBytes(StandardCharsets.UTF_8));

    projectId = 1l;
    analysisId = 1l;
    reportId = 1l;
    iteration = 1;
    type = ReportTypes.MULTI_CRITERIA_ANALYSIS;
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldRejectUnknownReportTypeInPath() throws Exception {
    mockMvc.perform(multipart("/report/upload/1/1/unknown_report/1")
        .file((MockMultipartFile) fileMultipart))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.status").value(400));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldUploadReportIteration_Success() throws Exception {
    when(reportService.uploadReport2(projectId, analysisId, type, fileMultipart, iteration)).thenReturn("Fichier enregistré avec succès.");
    mockMvc.perform(multipart("/report/upload/" + projectId + "/" + analysisId + "/" + type + "/" + iteration).file((MockMultipartFile) fileMultipart))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("Fichier enregistré")));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldUploadReportIteration_ThrowPredictibilityAnalysisFoundException() throws Exception {
    when(reportService.uploadReport2(projectId, analysisId, type, fileMultipart, iteration)).thenThrow(new NotFoundException("Cette analyse de prévisibilité n'existe pas."));
    mockMvc.perform(multipart("/report/upload/" + projectId + "/" + analysisId + "/" + type + "/" + iteration).file((MockMultipartFile) fileMultipart))
      .andDo(print())
      .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404))
      .andExpect(jsonPath("$.message").value("Cette analyse de prévisibilité n'existe pas."));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldUploadReportIteration_ThrowMultiCreteriaException() throws Exception {
    when(reportService.uploadReport2(projectId, analysisId, type, fileMultipart, iteration)).thenThrow(new NotFoundException("Cette analyse multicritère n'existe pas."));
    mockMvc.perform(multipart("/report/upload/" + projectId + "/" + analysisId + "/" + type + "/" + iteration).file((MockMultipartFile) fileMultipart))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.status").value(404));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldUploadReportIteration_ThrowReportDoesntExist() throws Exception {
    when(reportService.uploadReport2(projectId, analysisId, type, fileMultipart, iteration)).thenThrow(new ProjectNotFoundException("Ce projet n'existe pas."));
    mockMvc.perform(multipart("/report/upload/" + projectId + "/" + analysisId + "/" + type + "/" + iteration).file((MockMultipartFile) fileMultipart))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.status").value(404))
      .andExpect(jsonPath("$.message").value("Ce projet n'existe pas."));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldUpdateReportIteration_Success() throws Exception {
    when(reportService.uploadAndUpdateReport2(projectId, analysisId, type, fileMultipart, iteration)).thenReturn("Le fichier a été mis à jours avec succès.");
    mockMvc.perform(multipart("/report/upload/" + projectId + "/" + analysisId + "/" + type + "/" + iteration + "/update").file((MockMultipartFile) fileMultipart).with(request -> {
        request.setMethod("PUT");
        return request;
      }))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("Le fichier a été mis à jours avec succès")));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldUpdateReportIteration_ThrowPredictibilityAnalysisFoundException() throws Exception {
    when(reportService.uploadAndUpdateReport2(projectId, analysisId, type, fileMultipart, iteration)).thenThrow(new NotFoundException("Cette analyse de prévisibilité n'existe pas."));
    mockMvc.perform(multipart("/report/upload/" + projectId + "/" + analysisId + "/" + type + "/" + iteration + "/update").file((MockMultipartFile) fileMultipart).with(request -> {
        request.setMethod("PUT");
        return request;
      }))
      .andDo(print())
      .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404))
      .andExpect(jsonPath("$.message").value("Cette analyse de prévisibilité n'existe pas."));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldUpdateReportIteration_ThrowMultiCreteriaException() throws Exception {
    when(reportService.uploadAndUpdateReport2(projectId, analysisId, type, fileMultipart, iteration)).thenThrow(new NotFoundException("Cette analyse multicritère n'existe pas."));
    mockMvc.perform(multipart("/report/upload/" + projectId + "/" + analysisId + "/" + type + "/" + iteration + "/update").file((MockMultipartFile) fileMultipart).with(request -> {
        request.setMethod("PUT");
        return request;
      }))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.status").value(404));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldUpdateReportIteration_ThrowReportDoesntExist() throws Exception {
    when(reportService.uploadAndUpdateReport2(projectId, analysisId, type, fileMultipart, iteration)).thenThrow(new ProjectNotFoundException("Ce projet n'existe pas."));
    mockMvc.perform(multipart("/report/upload/" + projectId + "/" + analysisId + "/" + type + "/" + iteration + "/update").file((MockMultipartFile) fileMultipart).with(request -> {
        request.setMethod("PUT");
        return request;
      }))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.status").value(404))
      .andExpect(jsonPath("$.message").value("Ce projet n'existe pas."));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldPreviewReport_Success() throws Exception {
    HttpServletResponse httpServletResponse = new MockHttpServletResponse();
    willDoNothing().given(reportService).previewReport(reportId, httpServletResponse);
    mockMvc.perform(get("/report/preview/" + reportId))
      .andExpect(status().isOk())
      .andDo(print());
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldDownoad_Success() throws Exception {
    HttpServletResponse httpServletResponse = new MockHttpServletResponse();
    willDoNothing().given(reportService).downloadReport(reportId, httpServletResponse);
    mockMvc.perform(get("/report/download/" + reportId))
      .andExpect(status().isOk())
      .andDo(print());
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "User"})
  void shouldGetReport_ThrowException() throws Exception {
    when(reportService.getReportsByProjectId(projectId)).thenThrow(new ProjectNotFoundException("Ce projet n'existe pas."));
    mockMvc.perform(get("/report/" + projectId))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.status").value(404))
      .andExpect(jsonPath("$.message").value("Ce projet n'existe pas."));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "User"})
  void shouldDeleteReportById_Success() throws Exception {
    when(reportService.deleteReportById(reportId)).thenReturn("Fichier supprimé avec succès");
    mockMvc.perform(delete("/report/" + projectId))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("Fichier supprimé avec succès")));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "User"})
  void shouldDeleteReportById_ThrowNotFound() throws Exception {
    when(reportService.deleteReportById(reportId)).thenThrow(new ReportNotFoundException("Le rapport avec l'ID 1 n'existe pas."));
    mockMvc.perform(delete("/report/" + projectId))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.status").value(404))
      .andExpect(jsonPath("$.message").value("Le rapport avec l'ID 1 n'existe pas."));
  }
}
