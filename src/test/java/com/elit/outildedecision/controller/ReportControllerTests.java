package com.elit.outildedecision.controller;

import com.elit.outildedecision.exception.MulticriteriaNotFoundException;
import com.elit.outildedecision.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.ReportNotFoundException;
import com.elit.outildedecision.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReportControllerTests {

  @MockitoBean
  ReportService reportService;
  @InjectMocks
  ReportController reportController;
  Integer iteration;
  @Autowired
  private MockMvc mockMvc;
  private MultipartFile fileMultipart;
  private Long projectId, analysisId, reportId;
  private String type;

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
    type = "type";
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldUploadReport_Success() throws Exception {
    when(reportService.uploadReport(projectId, analysisId, type, fileMultipart)).thenReturn(ResponseEntity.ok().body("Fichier enregistré avec succès."));
    mockMvc.perform(multipart("/report/upload/" + projectId + "/" + analysisId + "/" + type).file((MockMultipartFile) fileMultipart))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("Fichier enregistr")));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldUploadReport_ThrowPredictibilityAnalysisFoundException() throws Exception {
    when(reportService.uploadReport(projectId, analysisId, type, fileMultipart)).thenThrow(new PredictibilityAnalysisNotFoundException("Cette analyse de prévisibilité n'existe pas."));
    mockMvc.perform(multipart("/report/upload/" + projectId + "/" + analysisId + "/" + type).file((MockMultipartFile) fileMultipart))
      .andDo(print())
      .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404))
      .andExpect(jsonPath("$.message").value("Cette analyse de prévisibilité n'existe pas."));
    ;
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldUploadReport_ThrowMultiCreteriaException() throws Exception {
    when(reportService.uploadReport(projectId, analysisId, type, fileMultipart)).thenThrow(new MulticriteriaNotFoundException("Cette analyse multicritère n'existe pas."));
    mockMvc.perform(multipart("/report/upload/" + projectId + "/" + analysisId + "/" + type).file((MockMultipartFile) fileMultipart))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.status").value(404));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldUploadReport_ThrowReportDoesntExist() throws Exception {
    when(reportService.uploadReport(projectId, analysisId, type, fileMultipart)).thenThrow(new ProjectNotFoundException("Ce projet n'existe pas."));
    mockMvc.perform(multipart("/report/upload/" + projectId + "/" + analysisId + "/" + type).file((MockMultipartFile) fileMultipart))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404))
      .andExpect(jsonPath("$.message").value("Ce projet n'existe pas."));
    ;
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldUploadReportIteration_Success() throws Exception {
    when(reportService.uploadReport2(projectId, analysisId, type, fileMultipart, iteration)).thenReturn(ResponseEntity.ok().body("Fichier enregistré avec succès."));
    mockMvc.perform(multipart("/report/upload/" + projectId + "/" + analysisId + "/" + type + "/" + iteration).file((MockMultipartFile) fileMultipart))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("Fichier enregistré")));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldUploadReportIteration_ThrowPredictibilityAnalysisFoundException() throws Exception {
    when(reportService.uploadReport2(projectId, analysisId, type, fileMultipart, iteration)).thenThrow(new PredictibilityAnalysisNotFoundException("Cette analyse de prévisibilité n'existe pas."));
    mockMvc.perform(multipart("/report/upload/" + projectId + "/" + analysisId + "/" + type + "/" + iteration).file((MockMultipartFile) fileMultipart))
      .andDo(print())
      .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404))
      .andExpect(jsonPath("$.message").value("Cette analyse de prévisibilité n'existe pas."));
    ;
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldUploadReportIteration_ThrowMultiCreteriaException() throws Exception {
    when(reportService.uploadReport2(projectId, analysisId, type, fileMultipart, iteration)).thenThrow(new MulticriteriaNotFoundException("Cette analyse multicritère n'existe pas."));
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
    when(reportService.uploadAndUpdateReport2(projectId, analysisId, type, fileMultipart, iteration)).thenReturn(ResponseEntity.ok().body("Le fichier a été mis à jours avec succès."));
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
    when(reportService.uploadAndUpdateReport2(projectId, analysisId, type, fileMultipart, iteration)).thenThrow(new PredictibilityAnalysisNotFoundException("Cette analyse de prévisibilité n'existe pas."));
    mockMvc.perform(multipart("/report/upload/" + projectId + "/" + analysisId + "/" + type + "/" + iteration + "/update").file((MockMultipartFile) fileMultipart).with(request -> {
        request.setMethod("PUT");
        return request;
      }))
      .andDo(print())
      .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404))
      .andExpect(jsonPath("$.message").value("Cette analyse de prévisibilité n'existe pas."));
    ;
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldUpdateReportIteration_ThrowMultiCreteriaException() throws Exception {
    when(reportService.uploadAndUpdateReport2(projectId, analysisId, type, fileMultipart, iteration)).thenThrow(new MulticriteriaNotFoundException("Cette analyse multicritère n'existe pas."));
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

  /*
    @Test
    @WithMockUser(authorities = {"ADMIN", "User"})
    void shouldGetReport_Sucess() throws Exception {
      when(reportService.getReportsByProjectId(projectId)).thenReturn(ModelSamples.ReportSamples.getReportResponseModels());
      mockMvc.perform(get("/report/"+projectId))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().json(ModelSamples.toJson(ModelSamples.ReportSamples.getReportResponseModels())));
    }
  */
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
    when(reportService.deleteReportById(reportId)).thenReturn(ResponseEntity.ok().body("Fichier supprimé avec succès"));
    mockMvc.perform(delete("/report/" + projectId))
      .andDo(print())
      .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "User"})
  void shouldDeleteReportById_ThrowException() throws Exception {
    when(reportService.deleteReportById(reportId)).thenReturn(ResponseEntity.badRequest().body("Erreur lors de la suppression du fichier."));
    mockMvc.perform(delete("/report/" + projectId))
      .andDo(print())
      .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "User"})
  void shouldRenameReportById_Success() throws Exception {
    when(reportService.renameReportById(eq(reportId), any())).thenReturn(ResponseEntity.ok().body("Le fichier a été mis à jour."));
    mockMvc.perform(put("/report/" + reportId))
      .andDo(print())
      .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "User"})
  void shouldRenameReportById_ThrowException() throws Exception {
    when(reportService.renameReportById(eq(reportId), any())).thenThrow(new ReportNotFoundException("Ce report n'existe pas."));
    mockMvc.perform(put("/report/" + reportId))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.status").value(404))
      .andExpect(jsonPath("$.message").value("Ce report n'existe pas."));
  }
}
