
package com.elit.outildedecision.service;

import com.elit.outildedecision.entity.FormStep;
import com.elit.outildedecision.exception.EscoreException;
import com.elit.outildedecision.exception.MulticriteriaNotFoundException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.QuestionsNotFoundException;
import com.elit.outildedecision.exception.UserNotAuthorizedException;
import com.elit.outildedecision.model.FormQuestionModel;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
/**
 * This interface defines the contract for the Multi-Criteria Analysis Service, which provides methods to create,
 * retrieve, update, and calculate scores for multi-criteria analyses.
 */
public interface MultiCriteriaAnalysisService {

  /**
   * Creates a new multi-criteria analysis form for the specified project.
   *
   * @param projectId the ID of the project for which to create the form
   * @return a ResponseEntity indicating the success or failure of the create operation
   * @throws IOException if an I/O error occurs while reading the configuration file
   * @throws ParseException if a parse error occurs while reading the configuration file
   * @throws org.json.simple.parser.ParseException if a parse error occurs while reading the configuration file
   */
  ResponseEntity<String> createMulticriteriaAnalysisForm(Long projectId)
    throws IOException, ParseException, org.json.simple.parser.ParseException;

  /**
   * Retrieves the multi-criteria analysis form for the specified project.
   *
   * @param projectId the ID of the project for which to retrieve the form
   * @return a ResponseEntity containing the multi-criteria analysis form
   * @throws ProjectNotFoundException if the specified project does not exist
   */
  ResponseEntity<Object> getMulticriteriaAnalysis(Long projectId) throws ProjectNotFoundException;

  /**
   * Updates the multi-criteria analysis form for the specified project.
   *
   * @param projectId the ID of the project for which to update the form
   * @param multiCriteriaAnalysis a FormStep object representing the updated form
   * @return a ResponseEntity indicating the success or failure of the update operation
   */
  ResponseEntity<String> updateMultiCriteriaAnalysis(
    Long projectId, FormStep multiCriteriaAnalysis);

  /**
   * Updates an existing multi-criteria analysis question.
   *
   * @param formQuestion a FormQuestionModel object representing the question to update
   * @return a ResponseEntity indicating the success or failure of the update operation
   * @throws MulticriteriaNotFoundException if the specified multi-criteria analysis does not exist
   */
  ResponseEntity<String> updateMultiCriteriaQuestion(FormQuestionModel formQuestion)
    throws MulticriteriaNotFoundException;

  /**
   * Calculates the score for the specified multi-criteria analysis.
   *
   * @param projectId the ID of the project for which to calculate the score
   * @return a ResponseEntity containing the calculated score
   * @throws ProjectNotFoundException if the specified project does not exist
   * @throws QuestionsNotFoundException if any of the questions associated with the multi-criteria analysis do not exist
   * @throws EscoreException if an error occurs while calculating the score
   */
  ResponseEntity calculateMultiCriteriaAnalysisScore(Long projectId)
    throws ProjectNotFoundException, QuestionsNotFoundException, EscoreException;

  /**
   * Increases the iteration count for the specified multi-criteria analysis.
   *
   * @param analyseId the ID of the multi-criteria analysis for which to increase the iteration count
   * @return a ResponseEntity indicating the success or failure of the operation
   * @throws MulticriteriaNotFoundException if the specified multi-criteria analysis does not exist
   * @throws UserNotAuthorizedException if the user is not authorized to update the specified multi-criteria analysis
   */
  ResponseEntity<String> increaseMulticriteriaIteration(Long analyseId)
    throws MulticriteriaNotFoundException, UserNotAuthorizedException;
}
