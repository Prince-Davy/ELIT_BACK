package com.elit.outildedecision.controller;

import com.elit.outildedecision.exception.MulticriteriaNotFoundException;
import com.elit.outildedecision.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReportControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  ReportService reportService;

  @InjectMocks
  ReportController reportController;


  private MultipartFile fileMultipart;
  private Long projectId, analysisId, reportId;
  Integer iteration;
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
  @WithMockUser (authorities = {"ADMIN", "USER"})
  void shouldUploadReport_Success() throws Exception {
    when(reportService.uploadReport(projectId, analysisId,type, fileMultipart)).thenReturn(ResponseEntity.ok().body("Fichier enregistré avec succès."));
    mockMvc.perform(multipart("/api/report/upload/"+projectId +"/"+analysisId+"/"+type).file((MockMultipartFile) fileMultipart))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("Fichier enregistr")));
  }

  @Test
  @WithMockUser (authorities = {"ADMIN", "USER"})
  void shouldUploadReport_ThrowPredictibilityAnalysisFoundException() throws Exception {
    when(reportService.uploadReport(projectId, analysisId,type, fileMultipart)).thenThrow(new PredictibilityAnalysisNotFoundException("Cette analyse de prévisibilité n'existe pas."));
    mockMvc.perform(multipart("/api/report/upload/"+projectId +"/"+analysisId+"/"+type).file((MockMultipartFile) fileMultipart))
      .andDo(print())
      .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value("NOT_FOUND"))
      .andExpect(jsonPath("$.message").value("Cette analyse de prévisibilité n'existe pas."));;
  }

  @Test
  @WithMockUser (authorities = {"ADMIN", "USER"})
  void shouldUploadReport_ThrowMultiCreteriaException() throws Exception {
    when(reportService.uploadReport(projectId, analysisId,type, fileMultipart)).thenThrow( new MulticriteriaNotFoundException("Cette analyse multicritère n'existe pas."));
    mockMvc.perform(multipart("/api/report/upload/"+projectId +"/"+analysisId+"/"+type).file((MockMultipartFile) fileMultipart))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.status").value("NOT_FOUND"));
  }

  @Test
  @WithMockUser (authorities = {"ADMIN", "USER"})
  void shouldUploadReport_ThrowReportDoesntExist() throws Exception {
    when(reportService.uploadReport(projectId, analysisId,type, fileMultipart)).thenThrow(new ProjectNotFoundException("Ce projet n'existe pas."));
    mockMvc.perform(multipart("/api/report/upload/"+projectId +"/"+analysisId+"/"+type).file((MockMultipartFile) fileMultipart))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value("NOT_FOUND"))
      .andExpect(jsonPath("$.message").value("Ce projet n'existe pas."));;
  }

  @Test
  @WithMockUser (authorities = {"ADMIN", "USER"})
  void shouldUploadReportIteration_Success() throws Exception {
    when(reportService.uploadReport2(projectId, analysisId,type,fileMultipart, iteration)).thenReturn(ResponseEntity.ok().body("Fichier enregistré avec succès."));
    mockMvc.perform(multipart("/api/report/upload/"+projectId +"/"+analysisId+"/"+type+"/"+iteration).file((MockMultipartFile) fileMultipart))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string(containsString("Fichier enregistr")));
  }

  @Test
  @WithMockUser (authorities = {"ADMIN", "USER"})
  void shouldUploadReportIteration_ThrowPredictibilityAnalysisFoundException() throws Exception {
    when(reportService.uploadReport2(projectId, analysisId,type, fileMultipart, iteration)).thenThrow(new PredictibilityAnalysisNotFoundException("Cette analyse de prévisibilité n'existe pas."));
    mockMvc.perform(multipart("/api/report/upload/"+projectId +"/"+analysisId+"/"+type+"/"+iteration).file((MockMultipartFile) fileMultipart))
      .andDo(print())
      .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value("NOT_FOUND"))
      .andExpect(jsonPath("$.message").value("Cette analyse de prévisibilité n'existe pas."));;
  }

  @Test
  @WithMockUser (authorities = {"ADMIN", "USER"})
  void shouldUploadReportIteration_ThrowMultiCreteriaException() throws Exception {
    when(reportService.uploadReport2(projectId, analysisId,type, fileMultipart, iteration)).thenThrow( new MulticriteriaNotFoundException("Cette analyse multicritère n'existe pas."));
    mockMvc.perform(multipart("/api/report/upload/"+projectId +"/"+analysisId+"/"+type+"/"+iteration).file((MockMultipartFile) fileMultipart))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.status").value("NOT_FOUND"));
  }

  @Test
  @WithMockUser (authorities = {"ADMIN", "USER"})
  void shouldUploadReportIteration_ThrowReportDoesntExist() throws Exception {
    when(reportService.uploadReport2(projectId, analysisId,type, fileMultipart, iteration)).thenThrow(new ProjectNotFoundException("Ce projet n'existe pas."));
    mockMvc.perform(multipart("/api/report/upload/"+projectId +"/"+analysisId+"/"+type+"/"+iteration).file((MockMultipartFile) fileMultipart))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.status").value("NOT_FOUND"))
      .andExpect(jsonPath("$.message").value("Ce projet n'existe pas."));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldPreviewReport_Success() throws Exception {
    HttpServletResponse httpServletResponse = new MockHttpServletResponse();
    willDoNothing().given(reportService).previewReport(reportId,httpServletResponse);
    mockMvc.perform(get("/api/report/preview/"+ reportId))
      .andExpect(status().isOk())
      .andDo(print());
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "USER"})
  void shouldDownoad_Success() throws Exception {
    HttpServletResponse httpServletResponse = new MockHttpServletResponse();
    willDoNothing().given(reportService).downloadReport(reportId,httpServletResponse);
    mockMvc.perform(get("/api/report/download/"+ reportId))
      .andExpect(status().isOk())
      .andDo(print());
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "User"})
  void shouldGetReport_Sucess() throws Exception {
    when(reportService.getReportsByProjectId(projectId)).thenReturn(ModelSamples.ReportSamples.getReportResponseModels());
    mockMvc.perform(get("/api/report/"+projectId))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().json(ModelSamples.toJson(ModelSamples.ReportSamples.getReportResponseModels())));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "User"})
  void shouldGetReport_ThrowException() throws Exception {
    when(reportService.getReportsByProjectId(projectId)).thenThrow(new ProjectNotFoundException("Ce projet n'existe pas."));
    mockMvc.perform(get("/api/report/"+projectId))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.status").value("NOT_FOUND"))
      .andExpect(jsonPath("$.message").value("Ce projet n'existe pas."));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "User"})
  void shouldDeleteReportById_Success() throws Exception {
    when(reportService.deleteReportById(reportId)).thenReturn(ResponseEntity.ok().body("Fichier supprimé avec succès"));
    mockMvc.perform(delete("/api/report/"+projectId))
      .andDo(print())
      .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(authorities = {"ADMIN", "User"})
  void shouldDeleteReportById_ThrowException() throws Exception {
    when(reportService.deleteReportById(reportId)).thenReturn(ResponseEntity.badRequest().body("Erreur lors de la suppression du fichier."));
    mockMvc.perform(delete("/api/report/"+projectId))
      .andDo(print())
      .andExpect(status().isBadRequest());
  }
}
