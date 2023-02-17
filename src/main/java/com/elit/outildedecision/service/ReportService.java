package com.elit.outildedecision.service;

import com.elit.outildedecision.exception.*;
import com.elit.outildedecision.model.ReportResponseModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface ReportService {
  ResponseEntity<Object> uploadReport(
      Long projectId, Long analyseId, String type, MultipartFile file)
      throws ProjectNotFoundException, PredictibilityAnaliseNotFoundException,
          MulticriteriaNotFoundException, ExecutionPlanNotFoundException;

  ResponseEntity<Object> uploadReport2(
      Long projectId, Long analyseId, String type, MultipartFile file, Integer iteration)
      throws ProjectNotFoundException, PredictibilityAnaliseNotFoundException,
          MulticriteriaNotFoundException, ExecutionPlanNotFoundException;

  void previewReport(Long reportId, HttpServletResponse response)
      throws ReportNotFoundException, IOException;

  void downloadReport(Long reportId, HttpServletResponse response)
      throws IOException, ReportNotFoundException;

  List<ReportResponseModel> getReportsByProjectId(Long projectId) throws ProjectNotFoundException;

  ResponseEntity<String> deleteReportById(Long reportId) throws ReportNotFoundException;

  ResponseEntity<String> renameReportById(Long reportId, HttpEntity<String> name)
      throws IOException, ReportNotFoundException;
}
