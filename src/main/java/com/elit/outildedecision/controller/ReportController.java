package com.elit.outildedecision.controller;

import com.elit.outildedecision.exception.*;
import com.elit.outildedecision.model.ReportResponseModel;
import com.elit.outildedecision.service.ReportService;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/report/")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping(value = "upload/{projectId}/{analyseId}/{type}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Object> uploadReport(@PathVariable Long projectId, @PathVariable Long analyseId, @PathVariable String type, @RequestParam("file") MultipartFile file) throws ProjectNotFoundException, MulticriteriaNotFoundException, ExecutionPlanNotFoundException, PredictibilityAnaliseNotFoundException {
        return reportService.uploadReport(projectId, analyseId, type, file);
    }

    @PostMapping(value = "upload/{projectId}/{analyseId}/{type}/{iteration}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Object> uploadReport2(@PathVariable Long projectId, @PathVariable Long analyseId, @PathVariable String type,  @RequestParam("file") MultipartFile file, @PathVariable Integer iteration) throws ProjectNotFoundException, MulticriteriaNotFoundException, ExecutionPlanNotFoundException, PredictibilityAnaliseNotFoundException {
        return reportService.uploadReport2(projectId, analyseId, type, file, iteration);
    }

    @RequestMapping("/preview/{reportId}")
    public void previewReport(HttpServletResponse response, @PathVariable Long reportId) throws IOException, ReportNotFoundException {
        reportService.previewReport(reportId, response);
    }

    @RequestMapping("download/{reportId}")
    public void downloadReport(@PathVariable Long reportId, HttpServletResponse response) throws IOException, ReportNotFoundException {
        reportService.downloadReport(reportId, response);
    }

    @GetMapping("{projectId}")
    public List<ReportResponseModel> getReportsByProjectId(@PathVariable Long projectId) throws ProjectNotFoundException {
        return reportService.getReportsByProjectId(projectId);
    }

    @DeleteMapping("{reportId}")
    public ResponseEntity<String> deleteReportById(@PathVariable Long reportId) throws ReportNotFoundException {
        return reportService.deleteReportById(reportId);
    }

    @PutMapping("{reportId}")
    public ResponseEntity<String> renameReportById(@PathVariable Long reportId, HttpEntity<String> name) throws IOException, ReportNotFoundException {
        return reportService.renameReportById(reportId, name);
    }
}
