package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.*;
import com.elit.outildedecision.repository.CriteriaRepository;
import com.elit.outildedecision.repository.FormStepRepo;
import com.elit.outildedecision.repository.MultiCriteriaAnalysisRepository;
import com.elit.outildedecision.repository.ReportRepository;
import com.elit.outildedecision.util.ReportTypes;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.List;

@Service
public class ScoreServiceImpl {
  private final CriteriaRepository criteriaRepository;
  private final MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository;
  private final FormStepRepo formStepRepo;
  private final ReportRepository reportRepository;
  @Value("${file.upload-dir}")
  String STORING_FOLDER;

  public ScoreServiceImpl(
      CriteriaRepository criteriaRepository,
      MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository,
      FormStepRepo formStepRepo,
      ReportRepository reportRepository) {
    this.criteriaRepository = criteriaRepository;
    this.multiCriteriaAnalysisRepository = multiCriteriaAnalysisRepository;
    this.formStepRepo = formStepRepo;
    this.reportRepository = reportRepository;
  }

  /**
   * @param formStep
   * @return Map<String, Float>
   */
  // @Override
  public Map<String, Float> calculateScore(FormStep formStep) {
    // Map<String, Float> stepResults = new HashMap<>();
    // Set<StepWeight> stepWeights = formStep.getStepWeights();
    // Set<FormQuestion> formQuestions = formStep.getFormQuestions();
    // Float psiDasp = calculatePsi(formQuestions, stepWeights);
    // Float maxDaspi = calculMaxDaspi(formQuestions, stepWeights);
    // stepResults.put("psiDasp", psiDasp);
    // stepResults.put("aMax", maxDaspi);
    // stepResults.put("finalResult", maxDaspi!=0? psiDasp/maxDaspi : 0);
    // return stepResults;
    return null;
  }
}

// @Override
// @Transactional
// public ResponseEntity<byte[]> generateReportPdf(Long formId) throws
// IOException {
// List<FormStep> formSteps = formStepRepo.findByFormId(formId);}
// Project project =
// multiCriteriaAnalysisRepository.findById(formId).get().getProject();
// ByteArrayOutputStream baos = new ByteArrayOutputStream();
// Document document = new Document(PageSize.A4);
// PdfWriter pdfWriter = PdfWriter.getInstance(document, baos);
// document.open();
// Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
// fontTitle.setSize(22);
// fontTitle.setColor(Color.RED);
// Paragraph title = new Paragraph("RAPPORT PMM DU PROJET : " +
// formSteps.get(0).getMultiCriteriaAnalysis().getProject().getName()
// , fontTitle);
// title.setAlignment(Element.ALIGN_CENTER);
// document.add(title);
// for (FormStep formStep : formSteps) {
// writeDocumentPart(document, formStep);
// }
// document.addTitle("RAPPORT PMM DU PROJET : " +
// formSteps.get(0).getMultiCriteriaAnalysis().getProject().getName());
// document.close();
// pdfWriter.flush();
// byte[] pdfAsBytes = baos.toByteArray();
// InputStream inputStream = new ByteArrayInputStream(pdfAsBytes);
// String filePath =
// StringUtils.cleanPath(STORING_FOLDER+project.getFolderPath()+"\\"+"RAPPORT_PMM_"+project.getName()+".pdf");
// Files.copy(inputStream, Path.of(filePath),
// StandardCopyOption.REPLACE_EXISTING);
// /*File file =
// googleDriveService.createGoogleFile(multiCriteriaAnalysisRepository.getById(formId).getProject().getFolderPath(),
// "application/pdf",
// "Rapport.pdf",
// targetStream);
// googleDriveService.createPublicPermission(file.getId());*/
// Report report = Report.builder()
// .project(multiCriteriaAnalysisRepository.getById(formId).getProject())
// .type(ReportTypes.MULTI_CRITERIA_ANALYSIS.name())
// .project(project)
// .rapportPath(filePath)
// .build();
// reportRepository.save(report);
// HttpHeaders headers = new HttpHeaders();
// headers.add("Content-Disposition", "attachment; filename=" +
// "RAPPORT_PMM_"+project.getName()+".pdf");
// headers.add("Content-Type", "application/pdf");
// return ResponseEntity.ok().headers(headers).body(pdfAsBytes);
// }
// @Override
// @Transactional
// public ResponseEntity<byte[]> previewReportOnline(Long reportId) throws
// IOException {
// Report report = reportRepository.findById(reportId)
// .orElseThrow(() -> new IllegalArgumentException("Invalid report Id:" +
// reportId));
// HttpHeaders headers = new HttpHeaders();
// headers.add("Content-Disposition", "inline; filename=" +
// "RAPPORT_PMM_"+report.getProject().getName()+".pdf");
// headers.add("Content-Type", "application/pdf");
// return
// ResponseEntity.ok().headers(headers).body(Files.readAllBytes(Path.of(report.getRapportPath())));
// }
//
// private void writeDocumentPart(Document document, FormStep formStep) {
// //Get the step Scores
// Map<String, Float> stepScores = calculateScore(formStep);
// //Setting Up Fonts For the Document
// Font stepFont = FontFactory.getFont(FontFactory.TIMES_BOLD);
// stepFont.setSize(14);
// stepFont.setColor(Color.orange);
//
// Font questionFont = FontFactory.getFont(FontFactory.TIMES_BOLDITALIC);
// questionFont.setSize(11);
// questionFont.setColor(Color.BLACK);
//
// Font answerFont = FontFactory.getFont(FontFactory.TIMES_ITALIC);
// answerFont.setSize(11);
// answerFont.setColor(Color.darkGray);
// // Step Section
// Paragraph paragraph = new Paragraph("Aspect : " + formStep.getStepName(),
// stepFont);
// paragraph.setAlignment(Element.ALIGN_CENTER);
// document.add(paragraph);
// PdfPTable weightTable = new PdfPTable(formStep.getStepWeights().size() + 1);
// weightTable.addCell(new PdfPCell(new Phrase("Critère : ")));
// for (StepWeight stepWeight : formStep.getStepWeights()) {
// PdfPCell cell = new PdfPCell(new Phrase(stepWeight.getCriteriaName()));
// cell.setPadding(Element.ALIGN_CENTER);
// weightTable.addCell(cell);
// }
// weightTable.addCell(new PdfPCell(new Phrase("Poid : ")));
// for (StepWeight stepWeight : formStep.getStepWeights()) {
// PdfPCell cell = new PdfPCell(new
// Phrase(Float.toString(stepWeight.getWeightValue())));
// cell.setPadding(Element.ALIGN_CENTER);
// weightTable.addCell(cell);
// }
// weightTable.setWidthPercentage(80);
// weightTable.setHorizontalAlignment(Element.ALIGN_CENTER);
// weightTable.setSpacingBefore(10);
// document.add(weightTable);
// int i = 1;
// for (FormQuestion formQuestion : formStep.getFormQuestions()) {
// Paragraph questionParagraph = new Paragraph(i +
// formQuestion.getQuestionText(), questionFont);
// Paragraph answerParagraph = new Paragraph(formQuestion.getAnswerText(),
// answerFont);
// document.add(questionParagraph);
// document.add(answerParagraph);
// PdfPTable criteriaTable = new PdfPTable(4);
// Phrase phrase = new Phrase("Critère : ");
// phrase.setFont(questionFont);
// criteriaTable.addCell(new PdfPCell(phrase));
// i++;
// for (Criteria questionCriteria : formQuestion.getCriterias()) {
// Phrase phraseToAdd = new Phrase(questionCriteria.getCriteriaName());
// phraseToAdd.setFont(questionFont);
// PdfPCell cell = new PdfPCell(phraseToAdd);
// cell.setPadding(Element.ALIGN_CENTER);
// criteriaTable.addCell(cell);
// }
// criteriaTable.addCell(new PdfPCell(new Phrase("Valeur : ")));
// for (Criteria questionCriteria : formQuestion.getCriterias()) {
// Phrase phraseToAdd = new Phrase(questionCriteria.getCriteriaValue());
// System.out.println("questionCriteria = " +
// questionCriteria.getCriteriaValue());
// phraseToAdd.setFont(questionFont);
// PdfPCell cell = new PdfPCell(phraseToAdd);
// cell.setPadding(Element.ALIGN_CENTER);
// criteriaTable.addCell(cell);
// }
// criteriaTable.setWidthPercentage(80);
// criteriaTable.setHorizontalAlignment(Element.ALIGN_CENTER);
// criteriaTable.setSpacingBefore(10);
// document.add(criteriaTable);
// }
// PdfPTable scoreTable = new PdfPTable(3);
//
// for(String key: stepScores.keySet()){
// Phrase phraseToWrite = new Phrase(key);
// phraseToWrite.setFont(questionFont);
// PdfPCell cell = new PdfPCell(phraseToWrite);
// cell.setHorizontalAlignment(Element.ALIGN_CENTER);
// scoreTable.addCell(cell);
// }
// for(String key: stepScores.keySet()){
// Phrase phraseToWrite = new Phrase(Float.toString(stepScores.get(key)));
// phraseToWrite.setFont(questionFont);
// PdfPCell cell = new PdfPCell(phraseToWrite);
// cell.setHorizontalAlignment(Element.ALIGN_CENTER);
// scoreTable.addCell(cell);
// }
// scoreTable.setSpacingBefore(15);
// document.add(scoreTable);
// }
// }
